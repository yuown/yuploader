package yuown.yuploader.util;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Base64Utils;

public class YuownUtils {

	public static final String SELECT_USER_QUERY = "SELECT * FROM users WHERE uname = ?";

	public static final String SELECT_FTP_DETAILS_QUERY = "SELECT * FROM settings WHERE name in (?, ?, ?, ?, ?, ?, ?)";

	public static final String FTP_USER = "ftp_user";

	public static final String FTP_PASSWORD = "ftp_password";

	public static final String FTP_PORT = "ftp_port";

	public static final String FTP_PATH = "ftp_path";

	public static final String FTP_HOST = "ftp_host";

	public static final String APP_VERSION = "app_version";

	public static final String NAME = "name";

	public static final String VALUE = "value";

	public static final String EMPTY_STRING = "";

	public static final String UPDATE_URL = "update_url";

	private static String ftpUserName;

	private static String ftpPassword;

	private static int ftpPort;

	private static String ftpPath;

	private static String ftpHost;

	private static double appVersion;

	private static String updateUrl;

	public static double longTo2Decimals(long input, long divide) {
		return (double) ((long) ((double) ((input * 100.0) / (divide * 100.0)) * 100)) / 100;
	}

	public static double longTo2Decimals(double input, long divide) {
		return (double) ((long) ((double) (input / divide) * 10)) / 10;
	}

	public static String getParentDirectoryName(String fileName) {
		String[] parts = StringUtils.split(fileName, File.separatorChar);
		String toReturn = EMPTY_STRING;
		if (null != parts && parts.length > 1) {
			toReturn = parts[parts.length - 2];
		}
		return toReturn;
	}

	public static String getFtpUserName() {
		return ftpUserName;
	}

	public static String getFtpPassword() {
		return ftpPassword;
	}

	public static int getFtpPort() {
		return ftpPort;
	}

	public static String getFtpPath() {
		return ftpPath;
	}

	public static String getFtpHost() {
		return ftpHost;
	}

	public static void setFtpUserName(String ftpUserName) {
		YuownUtils.ftpUserName = new String(Base64Utils.decodeFromString(ftpUserName));
	}

	public static void setFtpPassword(String ftpPassword) {
		YuownUtils.ftpPassword = new String(Base64Utils.decodeFromString(ftpPassword));
	}

	public static void setFtpPort(int ftpPort) {
		YuownUtils.ftpPort = ftpPort;
	}

	public static void setFtpPath(String ftpPath) {
		YuownUtils.ftpPath = ftpPath;
	}

	public static void setFtpHost(String ftpHost) {
		YuownUtils.ftpHost = new String(Base64Utils.decodeFromString(ftpHost));
	}

	public static void setAppVersion(double appVersion) {
		YuownUtils.appVersion = appVersion;
	}

	public static double getAppVersion() {
		return appVersion;
	}

	public static String getUpdateUrl() {
		return updateUrl;
	}

	public static void setUpdateUrl(String updateUrl) {
		YuownUtils.updateUrl = updateUrl;
	}
}
