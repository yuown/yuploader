package yuown.yuploader.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.SwingWorker;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import yuown.yuploader.model.FileObject;
import yuown.yuploader.model.Status;
import yuown.yuploader.model.YuploaderTableModel;
import yuown.yuploader.ui.Client;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class QueueUpload extends SwingWorker<Integer, Integer> {

    @Autowired
    private YuploaderTableModel yuploaderTableModel;

    @Autowired
    private FTPClient ftpClient;

    @Autowired
    private Client client;

    private boolean paused = false;
    
    @Autowired
    private StreamListener streamListener;

    private AutowireCapableBeanFactory aw;

	private int start = 0;

    public void submitToQueue() {
        client.setProgress(true);
        int rowCount = yuploaderTableModel.getRowCount();
        for (int i = start ; i < rowCount; i++) {
            final int row = i;
            final FileObject fileObject = (FileObject) yuploaderTableModel.getValueAt(row, 0);
            File f = new File(fileObject.getFullPath());
            try {
                if ((f.exists()) && !StringUtils.equals(Status.COMPLETED.toString(), fileObject.getStatus().toString())) {
                    yuploaderTableModel.setValueAt(Status.IN_PROGRESS, row, 3);

                    OutputStream dest = null;
                    if(fileObject.getOffset() > 0) {
                        dest = this.ftpClient.appendFileStream(fileObject.getFileName());
                    } else {
                        dest = this.ftpClient.storeFileStream(fileObject.getFileName());
                    }
                    streamListener.setRow(i);
                    InputStream source = new FileInputStream(f);
                    boolean flush = true;
                    int bytes;
                    long total = 0;
                    byte[] buffer = new byte[1024];

                    try {
                        while ((bytes = source.read(buffer)) != -1) {
                            if (total >= fileObject.getOffset()) {
                                dest.write(buffer, 0, bytes);
                            }
                            if (flush) {
                                dest.flush();
                            }
                            total += bytes;
                            if (total - bytes >= fileObject.getOffset()) {
                                boolean paused = streamListener.bytesTransferred(total, bytes);
                                if(paused) {
                                	client.hidePause(true);
                                	client.setLastAccess(System.currentTimeMillis());
                                	client.setStart(i);
                                	return;
                                }
                            }
                        }
                    } catch (IOException e) {
                        throw new CopyStreamException("IOException caught while copying.", total, e);
                    } finally {
                        source.close();
                        if(dest != null) {
                        	dest.close();
                        }
                        this.ftpClient.completePendingCommand();
                    }
                }
			} catch (Exception e1) {
				fileObject.setOffset(0);
				i--;
				try {
					this.ftpClient.completePendingCommand();
				} catch (IOException e) {
					e.printStackTrace();
				}
				e1.printStackTrace();
			}
        }
        client.hidePause(true);
        client.setLastAccess(System.currentTimeMillis());
        client.setProgress(false);
        client.setStart(0);
        return;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        System.out.println("1. Client: " + client.hashCode());
        submitToQueue();
        return 0;
    }

    public void setAutoWireCapableBeanFactory(AutowireCapableBeanFactory aw) {
        this.aw = aw;
    }

	public void setStart(int start2) {
		this.start = start2;
	}
}
