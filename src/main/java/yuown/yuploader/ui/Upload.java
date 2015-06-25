package yuown.yuploader.ui;

import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

public class Upload extends JInternalFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7925476333709078140L;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Upload frame = new Upload();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private Upload() {
		defaultConstructor();
	}
	
	/**
	 * Create the frame.
	 */
	private void defaultConstructor() {
		setBounds(100, 100, 1055, 527);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		JButton btnAddFiles = new JButton("Add Files");
		springLayout.putConstraint(SpringLayout.NORTH, btnAddFiles, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, btnAddFiles, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(btnAddFiles);
		
		JButton btnUploadFiles = new JButton("Upload Files");
		springLayout.putConstraint(SpringLayout.NORTH, btnUploadFiles, 0, SpringLayout.NORTH, btnAddFiles);
		springLayout.putConstraint(SpringLayout.WEST, btnUploadFiles, 6, SpringLayout.EAST, btnAddFiles);
		getContentPane().add(btnUploadFiles);
		
		JScrollPane scrollPane = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 6, SpringLayout.SOUTH, btnAddFiles);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, 1021, SpringLayout.WEST, getContentPane());
		getContentPane().add(scrollPane);
	}
	
	public Upload(String title,
                      boolean resizable,
                      boolean closable,
                      boolean maximizable,
                      boolean iconifiable) {
		super(title, resizable, closable, maximizable, iconifiable);
		defaultConstructor();
	}
}
