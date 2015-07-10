package yuown.yuploader.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
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
import yuown.yuploader.model.Theme;
import yuown.yuploader.model.User;
import yuown.yuploader.util.Helper;

public class YuploaderApp extends JDialog {
	private static final long serialVersionUID = -2444873343061728811L;
	private ApplicationContext context;
	private final JPanel loginPanel = new JPanel();
	private JComboBox themeList;
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

	public static void main(String[] args) {
		try {
			YuploaderApp dialog = new YuploaderApp();
			dialog.setDefaultCloseOperation(2);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public YuploaderApp() {
		setResizable(false);
		initialize();
		setTitle(this.props.getProperty("yuploader.app.title"));
		setBounds(100, 100, 460, 540);
		SpringLayout springLayout = new SpringLayout();
		springLayout.putConstraint("West", this.loginPanel, 10, "West", getContentPane());
		springLayout.putConstraint("East", this.loginPanel, -10, "East", getContentPane());
		getContentPane().setLayout(springLayout);
		this.loginPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(this.loginPanel);
		SpringLayout sl_loginPanel = new SpringLayout();
		this.loginPanel.setLayout(sl_loginPanel);

		JLabel lblUserName = new JLabel("User Name: ");
		sl_loginPanel.putConstraint("West", lblUserName, 10, "West", this.loginPanel);
		this.loginPanel.add(lblUserName);

		JLabel lblPassword = new JLabel("Password: ");
		sl_loginPanel.putConstraint("North", lblPassword, 15, "South", lblUserName);
		sl_loginPanel.putConstraint("West", lblPassword, 10, "West", this.loginPanel);
		sl_loginPanel.putConstraint("East", lblPassword, 0, "East", lblUserName);
		this.loginPanel.add(lblPassword);

		this.txtUserName = new JTextField();
		sl_loginPanel.putConstraint("North", this.txtUserName, -5, "North", lblUserName);
		sl_loginPanel.putConstraint("West", this.txtUserName, 10, "East", lblUserName);
		sl_loginPanel.putConstraint("East", this.txtUserName, -10, "East", this.loginPanel);
		this.loginPanel.add(this.txtUserName);
		this.txtUserName.setColumns(10);

		this.txtPassword = new JPasswordField();
		sl_loginPanel.putConstraint("North", this.txtPassword, -5, "North", lblPassword);
		sl_loginPanel.putConstraint("West", this.txtPassword, 0, "West", this.txtUserName);
		sl_loginPanel.putConstraint("East", this.txtPassword, 0, "East", this.txtUserName);
		this.loginPanel.add(this.txtPassword);

		JPanel headerPanel = new JPanel();
		springLayout.putConstraint("South", this.loginPanel, 150, "South", headerPanel);
		springLayout.putConstraint("East", headerPanel, -10, "East", getContentPane());
		springLayout.putConstraint("North", this.loginPanel, 10, "South", headerPanel);
		springLayout.putConstraint("West", headerPanel, 10, "West", getContentPane());

		this.btnLogin = new JButton("Login");
		sl_loginPanel.putConstraint("North", this.btnLogin, 10, "South", this.txtPassword);
		sl_loginPanel.putConstraint("East", this.btnLogin, 0, "East", this.txtPassword);
		this.btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				YuploaderApp.this.login(e);
			}
		});
		this.loginPanel.add(this.btnLogin);
		getRootPane().setDefaultButton(this.btnLogin);

		JLabel lblTheme = new JLabel("Theme: ");
		sl_loginPanel.putConstraint("North", lblUserName, 15, "South", lblTheme);
		sl_loginPanel.putConstraint("North", lblTheme, 10, "North", this.loginPanel);
		sl_loginPanel.putConstraint("West", lblTheme, 10, "West", this.loginPanel);
		this.loginPanel.add(lblTheme);

