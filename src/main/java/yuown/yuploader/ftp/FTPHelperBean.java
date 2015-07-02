package yuown.yuploader.ftp;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import yuown.yuploader.model.User;

@Component
@Scope("singleton")
public class FTPHelperBean {

	private String ftpUserName;

	private String ftpPassword;

	private String ftpHost;

	private int ftpPort;

	private String ftpBasePath;

	private int clientMode;

	private int fileType;

	private int bufferSize;

	private String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

	private User user;

	private int currentRow;

	public String getFtpUserName() {
		return ftpUserName;
	}

	public String getFtpPassword() {
		return ftpPassword;
	}

	public String getFtpHost() {
		return ftpHost;
	}

	public int getFtpPort() {
		return ftpPort;
	}

	public String getFtpBasePath() {
		return ftpBasePath;
	}

	public int getClientMode() {
		return clientMode;
	}

	public int getFileType() {
		return fileType;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public String getDate() {
		return date;
	}

	public User getUser() {
		return user;
	}

	public int getCurrentRow() {
		return currentRow;
	}

	public void setFtpUserName(String ftpUserName) {
		this.ftpUserName = ftpUserName;
	}

	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
	}

	public void setFtpHost(String ftpHost) {
		this.ftpHost = ftpHost;
	}

	public void setFtpPort(int ftpPort) {
		this.ftpPort = ftpPort;
	}

	public void setFtpBasePath(String ftpBasePath) {
		this.ftpBasePath = ftpBasePath;
	}

	public void setClientMode(int clientMode) {
		this.clientMode = clientMode;
	}

	public void setFileType(int fileType) {
		this.fileType = fileType;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setCurrentRow(int currentRow) {
		this.currentRow = currentRow;
	}
}
