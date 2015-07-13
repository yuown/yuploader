package yuown.yuploader.ftp;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import yuown.yuploader.model.FileObject;
import yuown.yuploader.model.Status;
import yuown.yuploader.model.YuploaderTableModel;
import yuown.yuploader.ui.Client;
import yuown.yuploader.util.Helper;
import yuown.yuploader.util.YuownUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JOptionPane;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class StreamListener implements CopyStreamListener, ActionListener {

    @Autowired
    private YuploaderTableModel yuploaderTableModel;

    @Autowired
    private Client client;

    @Autowired
    private FTPClient ftpClient;

    private long bytes = 0;
    private double percentCompleted;
    private long time = System.currentTimeMillis();
    private long startTime = System.currentTimeMillis();

    private int row = -1;

    private FileObject fileObject;

    private long totalBytesTransferred;

    @Autowired
    private Helper helper;

    private InputStream reader;

    public StreamListener() {
    }

    private void updateProgress(int row) {
        double currentKBRate = YuownUtils.longTo2Decimals(this.bytes, 1024);
        if (currentKBRate < 1024) {
            yuploaderTableModel.setValueAt(currentKBRate + " KB/s", row, 4);
        } else {
            double currentMBRate = YuownUtils.longTo2Decimals(currentKBRate, 1024);
            yuploaderTableModel.setValueAt(currentMBRate + " MB/s", row, 4);
        }
        yuploaderTableModel.setValueAt(YuownUtils.longTo2Decimals(this.time - this.startTime, 1000), row, 5);
        yuploaderTableModel.setValueAt(this.percentCompleted + " %", row, 2);
        client.setLastAccess(System.currentTimeMillis());
    }

    public void setRow(int row2) {
        this.row = row2;
        fileObject = (FileObject) yuploaderTableModel.getValueAt(row, 0);
        this.startTime = System.currentTimeMillis();
        totalBytesTransferred = fileObject.getOffset();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            client.toggleLogin(false);
            reader.close();
            ftpClient.abort();
            ftpClient.completePendingCommand();
        } catch (IOException ex) {
            client.toggleLogin(true);
            client.togglePause(false);
            ex.printStackTrace();
        }
        if (StringUtils.equals("Cancel Upload", e.getActionCommand())) {
            int choice = helper.confirm(client, "Are you sure cancel Current Upload ?");
            if (choice == JOptionPane.YES_OPTION) {
                fileObject.setOffset(0);
                yuploaderTableModel.setValueAt(Status.ADDED, row, 3);
                yuploaderTableModel.setValueAt("0 %", row, 2);
                totalBytesTransferred = 0;
            }
        }
    }

    @Override
    public void bytesTransferred(CopyStreamEvent event) {
        bytesTransferred(event.getTotalBytesTransferred(), event.getBytesTransferred(), event.getStreamSize());
    }

    @Override
    public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
        long s = fileObject.getSize();
        totalBytesTransferred += this.totalBytesTransferred;
        this.percentCompleted = YuownUtils.longTo2Decimals(totalBytesTransferred * 100, s);
        long d = System.currentTimeMillis() - this.time;
        if (d > 1000L) {
            this.time = System.currentTimeMillis();
            updateProgress(row);
            this.bytes = 0L;
        } else {
            this.bytes += bytesTransferred;
        }
        yuploaderTableModel.setValueAt(this.percentCompleted + " %", row, 2);
        if (s == totalBytesTransferred) {
            client.togglePause(false);
            updateProgress(row);
            fileObject.setStatus(Status.COMPLETED);
            yuploaderTableModel.setValueAt(Status.COMPLETED, row, 3);
        }
        fileObject.setOffset(totalBytesTransferred);
    }

    public void setTotalBytesTransferred(long totalBytesTransferred) {
        this.totalBytesTransferred = totalBytesTransferred;
    }

    public void setReader(InputStream reader) {
        this.reader = reader;
    }
}
