package yuown.yuploader.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
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
	
	private JButton btnUploadFiles;
	
	private JButton btnAddFiles;

	@Autowired
	private Helper helper;

	@Autowired
	private FTPHelperBean ftpHelperBean;

	private long lastAccess = -1L;

	private FTPClient ftp = new FTPClient();

	private boolean loginSuccess = false;
	private boolean logoutConfirmed = false;

	private boolean inProgress;

	/**
	 * Create the frame.
	 */
	private void defaultConstructor() {
		setBounds(100, 100, 1055, 527);
		getContentPane().setLayout(springLayout);

		btnAddFiles = new JButton("Add Files");
		btnAddFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				chooseFiles(event);
			}
		});
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, btnAddFiles);
		springLayout.putConstraint(SpringLayout.NORTH, btnAddFiles, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, btnAddFiles, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(btnAddFiles);

		btnUploadFiles = new JButton("Upload Files");
		btnUploadFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent uploadActionEvent) {
				YuploaderWorker w = new YuploaderWorker(UploadWindow.this);
				w.execute();
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
		table.getTableHeader().setReorderingAllowed(false);
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
		// final DefaultTableModel tM = (DefaultTableModel)
		// this.table.getModel();
		// int rowCount = tM.getRowCount();
		// for (int i = 0; i < rowCount; i++) {
		// final FileObject fo = (FileObject) tM.getValueAt(i, 0);
		// if (fo.getStatus() == Status.ADDED) {
		// fo.setStatus(Status.IN_PROGRESS);
		// tM.setValueAt(fo.getStatus(), i, 3);
		// ftpHelperBean.uploadFile(fo.getFullPath(), tM, i, 5);
		// fo.setStatus(Status.COMPLETED);
		// tM.setValueAt(fo.getStatus(), i, 3);
		// }
		// }

		Date d = new Date();
		String DDMMYYYY = (d.getDate() < 10 ? "0" + d.getDate() : new StringBuilder(String.valueOf(d.getDate())).toString()) + (d.getMonth() < 10 ? "0" + (d.getMonth() + 1) : new StringBuilder(String.valueOf(d.getMonth() + 1)).toString()) + (d.getYear() + 1900);
		boolean exists = false;
		try {
			if (System.currentTimeMillis() - this.lastAccess > 180000L) {
				System.out.println("Timeout Occured, hence Reconnecting!");

				this.ftp.connect((String) this.ftpHelperBean.getFtpHost());
				int reply = this.ftp.getReplyCode();
				if (!FTPReply.isPositiveCompletion(reply)) {
					this.ftp.disconnect();
					System.err.println("FTP server refused connection.");
				} else {
					if (!this.ftp.login((String) this.ftpHelperBean.getFtpUsername(), (String) this.ftpHelperBean.getFtpPassword())) {
						this.ftp.logout();
						helper.alert(this, "Problem with FTP Server Credentials, Contact Admin.");
						return;
					}
					this.loginSuccess = true;
					System.out.println("Curr Buff Size: " + this.ftp.getBufferSize());
					try {
						this.ftp.enterLocalPassiveMode();
						this.ftp.setAutodetectUTF8(true);
						this.ftp.setFileType(2, 2);
						this.ftp.setBufferSize(ftpHelperBean.getBufferSize());
						this.ftp.setFileTransferMode(2);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					toggleLoginCtrls(false);
				}
			}
			exists = this.ftp.changeWorkingDirectory(ftpHelperBean.getFtpPath());
			if (!exists) {
				this.ftp.makeDirectory(ftpHelperBean.getFtpPath());
				this.ftp.changeWorkingDirectory(ftpHelperBean.getFtpPath());
			}
			exists = this.ftp.changeWorkingDirectory(ftpHelperBean.getUserName());
			if (!exists) {
				this.ftp.makeDirectory(ftpHelperBean.getUserName());
				this.ftp.changeWorkingDirectory(ftpHelperBean.getUserName());
			}
			exists = this.ftp.changeWorkingDirectory(DDMMYYYY);
			if (!exists) {
				this.ftp.makeDirectory(DDMMYYYY);
				this.ftp.changeWorkingDirectory(DDMMYYYY);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		this.btnUploadFiles.setEnabled(false);
		this.btnAddFiles.setEnabled(false);
		this.inProgress = true;
		final DefaultTableModel tM = (DefaultTableModel) this.table.getModel();
		int rowCount = tM.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			final int row = i;
			final FileObject fo = (FileObject) tM.getValueAt(i, 0);
			try {
				boolean cd = this.ftp.changeWorkingDirectory(ftpHelperBean.getFtpPath() + ftpHelperBean.getUserName() + "/" + DDMMYYYY + "/" + fo.getFolder());
				if (!cd) {
					this.ftp.changeWorkingDirectory(ftpHelperBean.getFtpPath() + ftpHelperBean.getUserName() + "/" + DDMMYYYY);
					this.ftp.makeDirectory(fo.getFolder());
					this.ftp.changeWorkingDirectory(ftpHelperBean.getFtpPath() + ftpHelperBean.getUserName() + "/" + DDMMYYYY + "/" + fo.getFolder());
				}
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			File f = new File(fo.getFullPath());
			try {
				if ((f.exists()) && (Status.COMPLETED != fo.getStatus())) {
					System.out.println("File: " + fo.getFullPath());
					tM.setValueAt(Status.IN_PROGRESS, row, 3);

					this.ftp.setCopyStreamListener(new CopyStreamListener() {
						private long kbsTotal = 0L;
						private double percentCompleted;
						private long time = System.currentTimeMillis();
						private long start = System.currentTimeMillis();

						public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
							long s = fo.getSize();
							this.percentCompleted = ((int) (totalBytesTransferred / s * 10000.0D) / 100.0D);

							long d = System.currentTimeMillis() - this.time;
							if (d > 1000L) {
								this.time = System.currentTimeMillis();
								long currentkBRate = this.kbsTotal / 1024L;
								tM.setValueAt(currentkBRate + " KB/s", row, 4);
								tM.setValueAt((this.time - this.start) / 1000L, row, 5);
								this.kbsTotal = 0L;
							} else {
								this.kbsTotal += bytesTransferred;
							}
							tM.setValueAt(this.percentCompleted + " %", row, 2);
						}

						public void bytesTransferred(CopyStreamEvent event) {
							bytesTransferred(event.getTotalBytesTransferred(), event.getBytesTransferred(), event.getStreamSize());
						}
					});
					BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
					this.ftp.storeFile(fo.getFileName(), bis);

					bis.close();

					tM.setValueAt(Status.COMPLETED, row, 3);
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		this.lastAccess = System.currentTimeMillis();
		this.btnUploadFiles.setEnabled(true);
		this.btnAddFiles.setEnabled(true);
		this.inProgress = false;
	}

	private void toggleLoginCtrls(boolean b) {
		
	}

	protected void removeFiles(KeyEvent keyPressEvent) {
		if (keyPressEvent.getKeyCode() == KeyEvent.VK_DELETE || keyPressEvent.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			int[] selRows = this.table.getSelectedRows();
			DefaultTableModel tM = (DefaultTableModel) this.table.getModel();
			for (int i = selRows.length - 1; i >= 0; i--) {
				FileObject fileSelected = (FileObject) tM.getValueAt(selRows[i], 0);
				if (Status.IN_PROGRESS != fileSelected.getStatus()) {
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
		int command = this.fileChooser.showOpenDialog(this);
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

	public JTable getTable() {
		return table;
	}

}
