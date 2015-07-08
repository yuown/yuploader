package yuown.yuploader.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.BindException;

import javax.swing.SwingWorker;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import yuown.yuploader.model.FileObject;
import yuown.yuploader.model.Status;
import yuown.yuploader.model.YuploaderTableModel;
import yuown.yuploader.ui.Client;
import yuown.yuploader.util.Helper;

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
	
	@Autowired
	private Helper helper;

	private int start = 0;

	public void submitToQueue() {
		int rowCount = yuploaderTableModel.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			final int row = i;
			final FileObject fileObject = (FileObject) yuploaderTableModel.getValueAt(row, 0);
			File f = new File(fileObject.getFullPath());
			try {
				if ((f.exists()) && !StringUtils.equals(Status.COMPLETED.toString(), fileObject.getStatus().toString())) {
					yuploaderTableModel.setValueAt(Status.IN_PROGRESS, row, 3);
					fileObject.setStatus(Status.IN_PROGRESS);
					OutputStream dest = null;
					if (fileObject.getOffset() > 0) {
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
						while (!paused && (bytes = source.read(buffer)) != -1) {
							if (total >= fileObject.getOffset()) {
								dest.write(buffer, 0, bytes);
							}
							if (flush) {
								dest.flush();
							}
							total += bytes;
							if (total - bytes >= fileObject.getOffset()) {
								paused = streamListener.bytesTransferred(total, bytes);
								if (paused) {
									client.hidePause(paused);
									client.setLastAccess(System.currentTimeMillis());
									client.setStart(i);
									return;
								}
							}
						}
					} catch (Exception e) {
						if(ftpClient.isConnected()) {
							try {
								ftpClient.logout();
								ftpClient.disconnect();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
						client.setConnected(false);
						client.checkTimeoutAndConnect();
						e.printStackTrace();
					} finally {
						source.close();
						if (dest != null) {
							dest.close();
						}
						this.ftpClient.completePendingCommand();
					}
				}
			} catch(BindException be) {
				// In case of Network Disconnected
				if(ftpClient.isConnected()) {
					try {
						ftpClient.logout();
						ftpClient.disconnect();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				client.setConnected(false);
				client.checkTimeoutAndConnect();
				be.printStackTrace();
				i--;
			} catch(FTPConnectionClosedException fex) {
				helper.alert(client, "Check your Network connectivity, looks like you are not connected to Internet!");
				client.hidePause(false);
				client.setLastAccess(System.currentTimeMillis());
				return;
				
			} catch (Exception e1) {
				e1.printStackTrace();
				i--;
			}
		}
		client.hidePause(true);
		client.setLastAccess(System.currentTimeMillis());
		client.setStart(0);
		return;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	@Override
	protected Integer doInBackground() throws Exception {
		submitToQueue();
		return 0;
	}

	public void setStart(int start) {
		this.start = start;
	}
}
