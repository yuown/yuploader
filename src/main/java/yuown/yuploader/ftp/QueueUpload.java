package yuown.yuploader.ftp;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.io.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import yuown.yuploader.model.FileObject;
import yuown.yuploader.model.Status;
import yuown.yuploader.model.User;
import yuown.yuploader.model.YuploaderTableModel;
import yuown.yuploader.ui.Client;
import yuown.yuploader.util.Helper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;

import javax.swing.SwingWorker;

@Component
@Scope("prototype")
public class QueueUpload extends SwingWorker<Integer, Integer> {
    @Autowired
    private YuploaderTableModel yuploaderTableModel;
    @Autowired
    private FTPClient ftpClient;
    @Autowired
    private Client client;
    @Autowired
    private StreamListener streamListener;
    @Autowired
    private Helper helper;

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

    @Autowired
    private User userObject;

    public void submitToQueue() {
        client.toggleLogin(false);
        System.out.println("Uploader Worker Started: " + System.currentTimeMillis());

        int rowCount = this.yuploaderTableModel.getRowCount();
        for (int row = 0; (row < rowCount); row++) {
            FileObject file = (FileObject) this.yuploaderTableModel.getValueAt(row, 0);
            File f = new File(file.getFullPath());
            if ((f.exists()) && (isEligibleToStartUpload(file))) {
                InputStream reader = getReader(f, file);
                try {
                    markUploadInProgress(file, row);
                    client.togglePause(true);
                    streamListener.setReader(reader);
                    if (file.getOffset() > 0) {
                        ftpClient.appendFile(file.getFileName(), reader);
                    } else {
                        ftpClient.storeFile(file.getFileName(), reader);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                } finally {
                    Util.closeQuietly(reader);
                }
            }
        }
        client.togglePause(false);
        client.toggleLogin(true);
    }

    private long getOffsetFromServer(FileObject file) throws IOException {
        long l = -1;
        FTPFile[] ftpFile = ftpClient.listFiles();
        for (int i = 0; i < ftpFile.length; i++) {
            if (StringUtils.equals(ftpFile[i].getName(), file.getFileName())) {
                l = ftpFile[i].getSize();
                break;
            }
        }
        if (l < 0) {
            l = 0;
        }
        return l;
    }

    @SuppressWarnings("resource")
    protected InputStream getReader(File f, FileObject file) {
        InputStream reader = null;
        try {
            if (file.getOffset() > 0) {
                file.setOffset(getOffsetFromServer(file));
            }
            RandomAccessFile raf = new RandomAccessFile(f, "r");
            reader = Channels.newInputStream(raf.getChannel().position(file.getOffset()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reader;
    }

    private void markUploadInProgress(FileObject file, int row) {
        this.yuploaderTableModel.setValueAt(Status.IN_PROGRESS, row, 3);
        file.setStatus(Status.IN_PROGRESS);
        ftpClient.setCopyStreamListener(streamListener);
        this.streamListener.setRow(row);
    }

    protected boolean isEligibleToStartUpload(FileObject file) {
        return !StringUtils.equals(Status.COMPLETED.toString(), file.getStatus().toString());
    }

    protected Integer doInBackground() throws Exception {
        submitToQueue();
        return Integer.valueOf(0);
    }
}
