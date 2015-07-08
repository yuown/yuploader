package yuown.yuploader.ftp;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.BindException;

import javax.swing.SwingWorker;

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
	
	private boolean flush = true;

    private int bufferSize = 1024;

	public void submitToQueue() {
	    int rowCount = yuploaderTableModel.getRowCount();
		
	    for (int row = 0; row < rowCount; row++) {
	        final FileObject file = (FileObject) yuploaderTableModel.getValueAt(row, 0);
	        File f = new File(file.getFullPath());
	        if ((f.exists()) && isEligibleToStartUpload(file)) {
	            OutputStream writer = getWriter(file);
	            if(null != writer) {
	                markUploadInProgress(file, row);
	                InputStream reader = getReader(f);
	                int bytes;
                    long total = 0;
                    byte[] buffer = new byte[bufferSize];
                    while((bytes = readerData(reader, buffer)) != -1) {
                        if(reachedOffset(file, total)) {
                            try {
                                writer.write(buffer, 0, bytes);
                                total += bytes;
                                paused = streamListener.bytesTransferred(total, bytes);
                                if (paused) {
                                    client.hidePause(paused);
                                    client.setLastAccess(System.currentTimeMillis());
                                    return;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
	            }
	        }
	    }
		
		
		
		for (int i = 0; i < rowCount; i++) {
		    System.out.println("Current: " + i);
			final int row = i;
			final FileObject fileObject = (FileObject) yuploaderTableModel.getValueAt(row, 0);
			File f = new File(fileObject.getFullPath());
			try {
				if ((f.exists()) && isEligibleToStartUpload(fileObject)) {
					yuploaderTableModel.setValueAt(Status.IN_PROGRESS, row, 3);
					fileObject.setStatus(Status.IN_PROGRESS);
					OutputStream dest = null;
					System.out.println("Getting access to Output Stream");
					if (fileObject.getOffset() > 0) {
					    System.out.println("Getting access to Append");
						dest = this.ftpClient.appendFileStream(fileObject.getFileName());
					} else {
					    System.out.println("Getting access to New");
						dest = this.ftpClient.storeFileStream(fileObject.getFileName());
					}
					System.out.println("Got access to Output Stream");
					streamListener.setRow(i);
					InputStream source = getReader(f);
					boolean flush = true;
					int bytes;
					long total = 0;
					byte[] buffer = new byte[1024];

					try {
						while (!paused && (bytes = readerData(source, buffer)) != -1) {
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
		return;
	}

    private boolean reachedOffset(FileObject file, long total) {
        return total >= file.getOffset();
    }

    protected int readerData(InputStream reader, byte[] buffer) {
        int readSize = -1;
        try {
            readSize = reader.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return readSize;
    }

    protected FileInputStream getReader(File f) {
        FileInputStream reader = null;
        try {
            reader = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return reader;
    }

    private OutputStream getWriter(FileObject file) {
        OutputStream dest = null;
        try {
            if (file.getOffset() > 0) {
                dest = ftpClient.appendFileStream(file.getFileName());
            } else {
                dest = ftpClient.storeFileStream(file.getFileName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dest;
    }

    private void markUploadInProgress(FileObject file, int row) {
        yuploaderTableModel.setValueAt(Status.IN_PROGRESS, row, 3);
        file.setStatus(Status.IN_PROGRESS);
        streamListener.setRow(row);
    }

    protected boolean isEligibleToStartUpload(final FileObject file) {
        return !StringUtils.equals(Status.COMPLETED.toString(), file.getStatus().toString());
    }

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	@Override
	protected Integer doInBackground() throws Exception {
		submitToQueue();
		return 0;
	}
}
