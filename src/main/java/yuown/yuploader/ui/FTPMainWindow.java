package yuown.yuploader.ui;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;

import yuown.yuploader.model.FileObject;
import yuown.yuploader.model.Status;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class FTPMainWindow
{
  private JFrame frmVvvFileUploader;
  private JTextField txtUserName;
  private JPasswordField txtPassword;
  private JTable tableFileList;
  private JButton btnAddFiles;
  private JButton btnUploadFiles;
  private JButton btnRemoveSelected;
  private JButton btnLogin;
  private JButton btnLogout;
  private boolean loginSuccess = false;
  private boolean logoutConfirmed = false;
  private FTPClient ftp;
  private boolean inProgress = false;
  private JFileChooser jfc;
  private static FTPMainWindow window;
  private long lastAccess = -1L;
  private Map<String, String> uMap = null;
  private JScrollPane scrollPaneForTable;
  
  public static void main(String[] args)
  {
    EventQueue.invokeLater(new Runnable()
    {
      public void run()
      {
        try
        {
          FTPMainWindow.window = new FTPMainWindow();
          FTPMainWindow.window.frmVvvFileUploader.setVisible(true);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    });
  }
  
  public FTPMainWindow()
  {
    initialize();
  }
  
  private void initialize()
  {
    this.frmVvvFileUploader = new JFrame();
    this.frmVvvFileUploader.addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent e)
      {
        FTPMainWindow.this.handleExit(e);
      }
    });
    this.frmVvvFileUploader.addComponentListener(new ComponentAdapter()
    {
      public void componentResized(ComponentEvent e)
      {
        FTPMainWindow.this.windowResized(e);
      }
    });
    this.frmVvvFileUploader.setIconImage(Toolkit.getDefaultToolkit().getImage(FTPMainWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/TreeOpen.gif")));
    this.frmVvvFileUploader.setTitle("VVV File Uploader");
    this.frmVvvFileUploader.setBounds(100, 100, 800, 600);
    this.frmVvvFileUploader.setDefaultCloseOperation(3);
    this.frmVvvFileUploader.getContentPane().setLayout(null);
    
    JLabel lblUsername = new JLabel("Username: ");
    lblUsername.setHorizontalAlignment(4);
    lblUsername.setBounds(310, 9, 69, 14);
    this.frmVvvFileUploader.getContentPane().add(lblUsername);
    
    this.txtUserName = new JTextField();
    this.txtUserName.addKeyListener(new KeyAdapter()
    {
      public void keyPressed(KeyEvent e)
      {
        if (e.getKeyCode() == 10) {
          FTPMainWindow.this.login(null);
        }
      }
    });
    this.txtUserName.setBounds(382, 6, 133, 20);
    this.frmVvvFileUploader.getContentPane().add(this.txtUserName);
    this.txtUserName.setColumns(10);
    
    JLabel lblPassword = new JLabel("Password: ");
    lblPassword.setHorizontalAlignment(4);
    lblPassword.setBounds(312, 34, 67, 14);
    this.frmVvvFileUploader.getContentPane().add(lblPassword);
    
    this.txtPassword = new JPasswordField();
    this.txtPassword.addKeyListener(new KeyAdapter()
    {
      public void keyPressed(KeyEvent e)
      {
        if (e.getKeyCode() == 10) {
          FTPMainWindow.this.login(null);
        }
      }
    });
    this.txtPassword.setBounds(382, 31, 133, 20);
    this.frmVvvFileUploader.getContentPane().add(this.txtPassword);
    
    this.btnLogin = new JButton("Login");
    this.btnLogin.addKeyListener(new KeyAdapter()
    {
      public void keyPressed(KeyEvent e)
      {
        if ((e.getKeyCode() == 10) || (e.getKeyCode() == 32)) {
          FTPMainWindow.this.login(null);
        }
      }
    });
    this.btnLogin.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        FTPMainWindow.this.login(e);
      }
    });
    this.btnLogin.setBounds(525, 9, 91, 23);
    this.frmVvvFileUploader.getContentPane().add(this.btnLogin);
    
    this.btnAddFiles = new JButton("Add Files");
    this.btnAddFiles.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        FTPMainWindow.this.chooseFiles(e);
      }
    });
    this.btnAddFiles.setBounds(642, 39, 140, 23);
    this.frmVvvFileUploader.getContentPane().add(this.btnAddFiles);
    
    this.btnUploadFiles = new JButton("Upload Files");
    this.btnUploadFiles.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        //Worker w = new Worker(FTPMainWindow.window);
        //w.execute();
      }
    });
    this.btnUploadFiles.setBounds(642, 69, 140, 23);
    this.frmVvvFileUploader.getContentPane().add(this.btnUploadFiles);
    
    this.btnRemoveSelected = new JButton("Remove Selected");
    this.btnRemoveSelected.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        FTPMainWindow.this.removeSelected(e);
      }
    });
    this.btnRemoveSelected.setBounds(642, 9, 140, 23);
    this.frmVvvFileUploader.getContentPane().add(this.btnRemoveSelected);
    
    this.scrollPaneForTable = new JScrollPane();
    this.scrollPaneForTable.setBounds(5, 177, 785, 393);
    this.frmVvvFileUploader.getContentPane().add(this.scrollPaneForTable);
    
    this.tableFileList = new JTable();
    this.scrollPaneForTable.setViewportView(this.tableFileList);
    this.tableFileList.setSelectionMode(2);
    this.tableFileList.setModel(new DefaultTableModel(
      new Object[0][], 
      
      new String[] {
      "File Name", "Size", "Progress", "Status", "Speed", "Time (Seconds)" })
      {
        Class[] columnTypes = {
          FileObject.class, String.class, String.class, String.class, Object.class, String.class };
        
        public Class getColumnClass(int columnIndex)
        {
          return this.columnTypes[columnIndex];
        }
        
        boolean[] columnEditables = {
          true };
        
        public boolean isCellEditable(int row, int column)
        {
          return this.columnEditables[column];
        }
      });
    this.btnLogout = new JButton("Logout");
    this.btnLogout.addKeyListener(new KeyAdapter()
    {
      public void keyPressed(KeyEvent e)
      {
        if ((e.getKeyCode() == 10) || (e.getKeyCode() == 32)) {
          FTPMainWindow.this.logout(null);
        }
      }
    });
    this.btnLogout.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        FTPMainWindow.this.logout(e);
      }
    });
    this.btnLogout.setBounds(525, 9, 91, 23);
    this.frmVvvFileUploader.getContentPane().add(this.btnLogout);
    try
    {
      BufferedImage myPicture = ImageIO.read(getClass().getResource("/vvv.png"));
      JLabel forLogo = new JLabel(new ImageIcon(myPicture));
      forLogo.setBounds(5, 6, 217, 160);
      this.frmVvvFileUploader.getContentPane().add(forLogo);
      
      JLabel lblContactBelowFor = new JLabel("Contact Below For Any issues/ support:");
      lblContactBelowFor.setVerticalAlignment(1);
      lblContactBelowFor.setBounds(232, 75, 240, 19);
      this.frmVvvFileUploader.getContentPane().add(lblContactBelowFor);
      
      JLabel lblMobile = new JLabel("Mobile: +91-9900034758");
      lblMobile.setVerticalAlignment(1);
      lblMobile.setBounds(232, 125, 240, 19);
      this.frmVvvFileUploader.getContentPane().add(lblMobile);
      
      JLabel lblWebsiteHttpvasanthvideovisioncom = new JLabel("Website: http://vasanthvideovision.com/");
      lblWebsiteHttpvasanthvideovisioncom.setVerticalAlignment(1);
      lblWebsiteHttpvasanthvideovisioncom.setBounds(232, 100, 240, 19);
      this.frmVvvFileUploader.getContentPane().add(lblWebsiteHttpvasanthvideovisioncom);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    this.jfc = new JFileChooser();
    
    disableAllControls();
  }
  
  protected void windowResized(ComponentEvent e)
  {
    this.scrollPaneForTable.setBounds(5, 177, this.frmVvvFileUploader.getWidth() - 20, this.frmVvvFileUploader.getHeight() - 210);
  }
  
  protected void handleExit(WindowEvent e)
  {
    System.out.println("Window Closed!");
    deleteAllRows();
    try
    {
      if ((this.ftp != null) && (this.ftp.isConnected())) {
        this.ftp.disconnect();
      }
    }
    catch (Exception e1)
    {
      e1.printStackTrace();
    }
  }
  
  protected void uploadFiles(ActionEvent e)
  {
    Date d = new Date();
    String DDMMYYYY = (d.getDate() < 10 ? "0" + d.getDate() : new StringBuilder(String.valueOf(d.getDate())).toString()) + (
      d.getMonth() < 10 ? "0" + (d.getMonth() + 1) : new StringBuilder(String.valueOf(d.getMonth() + 1)).toString()) + (
      d.getYear() + 1900);
    boolean exists = false;
    try
    {
      if (System.currentTimeMillis() - this.lastAccess > 180000L)
      {
        System.out.println("Timeout Occured, hence Reconnecting!");
        
        this.ftp.connect((String)this.uMap.get("ftp.host"));
        int reply = this.ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply))
        {
          this.ftp.disconnect();
          System.err.println("FTP server refused connection.");
        }
        else
        {
          if (!this.ftp.login((String)this.uMap.get("ftp.user"), (String)this.uMap.get("ftp.pwd")))
          {
            this.ftp.logout();
            JOptionPane.showMessageDialog(null, "Problem with FTP Server Credentials, Contact Admin.");
            return;
          }
          this.loginSuccess = true;
          System.out.println("Curr Buff Size: " + this.ftp.getBufferSize());
          try
          {
            this.ftp.enterLocalPassiveMode();
            this.ftp.setAutodetectUTF8(true);
            this.ftp.setFileType(2, 2);
            this.ftp.setFileTransferMode(2);
          }
          catch (IOException e1)
          {
            e1.printStackTrace();
          }
          toggleLoginCtrls(false);
        }
      }
      exists = this.ftp.changeWorkingDirectory("uploadedFiles");
      if (!exists)
      {
        this.ftp.makeDirectory("uploadedFiles");
        this.ftp.changeWorkingDirectory("uploadedFiles");
      }
      exists = this.ftp.changeWorkingDirectory(this.txtUserName.getText());
      if (!exists)
      {
        this.ftp.makeDirectory(this.txtUserName.getText());
        this.ftp.changeWorkingDirectory(this.txtUserName.getText());
      }
      exists = this.ftp.changeWorkingDirectory(DDMMYYYY);
      if (!exists)
      {
        this.ftp.makeDirectory(DDMMYYYY);
        this.ftp.changeWorkingDirectory(DDMMYYYY);
      }
    }
    catch (Exception e1)
    {
      e1.printStackTrace();
    }
    this.btnUploadFiles.setEnabled(false);
    this.btnRemoveSelected.setEnabled(false);
    this.inProgress = true;
    final DefaultTableModel tM = (DefaultTableModel)this.tableFileList.getModel();
    int rowCount = tM.getRowCount();
    for (int i = 0; i < rowCount; i++)
    {
      final int row = i;
      final FileObject fo = (FileObject)tM.getValueAt(i, 0);
      try
      {
        boolean cd = this.ftp.changeWorkingDirectory("/uploadedFiles/" + this.txtUserName.getText() + "/" + DDMMYYYY + "/" + fo.getFolder());
        if (!cd)
        {
          this.ftp.changeWorkingDirectory("/uploadedFiles/" + this.txtUserName.getText() + "/" + DDMMYYYY);
          this.ftp.makeDirectory(fo.getFolder());
          this.ftp.changeWorkingDirectory("/uploadedFiles/" + this.txtUserName.getText() + "/" + DDMMYYYY + "/" + fo.getFolder());
        }
      }
      catch (IOException e2)
      {
        e2.printStackTrace();
      }
      File f = new File(fo.getFullPath());
      try
      {
        if ((f.exists()) && (Status.COMPLETED != fo.getStatus()))
        {
          System.out.println("File: " + fo.getFullPath());
          tM.setValueAt("In Progress", row, 3);
          
          this.ftp.setCopyStreamListener(new CopyStreamListener()
          {
            private long kbsTotal = 0L;
            private double percentCompleted;
            private long time = System.currentTimeMillis();
            private long start = System.currentTimeMillis();
            
            public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize)
            {
              long s = fo.getSize();
              this.percentCompleted = ((int)(totalBytesTransferred / s * 10000.0D) / 100.0D);
              
              long d = System.currentTimeMillis() - this.time;
              if (d > 1000L)
              {
                this.time = System.currentTimeMillis();
                long currentkBRate = this.kbsTotal / 1024L;
                tM.setValueAt(currentkBRate + " KB/s", row, 4);
                tM.setValueAt((this.time - this.start) / 1000L, row, 5);
                this.kbsTotal = 0L;
              }
              else
              {
                this.kbsTotal += bytesTransferred;
              }
              tM.setValueAt(this.percentCompleted + " %", row, 2);
            }
            
            public void bytesTransferred(CopyStreamEvent event)
            {
              bytesTransferred(event.getTotalBytesTransferred(), event.getBytesTransferred(), event.getStreamSize());
            }
          });
          BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
          this.ftp.storeFile(fo.getFileName(), bis);
          
          bis.close();
          
          tM.setValueAt(Status.COMPLETED, row, 3);
        }
      }
      catch (FileNotFoundException e1)
      {
        e1.printStackTrace();
      }
      catch (IOException e1)
      {
        e1.printStackTrace();
      }
    }
    this.lastAccess = System.currentTimeMillis();
    this.btnUploadFiles.setEnabled(true);
    this.btnRemoveSelected.setEnabled(true);
    this.inProgress = false;
  }
  
  protected void chooseFiles(ActionEvent e)
  {
    this.jfc.setMultiSelectionEnabled(true);
    this.jfc.setFileSelectionMode(1);
    int command = this.jfc.showOpenDialog(null);
    if (command == 0)
    {
      File[] f = this.jfc.getSelectedFiles();
      for (int i = 0; i < f.length; i++) {
        if (f[i].isDirectory())
        {
          File[] innerFiles = f[i].listFiles();
          for (int j = 0; j < innerFiles.length; j++) {
            if (innerFiles[j].isFile())
            {
              FileObject fo = new FileObject();
              fo.setFileName(innerFiles[j].getName());
              fo.setFullPath(innerFiles[j].getAbsolutePath());
              fo.setProgress("0 %");
              fo.setSize(innerFiles[j].length());
              fo.setFolder(f[i].getName());
              addFiletoTable(fo);
            }
          }
        }
      }
    }
  }
  
  protected void logout(ActionEvent e)
  {
    int dialogResult = 0;
    if (this.inProgress) {
      dialogResult = JOptionPane.showConfirmDialog(null, "File Upload is still in Progress,  are you sure to Cancel all the Uploads and Logout?", "Warning", 0);
    }
    if (dialogResult == 0) {
      this.logoutConfirmed = true;
    } else {
      this.logoutConfirmed = false;
    }
    if (this.logoutConfirmed)
    {
      disableAllControls();
      deleteAllRows();
      try
      {
        this.ftp.disconnect();
      }
      catch (Exception e1)
      {
        e1.printStackTrace();
      }
      this.btnLogin.setVisible(this.logoutConfirmed);
      this.btnLogout.setVisible(!this.logoutConfirmed);
      toggleLoginCtrls(true);
    }
  }
  
  protected void login(ActionEvent e)
  {
    this.loginSuccess = true;
    
//    this.ftp = new FTPClient();
//    try
//    {
//      String u = this.txtUserName.getText();
//      String p = this.txtPassword.getText();
//      if ((!Utils.isStringEmpty(u)) && (!Utils.isStringEmpty(p)))
//      {
//        this.uMap = DBConnector.validateUser(u, p);
//      }
//      else
//      {
//        JOptionPane.showMessageDialog(null, "Username/ Password cannot be empty!");
//        return;
//      }
//      if (!((String)this.uMap.get("validUser")).equals("true"))
//      {
//        JOptionPane.showMessageDialog(null, "Username/ password is invalid or disabled");
//        return;
//      }
//      this.ftp.connect((String)this.uMap.get("ftp.host"));
//      int reply = this.ftp.getReplyCode();
//      if (!FTPReply.isPositiveCompletion(reply))
//      {
//        this.ftp.disconnect();
//        System.err.println("FTP server refused connection.");
//      }
//      else
//      {
//        if (!this.ftp.login((String)this.uMap.get("ftp.user"), (String)this.uMap.get("ftp.pwd")))
//        {
//          this.ftp.logout();
//          JOptionPane.showMessageDialog(null, "Problem with FTP Server Credentials, Contact Admin.");
//          return;
//        }
//        this.loginSuccess = true;
//        toggleLoginCtrls(false);
//      }
//    }
//    catch (IOException exp)
//    {
//      if (this.ftp.isConnected()) {
//        try
//        {
//          this.ftp.disconnect();
//        }
//        catch (IOException localIOException1) {}
//      }
//      JOptionPane.showMessageDialog(null, "Could not connect to server.");
//      exp.printStackTrace();
//    }
    if (this.loginSuccess)
    {
      this.btnLogin.setVisible(!this.loginSuccess);
      this.btnLogout.setVisible(this.loginSuccess);
      enableAllControls();
    }
  }
  
  private void disableAllControls()
  {
    toggleControls(false);
  }
  
  private void enableAllControls()
  {
    toggleControls(true);
  }
  
  private void toggleControls(boolean flag)
  {
    this.tableFileList.setEnabled(flag);
    this.btnAddFiles.setEnabled(flag);
    this.btnRemoveSelected.setEnabled(flag);
    this.btnUploadFiles.setEnabled(flag);
    this.btnLogin.setVisible(!flag);
    this.btnLogout.setVisible(flag);
  }
  
  private void deleteAllRows()
  {
    DefaultTableModel tM = (DefaultTableModel)this.tableFileList.getModel();
    int rowCount = tM.getRowCount();
    for (int i = rowCount - 1; i >= 0; i--) {
      tM.removeRow(i);
    }
  }
  
  protected void removeSelected(ActionEvent e)
  {
    int[] selRows = this.tableFileList.getSelectedRows();
    
    DefaultTableModel tM = (DefaultTableModel)this.tableFileList.getModel();
    int rowCount = tM.getRowCount();
    for (int i = selRows.length - 1; i >= 0; i--) {
      tM.removeRow(selRows[i]);
    }
  }
  
  public boolean isStringEmpty(String string)
  {
    return (string == null) || (string.trim().length() == 0);
  }
  
  protected void addFiletoTable(FileObject fo)
  {
    DefaultTableModel dtm = (DefaultTableModel)this.tableFileList.getModel();
    if (!contains(fo)) {
      dtm.addRow(new Object[] { fo, Long.valueOf(fo.getSize()), fo.getProgress(), "Added" });
    }
  }
  
  protected boolean contains(FileObject fo)
  {
    boolean f = false;
    DefaultTableModel tM = (DefaultTableModel)this.tableFileList.getModel();
    int rowCount = tM.getRowCount();
    for (int i = 0; i < rowCount; i++) {
      if (tM.getValueAt(i, 0).equals(fo))
      {
        f = true;
        break;
      }
    }
    return f;
  }
  
  protected void toggleLoginCtrls(boolean f)
  {
    this.txtUserName.setEnabled(f);
    this.txtPassword.setEnabled(f);
  }
}
