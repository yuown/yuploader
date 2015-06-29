package yuown.yuploader.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;

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

    private final JPanel contentPanel = new JPanel();
    private JTextField txtUserName;
    private JPasswordField txtPassword;

    private JdbcTemplate jdbcTemplate;
    private YuploaderApp applicationDesktop;
    private Helper helper;
    private FTPHelperBean ftpHelperBean;

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
        setTitle("Yuploader - Vasanth Video Vision");
        setBounds(100, 100, 400, 200);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);

        JLabel lblNewLabel = new JLabel("User Name: ");
        lblNewLabel.setBounds(42, 32, 85, 16);
        contentPanel.add(lblNewLabel);

        JLabel lblPassword = new JLabel("Password: ");
        lblPassword.setBounds(42, 74, 85, 16);
        contentPanel.add(lblPassword);

        txtUserName = new JTextField();
        txtUserName.setBounds(139, 26, 193, 28);
        contentPanel.add(txtUserName);
        txtUserName.setColumns(10);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(139, 68, 193, 28);
        contentPanel.add(txtPassword);
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        login(e);
                    }
                });
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }

        initialize();
    }

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
                            ftpHelperBean.setUserName(userEntity.getUname());
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

    private void launchApp() {
        applicationDesktop = context.getBean("applicationDesktop", YuploaderApp.class);
        applicationDesktop.getFrame().setVisible(true);
    }

    private void initialize() {
        context = new ClassPathXmlApplicationContext(new String[] { "yuploader.xml" });
        jdbcTemplate = context.getBean("jdbcTemplate", JdbcTemplate.class);
        helper = context.getBean("helper", Helper.class);
        ftpHelperBean = context.getBean("ftpHelperBean", FTPHelperBean.class);
    }
}
