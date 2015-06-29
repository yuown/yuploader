package yuown.yuploader.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import yuown.yuploader.ftp.FTPHelperBean;
import yuown.yuploader.model.FileObject;
import yuown.yuploader.model.Status;
import yuown.yuploader.util.Helper;

@Component
public class UploadWindow extends JInternalFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7925476333709078140L;
	
	private SpringLayout springLayout = new SpringLayout();
	
	private JScrollPane scrollPane = new JScrollPane();
	
	private JTable table;
	
	private JFileChooser fileChooser;
	
	@Autowired
	private Helper helper;
	
	@Autowired
	private FTPHelperBean ftpHelperBean;

	/**
	 * Create the frame.
	 */
	private void defaultConstructor() {
		setBounds(100, 100, 1055, 527);
		getContentPane().setLayout(springLayout);
		
		JButton btnAddFiles = new JButton("Add Files");
		btnAddFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				chooseFiles(event);
			}
		});
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, btnAddFiles);
		springLayout.putConstraint(SpringLayout.NORTH, btnAddFiles, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, btnAddFiles, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(btnAddFiles);
		
		JButton btnUploadFiles = new JButton("Upload Files");
		btnUploadFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent uploadActionEvent) {
				uploadFiles(uploadActionEvent);
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, btnUploadFiles, 0, SpringLayout.NORTH, btnAddFiles);
		springLayout.putConstraint(SpringLayout.EAST, btnUploadFiles, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(btnUploadFiles);
		
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 10, SpringLayout.SOUTH, btnAddFiles);
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, btnUploadFiles);
		getContentPane().add(scrollPane);
		
		table = new JTable();
		table.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent keyPressEvent) {
				removeFiles(keyPressEvent);
			}
		});
		scrollPane.setViewportView(table);
		
		table.setModel(new DefaultTableModel(new Object[0][],

		new String[] { "File Name", "Size", "Progress", "Status", "Speed", "Time (Seconds)" }) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 278353099841109487L;
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = { FileObject.class, String.class, String.class, String.class, Object.class, String.class };

			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int columnIndex) {
				return this.columnTypes[columnIndex];
			}

			boolean[] columnEditables = { false, false, false, false, false, false };

			public boolean isCellEditable(int row, int column) {
				return this.columnEditables[column];
			}
		});
		
		fileChooser = new JFileChooser();
	}
	
	protected void uploadFiles(ActionEvent uploadActionEvent) {
		final DefaultTableModel tM = (DefaultTableModel) this.table.getModel();
		int rowCount = tM.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			final FileObject fo = (FileObject) tM.getValueAt(i, 0);
			if (fo.getStatus() == Status.ADDED) {
				fo.setStatus(Status.IN_PROGRESS);
				tM.setValueAt(fo.getStatus(), i, 3);
				ftpHelperBean.uploadFile(fo.getFullPath(), tM, i, 5);
				fo.setStatus(Status.COMPLETED);
				tM.setValueAt(fo.getStatus(), i, 3);
			}
		}
	}

	protected void removeFiles(KeyEvent keyPressEvent) {
		if (keyPressEvent.getKeyCode() == KeyEvent.VK_DELETE || keyPressEvent.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			int[] selRows = this.table.getSelectedRows();
			DefaultTableModel tM = (DefaultTableModel) this.table.getModel();
			for (int i = selRows.length - 1; i >= 0; i--) {
				FileObject fileSelected = (FileObject) tM.getValueAt(selRows[i], 0);
				if(Status.IN_PROGRESS != fileSelected.getStatus()) {
					tM.removeRow(selRows[i]);
				}
			}
		}
	}

	public UploadWindow(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable) {
		super(title, resizable, closable, maximizable, iconifiable);
		defaultConstructor();
	}
	
	protected void chooseFiles(ActionEvent e) {
		this.fileChooser.setMultiSelectionEnabled(true);
		this.fileChooser.setFileSelectionMode(1);
		int command = this.fileChooser.showOpenDialog(null);
		if (command == 0) {
			File[] f = this.fileChooser.getSelectedFiles();
			for (int i = 0; i < f.length; i++) {
				if (f[i].isDirectory()) {
					File[] innerFiles = f[i].listFiles();
					for (int j = 0; j < innerFiles.length; j++) {
						if (innerFiles[j].isFile()) {
							FileObject fo = new FileObject();
							fo.setFileName(innerFiles[j].getName());
							fo.setFullPath(innerFiles[j].getAbsolutePath());
							fo.setProgress("0 %");
							fo.setStatus(Status.ADDED);
							fo.setSize(innerFiles[j].length());
							fo.setFolder(f[i].getName());
							addFiletoTable(fo);
						}
					}
				}
			}
		}
	}

	private void addFiletoTable(FileObject fo) {
		DefaultTableModel dtm = (DefaultTableModel) this.table.getModel();
		if (!contains(fo)) {
			dtm.addRow(new Object[] { fo, fo.getKBSize(), fo.getProgress(), fo.getStatus() });
		}
	}

	protected boolean contains(FileObject fo) {
		boolean contains = false;
		DefaultTableModel tM = (DefaultTableModel) this.table.getModel();
		int rowCount = tM.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			if (tM.getValueAt(i, 0).equals(fo)) {
				contains = true;
				break;
			}
		}
		return contains;
	}
}
