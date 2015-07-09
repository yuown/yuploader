package yuown.yuploader.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;

import javax.swing.SwingWorker;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamException;
import org.apache.commons.net.io.CopyStreamListener;
import org.apache.commons.net.io.SocketOutputStream;
import org.apache.commons.net.io.Util;
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

	private int bufferSize = 1024;

	private boolean flush = true;

	public void submitToQueue() {
		System.out.println("Uploader Worker Started: " + System.currentTimeMillis());
		
		streamListener.setWorker(this);
		
		int rowCount = yuploaderTableModel.getRowCount();

		for (int row = 0; row < rowCount; row++) {
			final FileObject file = (FileObject) yuploaderTableModel.getValueAt(row, 0);
			File f = new File(file.getFullPath());
			if ((f.exists()) && isEligibleToStartUpload(file)) {
				System.out.println("File: " + file.getFullPath());
				markUploadInProgress(file, row);
				InputStream reader = null;
				SocketOutputStream writer = null;
				if (file.getOffset() > 0) {
					RandomAccessFile raf;
					try {
						System.out.println("Start Resume file: " + file.getFullPath());
						raf = new RandomAccessFile(f, "r");
						reader = Channels.newInputStream(raf.getChannel().position(file.getOffset()));
						writer = (SocketOutputStream) ftpClient.appendFileStream(file.getFileName());
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("Resume Failed, Start Uploading From Beginning: " + file.getFullPath());
						try {
							reader = getReader(f);
							writer = (SocketOutputStream) ftpClient.storeFileStream(file.getFileName());
							file.setOffset(0);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				} else {
					reader = getReader(f);
					try {
						System.out.println("Start a file: " + file.getFullPath());
						writer = (SocketOutputStream) ftpClient.storeFileStream(file.getFileName());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (writer != null) {
					try {
						copyStream(reader, writer, bufferSize, CopyStreamEvent.UNKNOWN_STREAM_SIZE, streamListener, flush, file.getOffset());
					} catch (Exception e) {
						e.printStackTrace();
					}
					Util.closeQuietly(writer);
					try {
						ftpClient.completePendingCommand();
						System.out.println("Finished Uploading File: " + file.getFullPath());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		client.hidePause(true);
		client.setLastAccess(System.currentTimeMillis());
		return;
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

	protected long copyStream(InputStream source, OutputStream dest, int bufferSize, long streamSize, CopyStreamListener listener, boolean flush, long totalTillNow) throws CopyStreamException {
		int bytes;
		long total = totalTillNow;
		byte[] buffer = new byte[bufferSize];

		try {
			while ((bytes = source.read(buffer)) != -1) {
				if (bytes == 0) {
					bytes = source.read();
					if (bytes < 0) {
						break;
					}
					dest.write(bytes);
					if (flush) {
						dest.flush();
					}
					++total;
					if (listener != null) {
						listener.bytesTransferred(total, 1, streamSize);
					}
					continue;
				}

				dest.write(buffer, 0, bytes);
				if (flush) {
					dest.flush();
				}
				total += bytes;
				if (listener != null) {
					listener.bytesTransferred(total, bytes, streamSize);
					if (paused) {
						break;
					}
				}
			}
		} catch (IOException e) {
			throw new CopyStreamException("IOException caught while copying.", total, e);
		}

		return total;
	}
}
