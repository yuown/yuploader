package yuown.yuploader.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

import yuown.yuploader.ftp.QueueUpload;
import yuown.yuploader.ftp.StreamListener;
import yuown.yuploader.model.FileObject;
import yuown.yuploader.model.Status;
import yuown.yuploader.model.User;
import yuown.yuploader.model.YuploaderTableModel;
import yuown.yuploader.util.Helper;

@Component
public class Client extends JFrame {
	private static final long serialVersionUID = 5092315944101071110L;
	private JPanel contentPane;
	private JLabel lblForIcon_1;
	private JTable fileTable;
	@Value("${developer.email}")
	private String developerMail;
	@Value("${help.header}")
	private String helpHeader;
	@Value("${help.site}")
	private String helpSite;
	@Value("${help.mobile}")
	private String helpMobile;
	@Value("${app.version}")
	private String appVersion;
	@Value("${yuploader.app.title}")
	private String appTitle;
	@Value("${logo.path}")
	private String logoPath;
	private JFileChooser fileChooser;
	private JLabel lblUsername;
	private JLabel lblName;
	private JButton btnAddFiles;
	private JButton btnRemoveSelectedFiles;
	private JButton btnUploadFiles;
	private JButton btnlogout;
	private JButton btnPause;
	private JButton btnCancelUpload;
	@Autowired
	private Helper helper;
	@Autowired
	private User userObject;
	@Autowired
	private YuploaderTableModel yuploaderTableModel;
	@Autowired
	private QueueUpload queueUpload;
	@Autowired
	private StreamListener streamListener;
	private AutowireCapableBeanFactory aw;
	private boolean connected = false;
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
	private long ftpTimeout;
	private long lastAccess;
	private boolean inProgress = false;

