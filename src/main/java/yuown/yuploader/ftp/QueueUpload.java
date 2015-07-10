package yuown.yuploader.ftp;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import yuown.yuploader.model.FileObject;
import yuown.yuploader.model.Status;
import yuown.yuploader.model.YuploaderTableModel;
import yuown.yuploader.ui.Client;
import yuown.yuploader.util.Helper;

import java.io.File;
import java.io.FileInputStream;
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
    private boolean paused = false;
    @Autowired
    private StreamListener streamListener;
    @Autowired
    private Helper helper;
    private int bufferSize = 1024;
    private boolean flush = true;

    public void submitToQueue() {
        this.client.setInProgress(true);
        System.out.println("Uploader Worker Started: " + System.currentTimeMillis());

        this.streamListener.setWorker(this);

        int rowCount = this.yuploaderTableModel.getRowCount();
        for (int row = 0; row < rowCount; row++) {
            FileObject file = (FileObject) this.yuploaderTableModel.getValueAt(row, 0);
            File f = new File(file.getFullPath());
            if ((f.exists()) && (isEligibleToStartUpload(file))) {
                System.out.println("File: " + file.getFullPath());
                markUploadInProgress(file, row);
                InputStream reader = getReader(f, file);
                try {
                    if (file.getOffset() > 0) {
                        FTPFile ftpFile = ftpClient.mlistFile(file.getFileName());
                        long size = ftpFile.getSize();
                        file.setOffset(size);
                        ftpClient.appendFile(file.getFileName(), reader);
                    } else {
                        ftpClient.storeFile(file.getFileName(), reader);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        this.client.hidePause(true);
        this.client.setInProgress(false);
    }

    @SuppressWarnings("resource")
    protected InputStream getReader(File f, FileObject file) {
        InputStream reader = null;
        try {
            if (file.getOffset() > 0L) {
                try {
                    System.out.println("Start Resume file: " + file.getFullPath());
                    RandomAccessFile raf = new RandomAccessFile(f, "r");
                    reader = Channels.newInputStream(raf.getChannel().position(file.getOffset()));
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Resume Failed, Start Uploading From Beginning: " + file.getFullPath());
                    try {
                        reader = new FileInputStream(f);
                        file.setOffset(0L);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            } else {
                try {
                    reader = new FileInputStream(f);
                    file.setOffset(0L);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
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

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    protected Integer doInBackground() throws Exception {
        submitToQueue();
        return Integer.valueOf(0);
    }
}
