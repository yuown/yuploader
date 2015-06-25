package yuown.yuploader.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;

import javax.swing.JDesktopPane;

public class YuploaderApp {

	private JFrame frame;
	
	private JDesktopPane desktopPane;
	
	private Upload uploadFilesFrame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					YuploaderApp window = new YuploaderApp();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

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
		
		createInternalWindows();
	}

	private void createInternalWindows() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					uploadFilesFrame = new Upload("Upload Files", true, true, true, true);
					uploadFilesFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	protected void launchUploadFiles(ActionEvent e) {
		desktopPane.add(uploadFilesFrame);
	}

}
