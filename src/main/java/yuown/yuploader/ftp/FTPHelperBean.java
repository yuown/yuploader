package yuown.yuploader.ftp;

import org.springframework.beans.factory.annotation.Value;

public class FTPHelperBean {

    @Value("${ftp.conn.user}")
    private String ftpUsername;

    @Value("${ftp.conn.pwd}")
    private String ftpPassword;

    @Value("${ftp.conn.host}")
    private String ftpHost;

}
