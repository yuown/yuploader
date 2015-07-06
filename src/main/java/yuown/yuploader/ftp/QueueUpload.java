package yuown.yuploader.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.SwingWorker;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamException;
import org.apache.commons.net.io.CopyStreamListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import yuown.yuploader.model.FileObject;
import yuown.yuploader.model.Status;
import yuown.yuploader.model.YuploaderTableModel;
import yuown.yuploader.ui.Client;
import yuown.yuploader.util.YuownUtils;

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

	public void submitToQueue() {
		client.setProgress(true);
		int rowCount = yuploaderTableModel.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			final int row = i;
			final FileObject fileObject = (FileObject) yuploaderTableModel.getValueAt(row, 0);
			File f = new File(fileObject.getFullPath());
			try {
				if ((f.exists()) && !StringUtils.equals(Status.COMPLETED.toString(), fileObject.getStatus().toString())) {
					yuploaderTableModel.setValueAt(Status.IN_PROGRESS, row, 3);
					this.ftpClient.setCopyStreamListener(new CopyStreamListener() {});
//					FileInputStream fis = new FileInputStream(f);
//					BufferedInputStream bis = new BufferedInputStream(fis);
//					this.ftpClient.storeFile(fileObject.getFileName(), bis);
//					bis.close();
					
					
//					SocketOutputStream os = (SocketOutputStream) this.ftpClient.storeFileStream(fileObject.getFileName());
//					int bufferSize = ftpClient.getBufferSize();
//					byte[] array = new byte[bufferSize];
//					int read = 0;
//					while((read = fis.read(array, (int) fileObject.getOffset(), bufferSize)) != -1) {
//						System.out.println("Array: " + array.length);
//						os.write(array, (int) fileObject.getOffset(), bufferSize);
//						fileObject.setOffset(fileObject.getOffset() + read);
//					}
//					fis.close();
//					os.close();
//					ftpClient.completePendingCommand();
					
					
					OutputStream dest = this.ftpClient.storeFileStream(fileObject.getFileName());
					InputStream source = new FileInputStream(f);
					boolean flush = true;
			        int bytes;
			        long total = 0;
			        byte[] buffer = new byte[1024];

			        try
			        {
			            while ((bytes = source.read(buffer)) != -1)
			            {
			                // Technically, some read(byte[]) methods may return 0 and we cannot
			                // accept that as an indication of EOF.

							if (bytes == 0)
			                {
			                    bytes = source.read();
			                    if (bytes < 0) {
			                        break;
			                    }
			                    dest.write(bytes);
			                    if(flush) {
			                        dest.flush();
			                    }
			                    ++total;
			                    if (listener != null) {
			                        listener.bytesTransferred(total, 1, streamSize);
			                    }
			                    continue;
			                }

			                dest.write(buffer, 0, bytes);
			                if(flush) {
			                    dest.flush();
			                }
			                total += bytes;
			                if (listener != null) {
			                    listener.bytesTransferred(total, bytes, streamSize);
			                }
			            }
			        }
			        catch (IOException e)
			        {
			            throw new CopyStreamException("IOException caught while copying.",
			                                          total, e);
			        }

					
					
					fileObject.setStatus(Status.COMPLETED);
					yuploaderTableModel.setValueAt(Status.COMPLETED, row, 3);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		client.setLastAccess(System.currentTimeMillis());
		client.setProgress(false);
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
	
	class StreamListener {

		private long bytes = 0;
		private double percentCompleted;
		private long time = System.currentTimeMillis();
		private long start = System.currentTimeMillis();

		public void bytesTransferred(FileObject fileObject, int row, long totalBytesTransferred, int bytesTransferred, long streamSize) {
			client.toggleLoginCtrls(false);
			long s = fileObject.getSize();
			this.percentCompleted = ((int) ((totalBytesTransferred * 10.0D) / (s * 10.0D) * 10000.0D) / 100.0D);

			long d = System.currentTimeMillis() - this.time;
			if (d > 1000) {
				updateProgress(row);
				this.bytes = 0;
				this.time = System.currentTimeMillis();
			} else {
				this.bytes += bytesTransferred;
			}

			if (totalBytesTransferred == s) {
				updateProgress(row);
				client.toggleLoginCtrls(true);
			}
		}

		private void updateProgress(final int row) {
			double currentKBRate = YuownUtils.longTo2Decimals(this.bytes, 1024);
			if (currentKBRate < 1024) {
				yuploaderTableModel.setValueAt(currentKBRate + " KB/s", row, 4);
			} else {
				double currentMBRate = YuownUtils.longTo2Decimals(currentKBRate, 1024);
				yuploaderTableModel.setValueAt(currentMBRate + " MB/s", row, 4);
			}
			yuploaderTableModel.setValueAt(YuownUtils.longTo2Decimals(this.time - this.start, 1000), row, 5);
			yuploaderTableModel.setValueAt(this.percentCompleted + " %", row, 2);
		}
	
	}
}
