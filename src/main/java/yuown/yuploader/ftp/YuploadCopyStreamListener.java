package yuown.yuploader.ftp;

import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import yuown.yuploader.model.FileObject;
import yuown.yuploader.model.Status;
import yuown.yuploader.model.YuploaderTableModel;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class YuploadCopyStreamListener implements CopyStreamListener {
	
	@Autowired
	private YuploaderTableModel yuploaderTableModel;

	private long kbsTotal = 0L;
	private double percentCompleted;
	private long time = System.currentTimeMillis();
	private long start = System.currentTimeMillis();

	private int row;

	@Override
	public void bytesTransferred(CopyStreamEvent event) {
		
	}

	@Override
	public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
		FileObject fileObject = (FileObject) yuploaderTableModel.getValueAt(row, 0);
		long s = fileObject.getSize();
		this.percentCompleted = ((int) (totalBytesTransferred / s * 10000.0D) / 100.0D);

		long d = System.currentTimeMillis() - this.time;
		if (d > 1000L) {
			this.time = System.currentTimeMillis();
			long currentkBRate = this.kbsTotal / 1024L;
			yuploaderTableModel.setValueAt(currentkBRate + " KB/s", row, 4);
			yuploaderTableModel.setValueAt((this.time - this.start) / 1000L, row, 5);
			this.kbsTotal = 0L;
		} else {
			this.kbsTotal += bytesTransferred;
		}
		yuploaderTableModel.setValueAt(this.percentCompleted + " %", row, 2);
		if(s == totalBytesTransferred) {
			yuploaderTableModel.setValueAt(Status.COMPLETED, row, 3);
		}
	}

	public void setRow(int row) {
		this.row = row;
	}
}