		this.themeList = new JComboBox();
		this.themeList.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == 1) {
					Theme item = (Theme) event.getItem();
					YuploaderApp.this.setTheme(YuploaderApp.this, item.getClassName());
				}
			}
		});
		sl_loginPanel.putConstraint("North", this.themeList, -5, "North", lblTheme);
		sl_loginPanel.putConstraint("West", this.themeList, 0, "West", this.txtUserName);
		sl_loginPanel.putConstraint("East", this.themeList, 0, "East", this.txtUserName);
		this.loginPanel.add(this.themeList);
		setDefaultCloseOperation(2);
		getContentPane().add(headerPanel);
		SpringLayout sl_headerPanel = new SpringLayout();
		headerPanel.setLayout(sl_headerPanel);

		BufferedImage logo = null;
		try {
			logo = ImageIO.read(getClass().getResource(this.props.getProperty("logo.path")));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		JLabel lblHeader = new JLabel(this.props.getProperty("help.header"));
		sl_headerPanel.putConstraint("North", lblHeader, 5, "North", headerPanel);
		sl_headerPanel.putConstraint("West", lblHeader, 5, "West", headerPanel);
		sl_headerPanel.putConstraint("East", lblHeader, -5, "East", headerPanel);
		headerPanel.add(lblHeader);

		JLabel lblForSite = new JLabel("Website: ");
		sl_headerPanel.putConstraint("North", lblForSite, 5, "South", lblHeader);
		sl_headerPanel.putConstraint("West", lblForSite, 5, "West", headerPanel);
		sl_headerPanel.putConstraint("East", lblForSite, 80, "West", headerPanel);
		headerPanel.add(lblForSite);

		JLabel lblForMobile = new JLabel("Mobile: ");
		sl_headerPanel.putConstraint("North", lblForMobile, 5, "South", lblForSite);
		sl_headerPanel.putConstraint("West", lblForMobile, 5, "West", headerPanel);
		sl_headerPanel.putConstraint("East", lblForMobile, 80, "West", headerPanel);
		headerPanel.add(lblForMobile);

		JLabel lblSite = new JLabel(this.props.getProperty("help.site"));
		sl_headerPanel.putConstraint("North", lblSite, 0, "North", lblForSite);
		sl_headerPanel.putConstraint("West", lblSite, 10, "East", lblForSite);
		sl_headerPanel.putConstraint("East", lblSite, -5, "East", headerPanel);
		headerPanel.add(lblSite);

		JLabel lblNumber = new JLabel(this.props.getProperty("help.mobile"));
		sl_headerPanel.putConstraint("North", lblNumber, 0, "North", lblForMobile);
		sl_headerPanel.putConstraint("West", lblNumber, 10, "East", lblForMobile);
		sl_headerPanel.putConstraint("East", lblNumber, -5, "East", headerPanel);
		headerPanel.add(lblNumber);

		JPanel statusPanel = new JPanel();
		springLayout.putConstraint("North", statusPanel, -40, "South", getContentPane());
		springLayout.putConstraint("West", statusPanel, 10, "West", getContentPane());
		springLayout.putConstraint("South", statusPanel, -5, "South", getContentPane());
		springLayout.putConstraint("East", statusPanel, -10, "East", getContentPane());
		statusPanel.setBorder(new BevelBorder(1, null, null, null, null));
		getContentPane().add(statusPanel);
		SpringLayout sl_statusPanel = new SpringLayout();
		statusPanel.setLayout(sl_statusPanel);

		JLabel lblDeveloper = new JLabel("Developer: ");
		sl_statusPanel.putConstraint("North", lblDeveloper, 7, "North", statusPanel);
		sl_statusPanel.putConstraint("West", lblDeveloper, 2, "West", statusPanel);
		statusPanel.add(lblDeveloper);

		JLabel lblDevsitecom = new JLabel(this.props.getProperty("developer.email"));
		sl_statusPanel.putConstraint("North", lblDevsitecom, 7, "North", statusPanel);
		sl_statusPanel.putConstraint("West", lblDevsitecom, 5, "East", lblDeveloper);
		statusPanel.add(lblDevsitecom);

		JLabel lblAppVersion = new JLabel(this.props.getProperty("app.version"));
		sl_statusPanel.putConstraint("North", lblAppVersion, 7, "North", statusPanel);
		sl_statusPanel.putConstraint("East", lblAppVersion, -10, "East", statusPanel);
		statusPanel.add(lblAppVersion);
		this.forIcon = new JLabel(new ImageIcon(logo));
		springLayout.putConstraint("South", headerPanel, 90, "South", this.forIcon);
		springLayout.putConstraint("North", this.forIcon, 10, "North", getContentPane());
		springLayout.putConstraint("West", this.forIcon, 10, "West", getContentPane());
		springLayout.putConstraint("South", this.forIcon, 200, "North", getContentPane());
		springLayout.putConstraint("East", this.forIcon, -10, "East", getContentPane());
		sl_headerPanel.putConstraint("West", this.forIcon, 10, "West", getContentPane());
		sl_headerPanel.putConstraint("East", this.forIcon, 227, "West", getContentPane());
		sl_headerPanel.putConstraint("North", this.forIcon, 10, "North", getContentPane());
		sl_headerPanel.putConstraint("South", this.forIcon, 170, "North", getContentPane());
		springLayout.putConstraint("North", headerPanel, 10, "South", this.forIcon);
		getContentPane().add(this.forIcon);
		for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			try {
				this.themeList.addItem(new Theme(info.getName(), info.getClassName()));
			} catch (Exception e) {
			}
		}
		setTheme(this, ((Theme) this.themeList.getItemAt(0)).getClassName());
	}

	protected void setTheme(JDialog frame, String className) {
		try {
			UIManager.setLookAndFeel(className);
			SwingUtilities.updateComponentTreeUI(frame);
		} catch (Exception e) {
		}
	}

	protected void setTheme(JFrame frame, String className) {
		try {
			UIManager.setLookAndFeel(className);
			SwingUtilities.updateComponentTreeUI(frame);
		} catch (Exception e) {
		}
	}

	protected void login(ActionEvent e) {
		final String user = this.txtUserName.getText();
		final String passwd = new String(this.txtPassword.getPassword());
		final YuploaderApp me = this;
		new SwingWorker() {
			protected Integer doInBackground() throws Exception {
				if ((StringUtils.isNotBlank(user)) && (StringUtils.isNotBlank(passwd))) {
					try {
						YuploaderApp.this.txtUserName.setEnabled(false);
						YuploaderApp.this.txtPassword.setEnabled(false);
						YuploaderApp.this.btnLogin.setEnabled(false);
						YuploaderApp.this.themeList.setEnabled(false);
						List<User> users = YuploaderApp.this.jdbcTemplate.query("SELECT * FROM users WHERE uname = ?", new String[] { user }, new UserMapper());
						if (!users.isEmpty()) {
							User userEntity = (User) users.get(0);
							if ((StringUtils.equalsIgnoreCase(userEntity.getUname(), user)) && (StringUtils.equals(userEntity.getPasswd(), passwd))) {
								if (!userEntity.isEnabled()) {
									YuploaderApp.this.helper.alert(me, "Your User is Disabled, Please Contact Administrator");
								} else {
									me.userObject.setUname(userEntity.getUname());
									me.userObject.setFullName(userEntity.getFullName());
									YuploaderApp.this.launchApp();
									me.setVisible(false);
								}
							} else {
								YuploaderApp.this.helper.alert(me, "Your Password is wrong, Please check");
							}
						} else {
							YuploaderApp.this.helper.alert(me, "Your Username is wrong, Please check");
						}
					} catch (CannotGetJdbcConnectionException cgjdbcexp) {
						YuploaderApp.this.helper.alert(me, "Failed to Connect to Database Server, Please Contact Administrator");
						cgjdbcexp.printStackTrace();
					}
				} else {
					YuploaderApp.this.helper.alert(me, "Username and Password are mandatory, Please Enter");
				}
				YuploaderApp.this.txtUserName.setEnabled(true);
				YuploaderApp.this.txtPassword.setEnabled(true);
				YuploaderApp.this.btnLogin.setEnabled(true);
				YuploaderApp.this.themeList.setEnabled(true);
				return Integer.valueOf(0);
			}
		}.execute();
	}

	private void launchApp() {
		System.out.println("3. Client: " + this.client.hashCode());
		setTheme(this.client, ((Theme) this.themeList.getSelectedItem()).getClassName());
		this.client.setVisible(true);
		this.client.setUser();
		this.client.connectInBackground();
		setVisible(false);
		dispose();
	}

	private void initialize() {
		this.context = new ClassPathXmlApplicationContext(new String[] { "yuploader.xml" });
		this.jdbcTemplate = ((JdbcTemplate) this.context.getBean("jdbcTemplate", JdbcTemplate.class));
		this.helper = ((Helper) this.context.getBean("helper", Helper.class));

		AutowireCapableBeanFactory aw = this.context.getAutowireCapableBeanFactory();
		this.client = ((Client) aw.getBean(Client.class));
		this.client.setAutoWireCapableBeanFactory(aw);
		this.userObject = ((User) aw.getBean("userObject", User.class));
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("yuploader-internet.properties");
		if (inputStream != null) {
			try {
				this.props.load(inputStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
