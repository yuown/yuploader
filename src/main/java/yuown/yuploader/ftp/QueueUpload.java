package yuown.yuploader.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;

import javax.swing.SwingWorker;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import yuown.yuploader.model.FileObject;
import yuown.yuploader.model.Status;
import yuown.yuploader.model.User;
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

	@Value("${ftp.conn.host}")
	private String ftpHost;

	@Value("${ftp.conn.user}")
	private String ftpUsername;

	@Value("${ftp.conn.pass}")
	private String ftpPassword;

	@Value("${ftp.conn.port}")
	private int ftpPort;

	@Value("${ftp.bufferSize}")
	private int ftpBufferSize;

	@Value("${ftp.conn.basepath}")
	private String ftpBasePath;

	@Autowired
	private User userObject;

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
				InputStream reader = getReader(f, file);
				try {
					streamListener.setTotalBytesTransferred(file.getOffset());
					if (file.getOffset() > 0) {
						ftpClient.appendFile(file.getFileName(), reader);
					} else {
						ftpClient.storeFile(file.getFileName(), reader);
					}
				} catch (IOException e) {
					e.printStackTrace();
					client.ftpLogout();
					client.setConnected(false);
					client.checkTimeoutAndConnect();
				} finally {
					Util.closeQuietly(reader);
				}
			}
		}
		this.client.hidePause(true);
		this.client.setInProgress(false);
	}

	private long getOffsetFromServer(FileObject file) throws IOException {
		long l = -1;
		FTPClient ftp = new FTPClient();
		ftp.connect(ftpHost);
		int reply = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			ftp.disconnect();
			System.err.println("FTP server refused connection.");
		} else {
			ftp.enterLocalPassiveMode();
			if (!ftp.login(ftpUsername, ftpPassword)) {
				System.out.println("Problem with FTP Server Credentials, Contact Admin.");
			} else {
				try {
					ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
					ftp.setAutodetectUTF8(true);
					ftp.setFileType(FTP.BINARY_FILE_TYPE);
					ftp.setBufferSize(-1);
					ftp.setFileTransferMode(2);
					ftp.configure(new FTPClientConfig(FTPClientConfig.SYST_L8));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
		String DDMMYYYY = helper.getDateDDMMYYYY();
		boolean exists = false;
		try {
			exists = ftp.changeWorkingDirectory(ftpBasePath);
			if (!exists) {
				ftp.makeDirectory(ftpBasePath);
				ftp.changeWorkingDirectory(ftpBasePath);
			}
			exists = ftp.changeWorkingDirectory(userObject.getUname());
			if (!exists) {
				ftp.makeDirectory(userObject.getUname());
				ftp.changeWorkingDirectory(userObject.getUname());
			}
			exists = ftp.changeWorkingDirectory(DDMMYYYY);
			if (!exists) {
				ftp.makeDirectory(DDMMYYYY);
				ftp.changeWorkingDirectory(DDMMYYYY);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		FTPFile[] ftpFile = ftp.listFiles();
		for (int i = 0; i < ftpFile.length; i++) {
			if (StringUtils.equals(ftpFile[i].getName(), file.getFileName())) {
				l = ftpFile[i].getSize();
				break;
			}
		}
		try {
			ftp.logout();
			ftp.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (l < 0) {
			l = 0;
		}
		return l;
	}

	@SuppressWarnings("resource")
	protected InputStream getReader(File f, FileObject file) {
		InputStream reader = null;
		try {
			// client.toggleLoginCtrls(false);
			if (file.getOffset() > 0L) {
				try {
					System.out.println("Start Resume file: " + file.getFullPath());
					file.setOffset(getOffsetFromServer(file));
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
		// client.toggleLoginCtrls(true);
		return reader;
	}

	private void markUploadInProgress(FileObject file, int row) {
		this.yuploaderTableModel.setValueAt(Status.IN_PROGRESS, row, 3);
		file.setStatus(Status.IN_PROGRESS);
		ftpClient.setCopyStreamListener(streamListener);
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
}