	@PostConstruct
	public void init() {
		setDefaultCloseOperation(3);
		setBounds(100, 100, 800, 700);
		setTitle(this.appTitle);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(this.contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		this.contentPane.setLayout(sl_contentPane);

		JPanel userPanel = new JPanel();
		sl_contentPane.putConstraint("North", userPanel, 10, "North", this.contentPane);
		sl_contentPane.putConstraint("West", userPanel, 65306, "East", this.contentPane);
		sl_contentPane.putConstraint("South", userPanel, 100, "North", this.contentPane);
		sl_contentPane.putConstraint("East", userPanel, -10, "East", this.contentPane);
		this.contentPane.add(userPanel);
		SpringLayout sl_userPanel = new SpringLayout();
		userPanel.setLayout(sl_userPanel);

		this.lblUsername = new JLabel("Username");
		this.lblUsername.setHorizontalAlignment(4);
		sl_userPanel.putConstraint("North", this.lblUsername, 10, "North", userPanel);
		sl_userPanel.putConstraint("West", this.lblUsername, 10, "West", userPanel);
		sl_userPanel.putConstraint("East", this.lblUsername, -10, "East", userPanel);
		userPanel.add(this.lblUsername);

		this.lblName = new JLabel("Name");
		this.lblName.setHorizontalAlignment(4);
		sl_userPanel.putConstraint("North", this.lblName, 5, "South", this.lblUsername);
		sl_userPanel.putConstraint("West", this.lblName, 10, "West", userPanel);
		sl_userPanel.putConstraint("East", this.lblName, -10, "East", userPanel);
		userPanel.add(this.lblName);

		this.btnlogout = new JButton("Logout");
		this.btnlogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Client.this.logout(e);
			}
		});
		sl_userPanel.putConstraint("North", this.btnlogout, 5, "South", this.lblName);
		sl_userPanel.putConstraint("East", this.btnlogout, -10, "East", userPanel);
		userPanel.add(this.btnlogout);

		JPanel logoPanel = new JPanel();
		sl_contentPane.putConstraint("North", logoPanel, 10, "North", this.contentPane);
		sl_contentPane.putConstraint("West", logoPanel, 10, "West", this.contentPane);
		sl_contentPane.putConstraint("South", logoPanel, 185, "North", this.contentPane);
		sl_contentPane.putConstraint("East", logoPanel, 593, "West", this.contentPane);
		this.contentPane.add(logoPanel);
		SpringLayout sl_logoPanel = new SpringLayout();
		logoPanel.setLayout(sl_logoPanel);
		try {
			BufferedImage logo = ImageIO.read(getClass().getResource(this.logoPath));
			this.lblForIcon_1 = new JLabel(new ImageIcon(logo));
			sl_logoPanel.putConstraint("North", this.lblForIcon_1, 0, "North", logoPanel);
			sl_logoPanel.putConstraint("West", this.lblForIcon_1, 0, "West", logoPanel);
			sl_logoPanel.putConstraint("South", this.lblForIcon_1, 160, "North", logoPanel);
			sl_logoPanel.putConstraint("East", this.lblForIcon_1, 217, "West", logoPanel);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		logoPanel.add(this.lblForIcon_1);

		JLabel lblHeader = new JLabel(this.helpHeader);
		sl_logoPanel.putConstraint("North", lblHeader, 10, "North", logoPanel);
		sl_logoPanel.putConstraint("West", lblHeader, 6, "East", this.lblForIcon_1);
		sl_logoPanel.putConstraint("East", lblHeader, -10, "East", logoPanel);
		logoPanel.add(lblHeader);

		JLabel lblForSite = new JLabel("Website: ");
		sl_logoPanel.putConstraint("North", lblForSite, 6, "South", lblHeader);
		sl_logoPanel.putConstraint("West", lblForSite, 6, "East", this.lblForIcon_1);
		logoPanel.add(lblForSite);

		JLabel lblForMobile = new JLabel("Mobile: ");
		sl_logoPanel.putConstraint("North", lblForMobile, 6, "South", lblForSite);
		sl_logoPanel.putConstraint("West", lblForMobile, 6, "East", this.lblForIcon_1);
		logoPanel.add(lblForMobile);

		JLabel lblSite = new JLabel(this.helpSite);
		sl_logoPanel.putConstraint("East", lblSite, 0, "East", lblHeader);
		sl_logoPanel.putConstraint("West", lblSite, 6, "East", lblForSite);
		sl_logoPanel.putConstraint("South", lblSite, 0, "South", lblForSite);
		logoPanel.add(lblSite);

		JLabel lblNumber = new JLabel(this.helpMobile);
		sl_logoPanel.putConstraint("West", lblNumber, 13, "East", lblForMobile);
		sl_logoPanel.putConstraint("South", lblNumber, 0, "South", lblForMobile);
		sl_logoPanel.putConstraint("East", lblNumber, 0, "East", lblHeader);
		logoPanel.add(lblNumber);

		JPanel statusPanel = new JPanel();
		statusPanel.setBorder(new BevelBorder(1, null, null, null, null));
		sl_contentPane.putConstraint("North", statusPanel, -30, "South", this.contentPane);
		sl_contentPane.putConstraint("West", statusPanel, 0, "West", this.contentPane);
		sl_contentPane.putConstraint("South", statusPanel, 0, "South", this.contentPane);
		sl_contentPane.putConstraint("East", statusPanel, 0, "East", this.contentPane);
		this.contentPane.add(statusPanel);
		SpringLayout sl_statusPanel = new SpringLayout();
		statusPanel.setLayout(sl_statusPanel);

		JLabel lblDeveloper = new JLabel("Developer: ");
		sl_statusPanel.putConstraint("North", lblDeveloper, 7, "North", statusPanel);
		sl_statusPanel.putConstraint("West", lblDeveloper, 2, "West", statusPanel);
		statusPanel.add(lblDeveloper);

		JLabel lblDevsitecom = new JLabel(this.developerMail);
		sl_statusPanel.putConstraint("North", lblDevsitecom, 7, "North", statusPanel);
		sl_statusPanel.putConstraint("West", lblDevsitecom, 5, "East", lblDeveloper);
		statusPanel.add(lblDevsitecom);

		JLabel lblAppVersion = new JLabel(this.appVersion);
		sl_statusPanel.putConstraint("North", lblAppVersion, 7, "North", statusPanel);
		sl_statusPanel.putConstraint("East", lblAppVersion, -10, "East", statusPanel);
		statusPanel.add(lblAppVersion);

		JScrollPane scrollPane = new JScrollPane();
		sl_contentPane.putConstraint("West", scrollPane, 10, "West", this.contentPane);
		sl_contentPane.putConstraint("South", scrollPane, -5, "North", statusPanel);
		sl_contentPane.putConstraint("East", scrollPane, -5, "East", this.contentPane);
		this.contentPane.add(scrollPane);

		this.btnAddFiles = new JButton("Add Files");
		this.btnAddFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Client.this.selectFiles(e);
			}
		});
		sl_contentPane.putConstraint("North", this.btnAddFiles, 5, "South", logoPanel);
		sl_contentPane.putConstraint("West", this.btnAddFiles, 10, "West", this.contentPane);
		sl_contentPane.putConstraint("North", scrollPane, 5, "South", this.btnAddFiles);

		this.fileTable = new JTable();
		this.fileTable.getTableHeader().setReorderingAllowed(false);
		scrollPane.setColumnHeaderView(this.fileTable);
		scrollPane.setViewportView(this.fileTable);
		this.fileTable.setModel(this.yuploaderTableModel);
		this.yuploaderTableModel.setTable(this.fileTable);

		this.contentPane.add(this.btnAddFiles);

		this.btnUploadFiles = new JButton("Upload Files");
		sl_contentPane.putConstraint("North", this.btnUploadFiles, 0, "North", this.btnAddFiles);
		this.btnUploadFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Client.this.submitToUpload();
			}
		});
		this.contentPane.add(this.btnUploadFiles);

		this.btnRemoveSelectedFiles = new JButton("Remove Selected Files");
		sl_contentPane.putConstraint("North", this.btnRemoveSelectedFiles, 0, "North", this.btnAddFiles);
		this.btnRemoveSelectedFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Client.this.removeSelectedFiles();
			}
		});
		sl_contentPane.putConstraint("West", this.btnUploadFiles, 5, "East", this.btnRemoveSelectedFiles);
		sl_contentPane.putConstraint("West", this.btnRemoveSelectedFiles, 5, "East", this.btnAddFiles);
		this.contentPane.add(this.btnRemoveSelectedFiles);

		this.btnPause = new JButton("Pause");
		sl_contentPane.putConstraint("North", this.btnPause, 0, "North", this.btnAddFiles);
		sl_contentPane.putConstraint("West", this.btnPause, 5, "East", this.btnUploadFiles);
		this.btnPause.addActionListener(this.streamListener);
		this.contentPane.add(this.btnPause);

		this.btnCancelUpload = new JButton("Cancel Upload");
		sl_contentPane.putConstraint("North", this.btnCancelUpload, 0, "North", this.btnAddFiles);
		sl_contentPane.putConstraint("West", this.btnCancelUpload, 5, "East", this.btnPause);
		this.btnCancelUpload.addActionListener(this.streamListener);
		this.contentPane.add(this.btnCancelUpload);

		this.fileChooser = new JFileChooser();
		hidePause(true);
	}

	public void connectInBackground() {
		System.out.println("Connect to FTP Server in Background.1");
		new SwingWorker() {
			protected Integer doInBackground() throws Exception {
				System.out.println("Connect to FTP Server in Background.2");
				Client.this.checkTimeoutAndConnect();
				return Integer.valueOf(0);
			}
		}.execute();
	}

	protected void removeSelectedFiles() {
		this.yuploaderTableModel.removeSelectedRows();
	}

	public void submitToUpload() {
		if ((this.connected) || (System.currentTimeMillis() - this.lastAccess < this.ftpTimeout)) {
			System.out.println("submitToUpload: connected?: " + this.connected);
			System.out.println("submitToUpload: Timeout?: " + (System.currentTimeMillis() - this.lastAccess < this.ftpTimeout));
			startOrPause();
		} else {
			System.out.println("Connecting Again due to Timeout or Network issue!");
			connectInBackground();
		}
	}

	public void startOrPause() {
		if (!this.inProgress) {
			hidePause(false);
			this.streamListener.setPaused(false);
			this.queueUpload = ((QueueUpload) this.aw.createBean(QueueUpload.class));
			this.queueUpload.execute();
		}
	}

	public void toggleLoginCtrls(boolean b) {
		this.btnAddFiles.setEnabled(b);
		this.btnUploadFiles.setEnabled(b);
		this.btnRemoveSelectedFiles.setEnabled(b);
		this.btnlogout.setEnabled(b);
	}

	public void hidePause(boolean b) {
		toggleLoginCtrls(b);
		this.btnPause.setVisible(!b);
		this.btnCancelUpload.setVisible(!b);
	}

	protected void logout(ActionEvent e) {
		try {
			this.ftpClient.logout();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		dispose();
	}

	protected void selectFiles(ActionEvent e) {
		this.fileChooser.setMultiSelectionEnabled(true);
		this.fileChooser.setFileSelectionMode(2);
		int command = this.fileChooser.showOpenDialog(this);
		if (command == 0) {
			File[] f = this.fileChooser.getSelectedFiles();
			for (int i = 0; i < f.length; i++) {
				if (f[i].isDirectory()) {
					File[] innerFiles = f[i].listFiles();
					for (int j = 0; j < innerFiles.length; j++) {
						if (innerFiles[j].isFile()) {
							addFiletoTable(file2Object(innerFiles[j]));
						}
					}
				} else if (f[i].isFile()) {
					addFiletoTable(file2Object(f[i]));
				}
			}
		}
	}

	private void addFiletoTable(FileObject fileObject) {
		this.yuploaderTableModel.addRow(fileObject);
	}

	private FileObject file2Object(File file) {
		FileObject fo = new FileObject();
		fo.setFileName(file.getName());
		fo.setFullPath(file.getAbsolutePath());
		fo.setProgress("0 %");
		fo.setStatus(Status.ADDED);
		fo.setSize(file.length());
		return fo;
	}

	public void setUser() {
		this.lblUsername.setText(this.userObject.getUname());
		this.lblName.setText(this.userObject.getFullName());
	}

	public void setAutoWireCapableBeanFactory(AutowireCapableBeanFactory aw) {
		this.aw = aw;
		this.yuploaderTableModel.setAutoWireBeanCapableFactory(aw);
	}

	public boolean isConnected() {
		return this.connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public boolean checkTimeoutAndConnect() {
		if ((!isConnected()) || (System.currentTimeMillis() - this.lastAccess > this.ftpTimeout)) {
			System.out.println("Connected: " + isConnected());
			System.out.println("Timeout ?: " + (System.currentTimeMillis() - this.lastAccess > this.ftpTimeout));
			try {
				this.ftpClient.connect(this.ftpHost);
				int reply = this.ftpClient.getReplyCode();
				if (!FTPReply.isPositiveCompletion(reply)) {
					this.ftpClient.disconnect();
					System.err.println("FTP server refused connection.");
					this.helper.alert(this, "FTP server refused connection.");
				} else if (!this.ftpClient.login(this.ftpUsername, this.ftpPassword)) {
					this.ftpClient.logout();
					System.out.println("Problem with FTP Server Credentials, Contact Admin.");
					this.helper.alert(this, "Problem with FTP Server Credentials, Contact Admin.");
				} else {
					try {
						this.ftpClient.enterLocalPassiveMode();
						this.ftpClient.setAutodetectUTF8(true);
						this.ftpClient.setFileType(2);
						this.ftpClient.setBufferSize(-1);
						this.ftpClient.setFileTransferMode(2);
						setConnected(true);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				createMissingDirectories();
			} catch (Exception e) {
				this.connected = false;
				System.out.println("Check your Network connectivity, looks like you are not connected to Internet!");
				this.helper.alert(this, "Check your Network connectivity, looks like you are not connected to Internet!");
				hidePause(true);
				e.printStackTrace();
			}
		}
		return this.connected;
	}

	private void createMissingDirectories() {
		String DDMMYYYY = this.helper.getDateDDMMYYYY();
		boolean exists = false;
		try {
			exists = this.ftpClient.changeWorkingDirectory(this.ftpBasePath);
			if (!exists) {
				this.ftpClient.makeDirectory(this.ftpBasePath);
				this.ftpClient.changeWorkingDirectory(this.ftpBasePath);
			}
			exists = this.ftpClient.changeWorkingDirectory(this.userObject.getUname());
			if (!exists) {
				this.ftpClient.makeDirectory(this.userObject.getUname());
				this.ftpClient.changeWorkingDirectory(this.userObject.getUname());
			}
			exists = this.ftpClient.changeWorkingDirectory(DDMMYYYY);
			if (!exists) {
				this.ftpClient.makeDirectory(DDMMYYYY);
				this.ftpClient.changeWorkingDirectory(DDMMYYYY);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setLastAccess(long currentTimeMillis) {
		this.lastAccess = currentTimeMillis;
	}

	public void setInProgress(boolean inProgress) {
		this.inProgress = inProgress;
	}

	public boolean getInProgress() {
		return this.inProgress;
	}
}
