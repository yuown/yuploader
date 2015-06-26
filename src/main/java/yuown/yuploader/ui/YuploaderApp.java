package yuown.yuploader.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import yuown.yuploader.ftp.FTPHelperBean;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class YuploaderApp {
    
    @Value("${yuploader.app.title}")
    private String appTitle;

	private JFrame frame;
	
    private JDesktopPane desktopPane;
	
    @Autowired
	private UploadWindow uploadWindow;
	
	@Autowired
	private FTPHelperBean ftpHelperBean;

	/**
	 * Create the application.
	 */
	public YuploaderApp() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 773, 466);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmUpload = new JMenuItem("Upload");
		mntmUpload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				launchUploadFiles(e);
			}
		});
		mnFile.add(mntmUpload);
		
		JMenu mnView = new JMenu("View");
		menuBar.add(mnView);
		
		JMenuItem mntmFiles = new JMenuItem("Files");
		mnView.add(mntmFiles);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mnHelp.add(mntmAbout);
		//frame.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		desktopPane = new JDesktopPane();
		frame.add(desktopPane);
	}

	protected void launchUploadFiles(ActionEvent e) {
	    if(!uploadWindow.isVisible()) {
    	    uploadWindow.setVisible(true);
    		desktopPane.add(uploadWindow);
	    }
	}
	
	public JFrame getFrame() {
	    frame.setTitle(appTitle);
        return frame;
    }
}
