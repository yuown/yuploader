package yuown.yuploader.ui;

import javax.swing.SwingWorker;

public class YuploaderWorker extends SwingWorker<UploadWindow, Integer> {

//	private DirectChannel ftpChannel;
//	private Message<File> fileMessage;
	private UploadWindow uploadWindow;

//	public YuploaderWorker(DirectChannel ftpChannel, Message<File> fileMessage) {
//		this.ftpChannel = ftpChannel;
//		this.fileMessage = fileMessage;
//	}

	public YuploaderWorker(UploadWindow uploadWindow) {
		this.uploadWindow = uploadWindow;
	}

	@Override
	protected UploadWindow doInBackground() throws Exception {
		uploadWindow.uploadFiles(null);
		return uploadWindow;
	}

}
