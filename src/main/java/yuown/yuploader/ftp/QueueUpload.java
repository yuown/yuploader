package yuown.yuploader.ftp;

import java.io.File;

import javax.swing.SwingWorker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import yuown.yuploader.model.FileObject;
import yuown.yuploader.model.Status;
import yuown.yuploader.model.YuploaderTableModel;
import yuown.yuploader.ui.YuploaderWorker;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class QueueUpload extends SwingWorker<Integer, Integer> {

	@Autowired
	private YuploaderTableModel yuploaderTableModel;

	@Autowired
	private YuploaderWorker yuploaderWorker;

	private ApplicationContext context;

	private AutowireCapableBeanFactory aw;

	public void submitToQueue() {

		int rowCount = yuploaderTableModel.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			final FileObject fo = (FileObject) yuploaderTableModel.getValueAt(i, 0);
			File f = new File(fo.getFullPath());
			try {
				if (f.exists()) {
					if (Status.COMPLETED != fo.getStatus()) {
						yuploaderWorker = aw.createBean(YuploaderWorker.class);
						yuploaderWorker.setContext(context);
						Thread.sleep(500);
						yuploaderWorker.setRow(i);
						yuploaderWorker.execute();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void setContext(ApplicationContext context) {
		this.context = context;
		aw = context.getAutowireCapableBeanFactory();
	}

	@Override
	protected Integer doInBackground() throws Exception {
		submitToQueue();
		return 0;
	}

}
