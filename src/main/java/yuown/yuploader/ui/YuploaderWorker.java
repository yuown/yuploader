package yuown.yuploader.ui;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import javax.swing.JFrame;
import javax.swing.SwingWorker;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import yuown.yuploader.ftp.YuploadCopyStreamListener;
import yuown.yuploader.model.FileObject;
import yuown.yuploader.model.Status;
import yuown.yuploader.model.User;
import yuown.yuploader.model.YuploaderTableModel;
import yuown.yuploader.util.Helper;

@Component
public class YuploaderWorker extends SwingWorker<JFrame, Integer> {

	@Autowired
	private Client client;

	@Autowired
	private Helper helper;

	@Autowired
	private FTPClient ftpClient;

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

	@Value("${ftp.conn.timeout}")
	private long ftpTimeOut;

	@Autowired
	private User userObject;

	private long lastAccess = -1L;

	@Autowired
	private YuploaderTableModel yuploaderTableModel;

	@Autowired
	private YuploadCopyStreamListener yuploadCopyStreamListener;

	private int row;

	private boolean connected = false;

	private ApplicationContext context;

	private AutowireCapableBeanFactory aw;

	public YuploaderWorker() {
	}

	@Override
	protected JFrame doInBackground() throws Exception {
		System.out.println(ftpClient);
//		if (checkTimeoutAndConnect()) {
//			createMissingDirectories();
			markInProgress();
			startUpload();
//		}
		return client;
	}

	private void startUpload() {
		FileObject fo = (FileObject) yuploaderTableModel.getValueAt(row, 0);
		BufferedInputStream bis;
		try {
			File file = new File(fo.getFullPath());
			yuploadCopyStreamListener = aw.createBean(YuploadCopyStreamListener.class);
			bis = new BufferedInputStream(new FileInputStream(file));
			yuploadCopyStreamListener.setRow(row);
			ftpClient.setCopyStreamListener(yuploadCopyStreamListener);
			ftpClient.storeFile(fo.getFileName(), bis);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void markInProgress() {
		yuploaderTableModel.setValueAt(Status.IN_PROGRESS, row, 3);
	}

//	public void createMissingDirectories() {
//		String DDMMYYYY = helper.getDateDDMMYYYY();
//		boolean exists = false;
//		try {
//			exists = ftpClient.changeWorkingDirectory(ftpBasePath);
//			if (!exists) {
//				ftpClient.makeDirectory(ftpBasePath);
//				ftpClient.changeWorkingDirectory(ftpBasePath);
//			}
//			exists = ftpClient.changeWorkingDirectory(userObject.getUname());
//			if (!exists) {
//				ftpClient.makeDirectory(userObject.getUname());
//				ftpClient.changeWorkingDirectory(userObject.getUname());
//			}
//			exists = ftpClient.changeWorkingDirectory(DDMMYYYY);
//			if (!exists) {
//				ftpClient.makeDirectory(DDMMYYYY);
//				ftpClient.changeWorkingDirectory(DDMMYYYY);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

//	public boolean checkTimeoutAndConnect() {
//		if (!client.isConnected() && (isTimeout() || isBeginning())) {
//			try {
//				ftpClient.connect(ftpHost);
//				int reply = ftpClient.getReplyCode();
//				if (!FTPReply.isPositiveCompletion(reply)) {
//					ftpClient.disconnect();
//					System.err.println("FTP server refused connection.");
//				} else {
//					if (!ftpClient.login(ftpUsername, ftpPassword)) {
//						ftpClient.logout();
//						// helper.alert(client,
//						// "Problem with FTP Server Credentials, Contact Admin.");
//					} else {
//						try {
//							ftpClient.enterLocalPassiveMode();
//							ftpClient.setAutodetectUTF8(true);
//							ftpClient.setFileType(2, 2);
//							ftpClient.setBufferSize(ftpBufferSize);
////							ftpClient.setFileTransferMode(2);
//							client.setConnected(true);
//						} catch (Exception e1) {
//							e1.printStackTrace();
//						}
//					}
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		return connected;
//	}

	private boolean isBeginning() {
		return true;
	}

	private boolean isTimeout() {
		boolean timeout = false;
		if (System.currentTimeMillis() - this.lastAccess > ftpTimeOut) {
			System.out.println("Timeout Occured, hence Reconnecting!");
			timeout = true;
		} else {
			timeout = true;
			connected = false;
		}
		return timeout;
	}

	public void setRow(int i) {
		this.row = i;
	}

	public void setContext(ApplicationContext context) {
		this.context = context;
		aw = context.getAutowireCapableBeanFactory();
	}

}
