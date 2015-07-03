package yuown.yuploader.ui;

import java.awt.image.BufferedImage;
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
import javax.swing.table.DefaultTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import yuown.yuploader.model.FileObject;
import yuown.yuploader.model.YuploaderTableModel;

@Component
public class Client extends JFrame {

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
    
    private JFileChooser fileChooser;
    
    @Autowired
    private YuploaderTableModel yuploaderTableModel;

    public Client() {
        init();
	}
    
    @PostConstruct
    public void init() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 660, 551);
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
        
        JLabel lblUsername = new JLabel("Username");
        lblUsername.setHorizontalAlignment(SwingConstants.RIGHT);
        sl_userPanel.putConstraint(SpringLayout.NORTH, lblUsername, 10, SpringLayout.NORTH, userPanel);
        sl_userPanel.putConstraint(SpringLayout.WEST, lblUsername, 10, SpringLayout.WEST, userPanel);
        sl_userPanel.putConstraint(SpringLayout.EAST, lblUsername, -10, SpringLayout.EAST, userPanel);
        userPanel.add(lblUsername);
        
        JLabel lblName = new JLabel("Name");
        lblName.setHorizontalAlignment(SwingConstants.RIGHT);
        sl_userPanel.putConstraint(SpringLayout.NORTH, lblName, 5, SpringLayout.SOUTH, lblUsername);
        sl_userPanel.putConstraint(SpringLayout.WEST, lblName, 10, SpringLayout.WEST, userPanel);
        sl_userPanel.putConstraint(SpringLayout.EAST, lblName, -10, SpringLayout.EAST, userPanel);
        userPanel.add(lblName);
        
        JButton btnlogout = new JButton("Logout");
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
			BufferedImage logo = ImageIO.read(getClass().getResource("/images/vvv.png"));
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
        
        JButton btnAddFiles = new JButton("Add Files");
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnAddFiles, 5, SpringLayout.SOUTH, logoPanel);
        sl_contentPane.putConstraint(SpringLayout.WEST, btnAddFiles, 10, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 5, SpringLayout.SOUTH, btnAddFiles);
        
        fileTable = new JTable();
        fileTable.getTableHeader().setReorderingAllowed(false);
        scrollPane.setColumnHeaderView(fileTable);
        scrollPane.setViewportView(fileTable);
        fileTable.setModel(yuploaderTableModel);
        
        
        contentPane.add(btnAddFiles);
        
        JButton btnUploadFiles = new JButton("Upload Files");
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnUploadFiles, 5, SpringLayout.SOUTH, logoPanel);
        sl_contentPane.putConstraint(SpringLayout.EAST, btnUploadFiles, -5, SpringLayout.EAST, contentPane);
        contentPane.add(btnUploadFiles);
    }
    
}
