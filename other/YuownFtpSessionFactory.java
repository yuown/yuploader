package yuown.yuploader.ftp;

import java.io.IOException;

import javax.swing.table.TableModel;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;

import yuown.yuploader.model.FileObject;
import yuown.yuploader.ui.UploadWindow;

public class YuownFtpSessionFactory extends DefaultFtpSessionFactory {

	private UploadWindow uploadWindow;
	
	private FTPHelperBean ftpHelperBean;

	@Override
	protected void postProcessClientAfterConnect(final FTPClient ftpClient) throws IOException {
		super.postProcessClientAfterConnect(ftpClient);
		System.out.println("************************* postProcessClientAfterConnect *************************");
		ftpClient.setCopyStreamListener(new CopyStreamListener() {
			private long kbsTotal = 0L;
			private double percentCompleted;
			private long time = System.currentTimeMillis();
			private long start = System.currentTimeMillis();

			public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
				TableModel model = uploadWindow.getTable().getModel();
				int row = ftpHelperBean.getCurrentRow();
				FileObject fo = (FileObject) model.getValueAt(row, 0);
				long s = fo.getSize();
				this.percentCompleted = ((int) (totalBytesTransferred / s * 10000.0D) / 100.0D);

				long d = System.currentTimeMillis() - this.time;
				if (d > 1000L) {
					this.time = System.currentTimeMillis();
					long currentkBRate = this.kbsTotal / 1024L;
					model.setValueAt(currentkBRate + " KB/s", row, 4);
					model.setValueAt((this.time - this.start) / 1000L, row, 5);
					this.kbsTotal = 0L;
				} else {
					this.kbsTotal += bytesTransferred;
				}
				model.setValueAt(this.percentCompleted + " %", row, 2);
			}

			public void bytesTransferred(CopyStreamEvent event) {
				bytesTransferred(event.getTotalBytesTransferred(), event.getBytesTransferred(), event.getStreamSize());
			}
		});
	}

	public void setUploadWindow(UploadWindow uploadWindow) {
		this.uploadWindow = uploadWindow;
	}

	public void setFtpHelperBean(FTPHelperBean ftpHelperBean) {
		this.ftpHelperBean = ftpHelperBean;
	}
}
