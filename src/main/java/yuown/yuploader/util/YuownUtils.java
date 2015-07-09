package yuown.yuploader.util;

public class YuownUtils {

	public static final String SELECT_USER_QUERY = "SELECT * FROM users WHERE uname = ?";

	public static final String SELECT_FTP_DETAILS_QUERY = "SELECT * FROM settings WHERE name in (?, ?, ?, ?, ?)";

	public static final String FTP_USER = "ftp_user";

	public static final String FTP_PASSWORD = "ftp_password";

	public static final String FTP_PORT = "ftp_port";

	public static final String FTP_PATH = "ftp_path";

	public static final String FTP_HOST = "ftp_host";

	public static final String NAME = "name";

	public static final String VALUE = "value";

	public static double longTo2Decimals(long input, long divide) {
		return (double) ((long) ((double) ((input * 100.0) / (divide * 100.0)) * 100)) / 100;
	}

	public static double longTo2Decimals(double input, long divide) {
		return (double) ((long) ((double) (input / divide) * 10)) / 10;
	}

}
