package yuown.yuploader.ftp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import yuown.yuploader.model.FileObject;
import yuown.yuploader.model.Status;
import yuown.yuploader.model.YuploaderTableModel;
import yuown.yuploader.ui.PauseResumeButton;
import yuown.yuploader.util.YuownUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StreamListener implements ActionListener {

    @Autowired
    private YuploaderTableModel yuploaderTableModel;

    private boolean paused = false;

    private long bytes = 0;
    private double percentCompleted;
    private long time = System.currentTimeMillis();
    private long start = System.currentTimeMillis();

    private int row = -1;

    private FileObject fileObject;
    
    private long total;

    public StreamListener() {
    }

    private PauseResumeButton button = null;

    public boolean bytesTransferred(long totalBytesTransferred, int bytesTransferred) {
        long s = fileObject.getSize();
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
            updateProgress(row);
            button.setText(Status.COMPLETED.toString());
            button.setEnabled(false);
            fileObject.setStatus(Status.COMPLETED);
            yuploaderTableModel.setValueAt(Status.COMPLETED, row, 3);
        }
        fileObject.setOffset(totalBytesTransferred);
        return paused;
    }

    private void updateProgress(int row) {
        double currentKBRate = YuownUtils.longTo2Decimals(this.bytes, 1024);
        if (currentKBRate < 1024) {
            yuploaderTableModel.setValueAt(currentKBRate + " KB/s", row, 4);
        } else {
            double currentMBRate = YuownUtils.longTo2Decimals(currentKBRate, 1024);
            yuploaderTableModel.setValueAt(currentMBRate + " MB/s", row, 4);
        }
        yuploaderTableModel.setValueAt(YuownUtils.longTo2Decimals(this.time - this.start, 1000), row, 5);
        yuploaderTableModel.setValueAt(this.percentCompleted + " %", row, 2);
    }

    public void setRow(int row2) {
        this.row = row2;
        fileObject = (FileObject) yuploaderTableModel.getValueAt(row, 0);
        button = (PauseResumeButton) yuploaderTableModel.getValueAt(row2, 6);
        button.setEnabled(true);
        yuploaderTableModel.getTable().updateUI();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(total == 0 || total == fileObject.getSize()) {
            return;
        }
        paused = !paused;
        if (paused) {
            button.setText("Resume");
        } else {
            button.setText("Pause");
        }
        yuploaderTableModel.getTable().updateUI();
    }
}
