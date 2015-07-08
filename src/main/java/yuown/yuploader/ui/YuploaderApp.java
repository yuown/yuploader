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
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;

import yuown.yuploader.extract.UserMapper;
import yuown.yuploader.model.User;
import yuown.yuploader.util.Helper;
import yuown.yuploader.util.YuownUtils;

public class YuploaderApp extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = -2444873343061728811L;

    private ApplicationContext context;

    private final JPanel loginPanel = new JPanel();
    private JTextField txtUserName;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    private JdbcTemplate jdbcTemplate;
    private Helper helper;
    
    @Autowired
    private Client client;
    
    @Autowired
    private User userObject;
    
    private Properties props = new Properties();
    private JLabel forIcon;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            YuploaderApp dialog = new YuploaderApp();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog.
     */
    public YuploaderApp() {
    	setResizable(false);
        initialize();
        setTitle(props.getProperty("yuploader.app.title"));
        setBounds(100, 100, 460, 480);
        SpringLayout springLayout = new SpringLayout();
        springLayout.putConstraint(SpringLayout.WEST, loginPanel, 10, SpringLayout.WEST, getContentPane());
        springLayout.putConstraint(SpringLayout.EAST, loginPanel, -10, SpringLayout.EAST, getContentPane());
        getContentPane().setLayout(springLayout);
        loginPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(loginPanel);
        SpringLayout sl_loginPanel = new SpringLayout();
        loginPanel.setLayout(sl_loginPanel);

        JLabel lblUserName = new JLabel("User Name: ");
        sl_loginPanel.putConstraint(SpringLayout.NORTH, lblUserName, 5, SpringLayout.NORTH, loginPanel);
        sl_loginPanel.putConstraint(SpringLayout.WEST, lblUserName, 10, SpringLayout.WEST, loginPanel);
        loginPanel.add(lblUserName);

        JLabel lblPassword = new JLabel("Password: ");
        sl_loginPanel.putConstraint(SpringLayout.NORTH, lblPassword, 15, SpringLayout.SOUTH, lblUserName);
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
        
        JPanel headerPanel = new JPanel();
        springLayout.putConstraint(SpringLayout.SOUTH, loginPanel, 121, SpringLayout.SOUTH, headerPanel);
        springLayout.putConstraint(SpringLayout.EAST, headerPanel, -10, SpringLayout.EAST, getContentPane());
        springLayout.putConstraint(SpringLayout.NORTH, loginPanel, 10, SpringLayout.SOUTH, headerPanel);
        springLayout.putConstraint(SpringLayout.WEST, headerPanel, 10, SpringLayout.WEST, getContentPane());
        
        btnLogin = new JButton("Login");
        sl_loginPanel.putConstraint(SpringLayout.NORTH, btnLogin, 10, SpringLayout.SOUTH, txtPassword);
        sl_loginPanel.putConstraint(SpringLayout.EAST, btnLogin, 0, SpringLayout.EAST, txtPassword);
        btnLogin.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		login(e);
        	}
        });
        loginPanel.add(btnLogin);
        getRootPane().setDefaultButton(btnLogin);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().add(headerPanel);
        SpringLayout sl_headerPanel = new SpringLayout();
        headerPanel.setLayout(sl_headerPanel);
        
        BufferedImage logo = null;
        try {
			logo = ImageIO.read(getClass().getResource(props.getProperty("logo.path")));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        
        JLabel lblHeader = new JLabel(props.getProperty("help.header"));
        sl_headerPanel.putConstraint(SpringLayout.NORTH, lblHeader, 5, SpringLayout.NORTH, headerPanel);
        sl_headerPanel.putConstraint(SpringLayout.WEST, lblHeader, 5, SpringLayout.WEST, headerPanel);
        sl_headerPanel.putConstraint(SpringLayout.EAST, lblHeader, -5, SpringLayout.EAST, headerPanel);
        headerPanel.add(lblHeader);
        
        JLabel lblForSite = new JLabel("Website: ");
        sl_headerPanel.putConstraint(SpringLayout.NORTH, lblForSite, 5, SpringLayout.SOUTH, lblHeader);
        sl_headerPanel.putConstraint(SpringLayout.WEST, lblForSite, 5, SpringLayout.WEST, headerPanel);
        sl_headerPanel.putConstraint(SpringLayout.EAST, lblForSite, 80, SpringLayout.WEST, headerPanel);
        headerPanel.add(lblForSite);
        
        JLabel lblForMobile = new JLabel("Mobile: ");
        sl_headerPanel.putConstraint(SpringLayout.NORTH, lblForMobile, 5, SpringLayout.SOUTH, lblForSite);
        sl_headerPanel.putConstraint(SpringLayout.WEST, lblForMobile, 5, SpringLayout.WEST, headerPanel);
        sl_headerPanel.putConstraint(SpringLayout.EAST, lblForMobile, 80, SpringLayout.WEST, headerPanel);
        headerPanel.add(lblForMobile);
        
        JLabel lblSite = new JLabel(props.getProperty("help.site"));
        sl_headerPanel.putConstraint(SpringLayout.NORTH, lblSite, 0, SpringLayout.NORTH, lblForSite);
        sl_headerPanel.putConstraint(SpringLayout.WEST, lblSite, 10, SpringLayout.EAST, lblForSite);
        sl_headerPanel.putConstraint(SpringLayout.EAST, lblSite, -5, SpringLayout.EAST, headerPanel);
        headerPanel.add(lblSite);
        
        JLabel lblNumber = new JLabel(props.getProperty("help.mobile"));
        sl_headerPanel.putConstraint(SpringLayout.NORTH, lblNumber, 0, SpringLayout.NORTH, lblForMobile);
        sl_headerPanel.putConstraint(SpringLayout.WEST, lblNumber, 10, SpringLayout.EAST, lblForMobile);
        sl_headerPanel.putConstraint(SpringLayout.EAST, lblNumber, -5, SpringLayout.EAST, headerPanel);
        headerPanel.add(lblNumber);
        
        JPanel statusPanel = new JPanel();
        springLayout.putConstraint(SpringLayout.NORTH, statusPanel, -40, SpringLayout.SOUTH, getContentPane());
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
        forIcon = new JLabel(new ImageIcon(logo));
        springLayout.putConstraint(SpringLayout.SOUTH, headerPanel, 90, SpringLayout.SOUTH, forIcon);
        springLayout.putConstraint(SpringLayout.NORTH, forIcon, 10, SpringLayout.NORTH, getContentPane());
        springLayout.putConstraint(SpringLayout.WEST, forIcon, 10, SpringLayout.WEST, getContentPane());
        springLayout.putConstraint(SpringLayout.SOUTH, forIcon, 200, SpringLayout.NORTH, getContentPane());
        springLayout.putConstraint(SpringLayout.EAST, forIcon, -10, SpringLayout.EAST, getContentPane());
        sl_headerPanel.putConstraint(SpringLayout.WEST, forIcon, 10, SpringLayout.WEST, getContentPane());
        sl_headerPanel.putConstraint(SpringLayout.EAST, forIcon, 227, SpringLayout.WEST, getContentPane());
        sl_headerPanel.putConstraint(SpringLayout.NORTH, forIcon, 10, SpringLayout.NORTH, getContentPane());
        sl_headerPanel.putConstraint(SpringLayout.SOUTH, forIcon, 170, SpringLayout.NORTH, getContentPane());
        springLayout.putConstraint(SpringLayout.NORTH, headerPanel, 10, SpringLayout.SOUTH, forIcon);
        getContentPane().add(forIcon);

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
        final String user = txtUserName.getText();
        final String passwd = new String(txtPassword.getPassword());
        final YuploaderApp me = this;
        new SwingWorker<Integer, Integer>() {
            @Override
            protected Integer doInBackground() throws Exception {
                if (StringUtils.isNotBlank(user) && StringUtils.isNotBlank(passwd)) {
                    try {
                        txtUserName.setEnabled(false);
                        txtPassword.setEnabled(false);
                        btnLogin.setEnabled(false);
                        List<User> users = jdbcTemplate.query(YuownUtils.SELECT_USER_QUERY, new String[] { user }, new UserMapper());
                        if (!users.isEmpty()) {
                            User userEntity = users.get(0);
                            if (StringUtils.equalsIgnoreCase(userEntity.getUname(), user) && StringUtils.equals(userEntity.getPasswd(), passwd)) {
                                if (!userEntity.isEnabled()) {
                                    helper.alert(me, "Your User is Disabled, Please Contact Administrator");
                                } else {
                                    me.userObject.setUname(userEntity.getUname());
                                    launchApp();
                                    me.setVisible(false);
                                }
                            } else {
                                helper.alert(me, "Your Password is wrong, Please check");
                            }
                        } else {
                            helper.alert(me, "Your Username is wrong, Please check");
                        }
                    } catch (CannotGetJdbcConnectionException cgjdbcexp) {
                        helper.alert(me, "Failed to Connect to Database Server, Please Contact Administrator");
                        cgjdbcexp.printStackTrace();
                    }
                } else {
                    helper.alert(me, "Username and Password are mandatory, Please Enter");
                }
                txtUserName.setEnabled(true);
                txtPassword.setEnabled(true);
                btnLogin.setEnabled(true);
                return 0;
            }
        }.execute();
    }

	private void launchApp() {
		System.out.println("3. Client: " + client.hashCode());
		client.setVisible(true);
		client.setUser();
		setVisible(false);
		dispose();
    }

    private void initialize() {
        context = new ClassPathXmlApplicationContext(new String[] { "yuploader.xml" });
        jdbcTemplate = context.getBean("jdbcTemplate", JdbcTemplate.class);
        helper = context.getBean("helper", Helper.class);
        
        AutowireCapableBeanFactory aw = context.getAutowireCapableBeanFactory();
        client = aw.getBean(Client.class);
        client.setAutoWireCapableBeanFactory(aw);
        userObject = aw.getBean("userObject", User.class);
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("yuploader-internet.properties");
        
		if (inputStream != null) {
			try {
				props.load(inputStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }
}
