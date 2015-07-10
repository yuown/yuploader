package yuown.yuploader.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.SocketException;
import java.nio.channels.Channels;

import javax.swing.SwingWorker;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamListener;
import org.apache.commons.net.io.SocketOutputStream;
import org.apache.commons.net.io.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import yuown.yuploader.model.FileObject;
import yuown.yuploader.model.Status;
import yuown.yuploader.model.YuploaderTableModel;
import yuown.yuploader.ui.Client;
import yuown.yuploader.util.Helper;

@Component
@Scope("prototype")
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
	private int bufferSize = 1024;
	private boolean flush = true;

	public void submitToQueue() {
		this.client.setInProgress(true);
		System.out.println("Uploader Worker Started: " + System.currentTimeMillis());

		this.streamListener.setWorker(this);

		int rowCount = this.yuploaderTableModel.getRowCount();
		for (int row = 0; row < rowCount; row++) {
			FileObject file = (FileObject) this.yuploaderTableModel.getValueAt(row, 0);
			File f = new File(file.getFullPath());
			if ((f.exists()) && (isEligibleToStartUpload(file))) {
				System.out.println("File: " + file.getFullPath());
				markUploadInProgress(file, row);
				SocketOutputStream writer = getWriter(file);
				if (writer != null) {
					InputStream reader = getReader(f, file);
					try {
						copyStream(reader, writer, this.bufferSize, -1L, this.streamListener, this.flush, file.getOffset());
					} catch (SocketException s) {
						s.printStackTrace();
						helper.alert(client, "Check your Network Connection and Restart Upload!");
						client.setConnected(false);
						break;
					} catch (IOException e) {
						e.printStackTrace();
					}
					Util.closeQuietly(writer);
					Util.closeQuietly(reader);
					try {
						this.ftpClient.completePendingCommand();
						System.out.println("Finished Uploading File: " + file.getFullPath());
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					client.setConnected(false);
					break;
				}
			}
		}
		this.client.hidePause(true);
		this.client.setLastAccess(System.currentTimeMillis());
		this.client.setInProgress(false);
	}

	private SocketOutputStream getWriter(FileObject file) {
		SocketOutputStream writer = null;
		try {
			if (file.getOffset() > 0L) {
				try {
					System.out.println("Start Resume file: " + file.getFullPath());
					writer = (SocketOutputStream) this.ftpClient.appendFileStream(file.getFileName());
				} catch(SocketException se) {
					System.out.println("Failed to Connect to FRP Server to get Output Stream!");
					se.printStackTrace();
					helper.alert(client, "Check your Network Connection and Restart Upload!");
					return null;
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Resume Failed, Start Uploading From Beginning: " + file.getFullPath());
					try {
						writer = (SocketOutputStream) this.ftpClient.storeFileStream(file.getFileName());
						file.setOffset(0L);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			} else {
				try {
					writer = (SocketOutputStream) this.ftpClient.storeFileStream(file.getFileName());
					file.setOffset(0L);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return writer;
	}

	@SuppressWarnings("resource")
	protected InputStream getReader(File f, FileObject file) {
		InputStream reader = null;
		try {
			if (file.getOffset() > 0L) {
				try {
					System.out.println("Start Resume file: " + file.getFullPath());
					RandomAccessFile raf = new RandomAccessFile(f, "r");
					reader = Channels.newInputStream(raf.getChannel().position(file.getOffset()));
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Resume Failed, Start Uploading From Beginning: " + file.getFullPath());
					try {
						reader = new FileInputStream(f);
						file.setOffset(0L);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			} else {
				try {
					reader = new FileInputStream(f);
					file.setOffset(0L);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reader;
	}

	private void markUploadInProgress(FileObject file, int row) {
		this.yuploaderTableModel.setValueAt(Status.IN_PROGRESS, row, 3);
		file.setStatus(Status.IN_PROGRESS);
		this.streamListener.setRow(row);
	}

	protected boolean isEligibleToStartUpload(FileObject file) {
		return !StringUtils.equals(Status.COMPLETED.toString(), file.getStatus().toString());
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	protected Integer doInBackground() throws Exception {
		submitToQueue();
		return Integer.valueOf(0);
	}

	protected long copyStream(InputStream source, OutputStream dest, int bufferSize, long streamSize, CopyStreamListener listener, boolean flush, long totalTillNow) throws IOException,
			SocketException {
		long total = totalTillNow;
		byte[] buffer = new byte[bufferSize];
		int bytes;
		System.out.println("Starting to Transfer.......");
		while ((bytes = source.read(buffer)) != -1) {
			System.out.print(bytes + " .. ");
			if (bytes == 0) {
				bytes = source.read();
				if (bytes >= 0) {
					dest.write(bytes);
					if (flush) {
						dest.flush();
					}
					total += 1L;
					if (listener != null) {
						listener.bytesTransferred(total, 1, streamSize);
					}
				}
			} else {
				dest.write(buffer, 0, bytes);
				if (flush) {
					dest.flush();
				}
				total += bytes;
				if (listener != null) {
					listener.bytesTransferred(total, bytes, streamSize);
					if (this.paused) {
						break;
					}
				}
			}
		}
		return total;
	}
}
