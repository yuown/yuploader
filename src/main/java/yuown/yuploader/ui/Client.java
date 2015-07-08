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
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import org.apache.commons.net.ftp.FTP;
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

	/**
	 * 
	 */
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

	private int start = 0;

	public Client() {
		// init();
	}

	@PostConstruct
	public void init() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 700);
		setTitle(appTitle);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);

		JPanel userPanel = new JPanel();
		sl_contentPane.putConstraint(SpringLayout.NORTH, userPanel, 10, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, userPanel, -230, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, userPanel, 100, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, userPanel, -10, SpringLayout.EAST, contentPane);
		contentPane.add(userPanel);
		SpringLayout sl_userPanel = new SpringLayout();
		userPanel.setLayout(sl_userPanel);

		lblUsername = new JLabel("Username");
		lblUsername.setHorizontalAlignment(SwingConstants.RIGHT);
		sl_userPanel.putConstraint(SpringLayout.NORTH, lblUsername, 10, SpringLayout.NORTH, userPanel);
		sl_userPanel.putConstraint(SpringLayout.WEST, lblUsername, 10, SpringLayout.WEST, userPanel);
		sl_userPanel.putConstraint(SpringLayout.EAST, lblUsername, -10, SpringLayout.EAST, userPanel);
		userPanel.add(lblUsername);

		lblName = new JLabel("Name");
		lblName.setHorizontalAlignment(SwingConstants.RIGHT);
		sl_userPanel.putConstraint(SpringLayout.NORTH, lblName, 5, SpringLayout.SOUTH, lblUsername);
		sl_userPanel.putConstraint(SpringLayout.WEST, lblName, 10, SpringLayout.WEST, userPanel);
		sl_userPanel.putConstraint(SpringLayout.EAST, lblName, -10, SpringLayout.EAST, userPanel);
		userPanel.add(lblName);

		btnlogout = new JButton("Logout");
		btnlogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logout(e);
			}
		});
		sl_userPanel.putConstraint(SpringLayout.NORTH, btnlogout, 5, SpringLayout.SOUTH, lblName);
		sl_userPanel.putConstraint(SpringLayout.EAST, btnlogout, -10, SpringLayout.EAST, userPanel);
		userPanel.add(btnlogout);

		JPanel logoPanel = new JPanel();
		sl_contentPane.putConstraint(SpringLayout.NORTH, logoPanel, 10, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, logoPanel, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, logoPanel, 185, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, logoPanel, 593, SpringLayout.WEST, contentPane);
		contentPane.add(logoPanel);
		SpringLayout sl_logoPanel = new SpringLayout();
		logoPanel.setLayout(sl_logoPanel);

		try {
			BufferedImage logo = ImageIO.read(getClass().getResource(logoPath));
			lblForIcon_1 = new JLabel(new ImageIcon(logo));
			sl_logoPanel.putConstraint(SpringLayout.NORTH, lblForIcon_1, 0, SpringLayout.NORTH, logoPanel);
			sl_logoPanel.putConstraint(SpringLayout.WEST, lblForIcon_1, 0, SpringLayout.WEST, logoPanel);
			sl_logoPanel.putConstraint(SpringLayout.SOUTH, lblForIcon_1, 160, SpringLayout.NORTH, logoPanel);
			sl_logoPanel.putConstraint(SpringLayout.EAST, lblForIcon_1, 217, SpringLayout.WEST, logoPanel);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		logoPanel.add(lblForIcon_1);

		JLabel lblHeader = new JLabel(helpHeader);
		sl_logoPanel.putConstraint(SpringLayout.NORTH, lblHeader, 10, SpringLayout.NORTH, logoPanel);
		sl_logoPanel.putConstraint(SpringLayout.WEST, lblHeader, 6, SpringLayout.EAST, lblForIcon_1);
		sl_logoPanel.putConstraint(SpringLayout.EAST, lblHeader, -10, SpringLayout.EAST, logoPanel);
		logoPanel.add(lblHeader);

		JLabel lblForSite = new JLabel("Website: ");
		sl_logoPanel.putConstraint(SpringLayout.NORTH, lblForSite, 6, SpringLayout.SOUTH, lblHeader);
		sl_logoPanel.putConstraint(SpringLayout.WEST, lblForSite, 6, SpringLayout.EAST, lblForIcon_1);
		logoPanel.add(lblForSite);

		JLabel lblForMobile = new JLabel("Mobile: ");
		sl_logoPanel.putConstraint(SpringLayout.NORTH, lblForMobile, 6, SpringLayout.SOUTH, lblForSite);
		sl_logoPanel.putConstraint(SpringLayout.WEST, lblForMobile, 6, SpringLayout.EAST, lblForIcon_1);
		logoPanel.add(lblForMobile);

		JLabel lblSite = new JLabel(helpSite);
		sl_logoPanel.putConstraint(SpringLayout.EAST, lblSite, 0, SpringLayout.EAST, lblHeader);
		sl_logoPanel.putConstraint(SpringLayout.WEST, lblSite, 6, SpringLayout.EAST, lblForSite);
		sl_logoPanel.putConstraint(SpringLayout.SOUTH, lblSite, 0, SpringLayout.SOUTH, lblForSite);
		logoPanel.add(lblSite);

		JLabel lblNumber = new JLabel(helpMobile);
		sl_logoPanel.putConstraint(SpringLayout.WEST, lblNumber, 13, SpringLayout.EAST, lblForMobile);
		sl_logoPanel.putConstraint(SpringLayout.SOUTH, lblNumber, 0, SpringLayout.SOUTH, lblForMobile);
		sl_logoPanel.putConstraint(SpringLayout.EAST, lblNumber, 0, SpringLayout.EAST, lblHeader);
		logoPanel.add(lblNumber);

		JPanel statusPanel = new JPanel();
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		sl_contentPane.putConstraint(SpringLayout.NORTH, statusPanel, -30, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, statusPanel, 0, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, statusPanel, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, statusPanel, 0, SpringLayout.EAST, contentPane);
		contentPane.add(statusPanel);
		SpringLayout sl_statusPanel = new SpringLayout();
		statusPanel.setLayout(sl_statusPanel);

		JLabel lblDeveloper = new JLabel("Developer: ");
		sl_statusPanel.putConstraint(SpringLayout.NORTH, lblDeveloper, 7, SpringLayout.NORTH, statusPanel);
		sl_statusPanel.putConstraint(SpringLayout.WEST, lblDeveloper, 2, SpringLayout.WEST, statusPanel);
		statusPanel.add(lblDeveloper);

		JLabel lblDevsitecom = new JLabel(developerMail);
		sl_statusPanel.putConstraint(SpringLayout.NORTH, lblDevsitecom, 7, SpringLayout.NORTH, statusPanel);
		sl_statusPanel.putConstraint(SpringLayout.WEST, lblDevsitecom, 5, SpringLayout.EAST, lblDeveloper);
		statusPanel.add(lblDevsitecom);

		JLabel lblAppVersion = new JLabel(appVersion);
		sl_statusPanel.putConstraint(SpringLayout.NORTH, lblAppVersion, 7, SpringLayout.NORTH, statusPanel);
		sl_statusPanel.putConstraint(SpringLayout.EAST, lblAppVersion, -10, SpringLayout.EAST, statusPanel);
		statusPanel.add(lblAppVersion);

		JScrollPane scrollPane = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -5, SpringLayout.NORTH, statusPanel);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -5, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane);

		btnAddFiles = new JButton("Add Files");
		btnAddFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectFiles(e);
			}
		});
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnAddFiles, 5, SpringLayout.SOUTH, logoPanel);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnAddFiles, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 5, SpringLayout.SOUTH, btnAddFiles);

		fileTable = new JTable();
		fileTable.getTableHeader().setReorderingAllowed(false);
		scrollPane.setColumnHeaderView(fileTable);
		scrollPane.setViewportView(fileTable);
		fileTable.setModel(yuploaderTableModel);
		yuploaderTableModel.setTable(fileTable);

		contentPane.add(btnAddFiles);

		btnUploadFiles = new JButton("Upload Files");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnUploadFiles, 0, SpringLayout.NORTH, btnAddFiles);
		btnUploadFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				submitToUpload();
			}
		});
		contentPane.add(btnUploadFiles);

		btnRemoveSelectedFiles = new JButton("Remove Selected Files");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnRemoveSelectedFiles, 0, SpringLayout.NORTH, btnAddFiles);
		btnRemoveSelectedFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				removeSelectedFiles();
			}
		});
		sl_contentPane.putConstraint(SpringLayout.WEST, btnUploadFiles, 5, SpringLayout.EAST, btnRemoveSelectedFiles);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnRemoveSelectedFiles, 5, SpringLayout.EAST, btnAddFiles);
		contentPane.add(btnRemoveSelectedFiles);

		btnPause = new JButton("Pause");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnPause, 0, SpringLayout.NORTH, btnAddFiles);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnPause, 5, SpringLayout.EAST, btnUploadFiles);
		btnPause.addActionListener(streamListener);
		contentPane.add(btnPause);

		btnCancelUpload = new JButton("Cancel Upload");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnCancelUpload, 0, SpringLayout.NORTH, btnAddFiles);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnCancelUpload, 5, SpringLayout.EAST, btnPause);
		contentPane.add(btnCancelUpload);

		fileChooser = new JFileChooser();
		hidePause(true);
		checkTimeoutAndConnect();
		System.out.println("2. Client: " + this.hashCode());
	}

	protected void removeSelectedFiles() {
		yuploaderTableModel.removeSelectedRows();
	}

	public void submitToUpload() {
		if(connected) {
			startOrPause();
		}
	}

	public void startOrPause() {
		queueUpload = aw.createBean(QueueUpload.class);
		queueUpload.setStart(start);
		hidePause(false);
		streamListener.setPaused(false);
		queueUpload.execute();
	}

	public void toggleLoginCtrls(boolean b) {
		btnAddFiles.setEnabled(b);
		btnUploadFiles.setEnabled(b);
		btnRemoveSelectedFiles.setEnabled(b);
		btnlogout.setEnabled(b);
	}

	public void hidePause(boolean b) {
		toggleLoginCtrls(b);
		btnPause.setVisible(!b);
		btnCancelUpload.setVisible(!b);
	}

	protected void logout(ActionEvent e) {
		try {
			ftpClient.logout();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		this.dispose();
	}

	protected void selectFiles(ActionEvent e) {
		this.fileChooser.setMultiSelectionEnabled(true);
		this.fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
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
		yuploaderTableModel.addRow(fileObject);
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
		lblUsername.setText(userObject.getUname());
		lblName.setText(userObject.getUname());
	}

	public void setAutoWireCapableBeanFactory(AutowireCapableBeanFactory aw) {
		this.aw = aw;
		yuploaderTableModel.setAutoWireBeanCapableFactory(aw);
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public boolean checkTimeoutAndConnect() {
		if (!isConnected() || (System.currentTimeMillis() - lastAccess > ftpTimeout)) {
			try {
				ftpClient.connect(ftpHost);
				int reply = ftpClient.getReplyCode();
				if (!FTPReply.isPositiveCompletion(reply)) {
					ftpClient.disconnect();
					System.err.println("FTP server refused connection.");
				} else {
					if (!ftpClient.login(ftpUsername, ftpPassword)) {
						ftpClient.logout();
						helper.alert(this, "Problem with FTP Server Credentials, Contact Admin.");
					} else {
						try {
							ftpClient.enterLocalPassiveMode();
							ftpClient.setAutodetectUTF8(true);
							ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
							ftpClient.setBufferSize(-1);
							ftpClient.setFileTransferMode(2);
							setConnected(true);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
				createMissingDirectories();
			} catch (Exception e) {
				connected = false;
				helper.alert(this, "Check your Network connectivity, looks like you are not connected to Internet!");
				hidePause(true);
				e.printStackTrace();
			}
		}
		return connected;
	}

	private void createMissingDirectories() {
		String DDMMYYYY = helper.getDateDDMMYYYY();
		boolean exists = false;
		try {
			exists = ftpClient.changeWorkingDirectory(ftpBasePath);
			if (!exists) {
				ftpClient.makeDirectory(ftpBasePath);
				ftpClient.changeWorkingDirectory(ftpBasePath);
			}
			exists = ftpClient.changeWorkingDirectory(userObject.getUname());
			if (!exists) {
				ftpClient.makeDirectory(userObject.getUname());
				ftpClient.changeWorkingDirectory(userObject.getUname());
			}
			exists = ftpClient.changeWorkingDirectory(DDMMYYYY);
			if (!exists) {
				ftpClient.makeDirectory(DDMMYYYY);
				ftpClient.changeWorkingDirectory(DDMMYYYY);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setLastAccess(long currentTimeMillis) {
		this.lastAccess = currentTimeMillis;
	}

	public void setStart(int start) {
		if (this.start < 0) {
			this.start = 0;
		} else {
			this.start = start;
		}
	}

	public int getStart() {
		return start;
	}
}
