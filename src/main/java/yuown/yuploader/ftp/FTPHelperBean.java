package yuown.yuploader.ftp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.table.DefaultTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class FTPHelperBean {

	@Value("${ftp.conn.user}")
	private String ftpUsername;

	@Value("${ftp.conn.pwd}")
	private String ftpPassword;

	@Value("${ftp.conn.host}")
	private String ftpHost;

	@Value("${ftp.conn.port}")
	private int ftpPort;

	@Value("${ftp.conn.path}")
	private String ftpPath;

	@Autowired()
	private DirectChannel ftpChannel;

	private String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

	private String userName;
	
	public void uploadFile(String fileName, final DefaultTableModel tM, final int row, final int column) {
		Message<File> fileMessage = MessageBuilder
				.withPayload(new File(fileName))
				.setHeader("path", ftpPath)
				.setHeader("user", userName)
				.setHeader("date", date).build();
		ftpChannel.send(fileMessage);
	}

	public String getFtpUsername() {
		return ftpUsername;
	}

	public void setFtpUsername(String ftpUsername) {
		this.ftpUsername = ftpUsername;
	}

	public String getFtpPassword() {
		return ftpPassword;
	}

	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
	}

	public String getFtpHost() {
		return ftpHost;
	}

	public void setFtpHost(String ftpHost) {
		this.ftpHost = ftpHost;
	}

	public void setFtpPort(int port) {
		this.ftpPort = port;
	}

	public int getFtpPort() {
		return ftpPort;
	}

	public void setFtpPath(String path) {
		this.ftpPath = path;
	}

	public String getFtpPath() {
		return ftpPath;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
