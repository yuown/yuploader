package yuown.yuploader.ftp;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;

import yuown.yuploader.ui.UploadWindow;

public class YuownFtpSessionFactory extends DefaultFtpSessionFactory {

	private UploadWindow uploadWindow;

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
				long s = 519;// fo.getSize();
				this.percentCompleted = ((int) (totalBytesTransferred / s * 10000.0D) / 100.0D);

				long d = System.currentTimeMillis() - this.time;
				if (d > 1000L) {
					this.time = System.currentTimeMillis();
					long currentkBRate = this.kbsTotal / 1024L;
					// tM.setValueAt(currentkBRate + " KB/s", row, 4);
					// tM.setValueAt((this.time - this.start) / 1000L, row, 5);
					this.kbsTotal = 0L;
				} else {
					this.kbsTotal += bytesTransferred;
				}
				// tM.setValueAt(this.percentCompleted + " %", row, 2);
			}

			public void bytesTransferred(CopyStreamEvent event) {
				bytesTransferred(event.getTotalBytesTransferred(), event.getBytesTransferred(), event.getStreamSize());
			}
		});
	}

	public void setUploadWindow(UploadWindow uploadWindow) {
		this.uploadWindow = uploadWindow;
	}
}
