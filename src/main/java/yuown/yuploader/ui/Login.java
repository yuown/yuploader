package yuown.yuploader.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import yuown.yuploader.extract.UserMapper;
import yuown.yuploader.ftp.FTPHelperBean;
import yuown.yuploader.model.User;
import yuown.yuploader.util.Helper;
import yuown.yuploader.util.YuownUtils;

public class Login extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = -2444873343061728811L;

    private ApplicationContext context;

    private final JPanel loginPanel = new JPanel();
    private JTextField txtUserName;
    private JPasswordField txtPassword;

    private JdbcTemplate jdbcTemplate;
    private Helper helper;
    private FTPHelperBean ftpHelperBean;
    
    @Autowired
    private Client client;
    
    private Properties props = new Properties();

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            Login dialog = new Login();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog.
     */
    public Login() {
        setResizable(false);
        initialize();
        setTitle(props.getProperty("yuploader.app.title"));
        setBounds(100, 100, 700, 400);
        SpringLayout springLayout = new SpringLayout();
        getContentPane().setLayout(springLayout);
        loginPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(loginPanel);
        SpringLayout sl_loginPanel = new SpringLayout();
        loginPanel.setLayout(sl_loginPanel);

        JLabel lblUserName = new JLabel("User Name: ");
        sl_loginPanel.putConstraint(SpringLayout.NORTH, lblUserName, 10, SpringLayout.NORTH, loginPanel);
        sl_loginPanel.putConstraint(SpringLayout.WEST, lblUserName, 10, SpringLayout.WEST, loginPanel);
        loginPanel.add(lblUserName);

        JLabel lblPassword = new JLabel("Password: ");
        sl_loginPanel.putConstraint(SpringLayout.NORTH, lblPassword, 20, SpringLayout.SOUTH, lblUserName);
        sl_loginPanel.putConstraint(SpringLayout.WEST, lblPassword, 10, SpringLayout.WEST, loginPanel);
        sl_loginPanel.putConstraint(SpringLayout.EAST, lblPassword, 0, SpringLayout.EAST, lblUserName);
        loginPanel.add(lblPassword);

        txtUserName = new JTextField();
        sl_loginPanel.putConstraint(SpringLayout.NORTH, txtUserName, -5, SpringLayout.NORTH, lblUserName);
        sl_loginPanel.putConstraint(SpringLayout.WEST, txtUserName, 10, SpringLayout.EAST, lblUserName);
        sl_loginPanel.putConstraint(SpringLayout.EAST, txtUserName, -10, SpringLayout.EAST, loginPanel);
        loginPanel.add(txtUserName);
        txtUserName.setColumns(10);

        txtPassword = new JPasswordField();
        sl_loginPanel.putConstraint(SpringLayout.NORTH, txtPassword, -5, SpringLayout.NORTH, lblPassword);
        sl_loginPanel.putConstraint(SpringLayout.WEST, txtPassword, 0, SpringLayout.WEST, txtUserName);
        sl_loginPanel.putConstraint(SpringLayout.EAST, txtPassword, 0, SpringLayout.EAST, txtUserName);
        loginPanel.add(txtPassword);
        
        JPanel iconPanel = new JPanel();
        springLayout.putConstraint(SpringLayout.NORTH, loginPanel, 6, SpringLayout.SOUTH, iconPanel);
        springLayout.putConstraint(SpringLayout.WEST, loginPanel, 0, SpringLayout.WEST, iconPanel);
        springLayout.putConstraint(SpringLayout.EAST, loginPanel, 0, SpringLayout.EAST, iconPanel);
        
        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		login(e);
        	}
        });
        sl_loginPanel.putConstraint(SpringLayout.WEST, btnLogin, 0, SpringLayout.WEST, lblUserName);
        sl_loginPanel.putConstraint(SpringLayout.SOUTH, btnLogin, -10, SpringLayout.SOUTH, loginPanel);
        loginPanel.add(btnLogin);
        getRootPane().setDefaultButton(btnLogin);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        JButton btnCancel = new JButton("Cancel");
        sl_loginPanel.putConstraint(SpringLayout.WEST, btnCancel, 20, SpringLayout.EAST, btnLogin);
        sl_loginPanel.putConstraint(SpringLayout.SOUTH, btnCancel, 0, SpringLayout.SOUTH, btnLogin);
        loginPanel.add(btnCancel);
        springLayout.putConstraint(SpringLayout.NORTH, iconPanel, 10, SpringLayout.NORTH, getContentPane());
        springLayout.putConstraint(SpringLayout.WEST, iconPanel, 10, SpringLayout.WEST, getContentPane());
        springLayout.putConstraint(SpringLayout.SOUTH, iconPanel, 180, SpringLayout.NORTH, getContentPane());
        springLayout.putConstraint(SpringLayout.EAST, iconPanel, -10, SpringLayout.EAST, getContentPane());
        getContentPane().add(iconPanel);
        SpringLayout sl_iconPanel = new SpringLayout();
        iconPanel.setLayout(sl_iconPanel);
        
        JLabel forIcon = new JLabel("For Icon");
        try {
			BufferedImage logo = ImageIO.read(getClass().getResource("/images/vvv.png"));
			forIcon = new JLabel(new ImageIcon(logo));
			sl_iconPanel.putConstraint(SpringLayout.NORTH, forIcon, 0, SpringLayout.NORTH, iconPanel);
	        sl_iconPanel.putConstraint(SpringLayout.WEST, forIcon, 0, SpringLayout.WEST, iconPanel);
	        sl_iconPanel.putConstraint(SpringLayout.SOUTH, forIcon, 160, SpringLayout.NORTH, iconPanel);
	        sl_iconPanel.putConstraint(SpringLayout.EAST, forIcon, 217, SpringLayout.WEST, iconPanel);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        iconPanel.add(forIcon);
        
        JLabel lblHeader = new JLabel(props.getProperty("help.header"));
        sl_iconPanel.putConstraint(SpringLayout.NORTH, lblHeader, 5, SpringLayout.NORTH, iconPanel);
        sl_iconPanel.putConstraint(SpringLayout.WEST, lblHeader, 5, SpringLayout.EAST, forIcon);
        sl_iconPanel.putConstraint(SpringLayout.EAST, lblHeader, -5, SpringLayout.EAST, iconPanel);
        iconPanel.add(lblHeader);
        
        JLabel lblForSite = new JLabel("Website: ");
        sl_iconPanel.putConstraint(SpringLayout.NORTH, lblForSite, 5, SpringLayout.SOUTH, lblHeader);
        sl_iconPanel.putConstraint(SpringLayout.WEST, lblForSite, 5, SpringLayout.EAST, forIcon);
        iconPanel.add(lblForSite);
        
        JLabel lblForMobile = new JLabel("Mobile: ");
        sl_iconPanel.putConstraint(SpringLayout.NORTH, lblForMobile, 5, SpringLayout.SOUTH, lblForSite);
        sl_iconPanel.putConstraint(SpringLayout.WEST, lblForMobile, 5, SpringLayout.EAST, forIcon);
        iconPanel.add(lblForMobile);
        
        JLabel lblSite = new JLabel(props.getProperty("help.site"));
        sl_iconPanel.putConstraint(SpringLayout.NORTH, lblSite, 0, SpringLayout.NORTH, lblForSite);
        sl_iconPanel.putConstraint(SpringLayout.WEST, lblSite, 10, SpringLayout.EAST, lblForSite);
        sl_iconPanel.putConstraint(SpringLayout.EAST, lblSite, 0, SpringLayout.EAST, lblHeader);
        iconPanel.add(lblSite);
        
        JLabel lblNumber = new JLabel(props.getProperty("help.mobile"));
        sl_iconPanel.putConstraint(SpringLayout.NORTH, lblNumber, 0, SpringLayout.NORTH, lblForMobile);
        sl_iconPanel.putConstraint(SpringLayout.WEST, lblNumber, 0, SpringLayout.WEST, lblSite);
        sl_iconPanel.putConstraint(SpringLayout.EAST, lblNumber, 0, SpringLayout.EAST, lblHeader);
        iconPanel.add(lblNumber);
        
        JPanel statusPanel = new JPanel();
        springLayout.putConstraint(SpringLayout.SOUTH, loginPanel, -10, SpringLayout.NORTH, statusPanel);
        springLayout.putConstraint(SpringLayout.NORTH, statusPanel, -35, SpringLayout.SOUTH, getContentPane());
        springLayout.putConstraint(SpringLayout.WEST, statusPanel, 10, SpringLayout.WEST, getContentPane());
        springLayout.putConstraint(SpringLayout.SOUTH, statusPanel, -5, SpringLayout.SOUTH, getContentPane());
        springLayout.putConstraint(SpringLayout.EAST, statusPanel, -10, SpringLayout.EAST, getContentPane());
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        getContentPane().add(statusPanel);
        SpringLayout sl_statusPanel = new SpringLayout();
        statusPanel.setLayout(sl_statusPanel);
        
        JLabel lblDeveloper = new JLabel("Developer: ");
        sl_statusPanel.putConstraint(SpringLayout.NORTH, lblDeveloper, 7, SpringLayout.NORTH, statusPanel);
        sl_statusPanel.putConstraint(SpringLayout.WEST, lblDeveloper, 2, SpringLayout.WEST, statusPanel);
        statusPanel.add(lblDeveloper);
        
        JLabel lblDevsitecom = new JLabel(props.getProperty("developer.email"));
        sl_statusPanel.putConstraint(SpringLayout.NORTH, lblDevsitecom, 7, SpringLayout.NORTH, statusPanel);
        sl_statusPanel.putConstraint(SpringLayout.WEST, lblDevsitecom, 5, SpringLayout.EAST, lblDeveloper);
        statusPanel.add(lblDevsitecom);
        
        JLabel lblAppVersion = new JLabel(props.getProperty("app.version"));
        sl_statusPanel.putConstraint(SpringLayout.NORTH, lblAppVersion, 7, SpringLayout.NORTH, statusPanel);
        sl_statusPanel.putConstraint(SpringLayout.EAST, lblAppVersion, -10, SpringLayout.EAST, statusPanel);
        statusPanel.add(lblAppVersion);

    }

    //    private boolean getFTPConfiguration() {
    //    	List<Config> config = jdbcTemplate.query(YuownUtils.SELECT_FTP_DETAILS_QUERY, new String[] { YuownUtils.FTP_USER, YuownUtils.FTP_PASSWORD, YuownUtils.FTP_PORT, YuownUtils.FTP_PATH, YuownUtils.FTP_HOST }, new ConfigMapper());
    //    	boolean retrievedConfig = false;
    //    	if(config.size() == 5) {
    //    		for (Config eachConfig : config) {
    //    			String configName = eachConfig.getName();
    //				if(StringUtils.equalsIgnoreCase(YuownUtils.FTP_USER, configName)) {
    //					ftpHelperBean.setFtpUsername(eachConfig.getValue());
    //				} else if(StringUtils.equalsIgnoreCase(YuownUtils.FTP_PASSWORD, configName)) {
    //					ftpHelperBean.setFtpPassword(eachConfig.getValue());
    //				} else if(StringUtils.equalsIgnoreCase(YuownUtils.FTP_PORT, configName)) {
    //					int port = 21;
    //					try {
    //						port = Integer.parseInt(eachConfig.getValue());
    //					} catch(Exception e) {}
    //					ftpHelperBean.setFtpPort(port);
    //				} else if(StringUtils.equalsIgnoreCase(YuownUtils.FTP_PATH, configName)) {
    //					ftpHelperBean.setFtpPath(eachConfig.getValue());
    //				} else if(StringUtils.equalsIgnoreCase(YuownUtils.FTP_HOST, configName)) {
    //					ftpHelperBean.setFtpHost(eachConfig.getValue());
    //				}
    //			}
    //    	}
    //    	return retrievedConfig;
    //	}

	protected void login(ActionEvent e) {
		String user = txtUserName.getText();
		String passwd = new String(txtPassword.getPassword());
		if (StringUtils.isNotBlank(user) && StringUtils.isNotBlank(passwd)) {
			try {
				List<User> users = jdbcTemplate.query(YuownUtils.SELECT_USER_QUERY, new String[] { txtUserName.getText() }, new UserMapper());
				if (!users.isEmpty()) {
					User userEntity = users.get(0);
					if (StringUtils.equalsIgnoreCase(userEntity.getUname(), user) && StringUtils.equals(userEntity.getPasswd(), passwd)) {
						if (!userEntity.isEnabled()) {
							helper.alert(this, "Your User is Disabled, Please Contact Administrator");
						} else {
							ftpHelperBean.setUser(userEntity);
							launchApp();
							this.setVisible(false);
						}
					} else {
						helper.alert(this, "Your Password is wrong, Please check");
					}
				} else {
					helper.alert(this, "Your Username is wrong, Please check");
				}
			} catch (CannotGetJdbcConnectionException cgjdbcexp) {
				helper.alert(this, "Failed to Connect to Database Server, Please Contact Administrator");
				cgjdbcexp.printStackTrace();
			}
		} else {
			helper.alert(this, "Username and Password are mandatory, Please Enter");
		}
	}

	private void launchApp() {
		client.setVisible(true);
		setVisible(false);
    }

    private void initialize() {
        context = new ClassPathXmlApplicationContext(new String[] { "yuploader.xml" });
        jdbcTemplate = context.getBean("jdbcTemplate", JdbcTemplate.class);
        helper = context.getBean("helper", Helper.class);
        ftpHelperBean = context.getBean("ftpHelperBean", FTPHelperBean.class);
//        client = context.getBean("client", Client.class);
        
        AutowireCapableBeanFactory aw = context.getAutowireCapableBeanFactory();
        client = aw.createBean(Client.class);
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("yuploader.properties");
        
		if (inputStream != null) {
			try {
				props.load(inputStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }
}
