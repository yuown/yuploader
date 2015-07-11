package yuown.yuploader.util;

import java.util.Date;

import javax.swing.JOptionPane;

import org.springframework.beans.factory.annotation.Value;

public class Helper {

	@Value("${yuploader.app.title}")
	private String appTitle;

	public void alert(java.awt.Component frame, String message) {
		JOptionPane.showMessageDialog(frame, message, appTitle, JOptionPane.WARNING_MESSAGE);
	}

	public int confirm(java.awt.Component frame, String message) {
		return JOptionPane.showConfirmDialog(frame, message, appTitle, JOptionPane.YES_NO_OPTION);
	}

	public String getDateDDMMYYYY() {
		Date d = new Date();
		return (d.getDate() < 10 ? "0" + d.getDate() : new StringBuilder(String.valueOf(d.getDate())).toString())
				+ (d.getMonth() < 10 ? "0" + (d.getMonth() + 1) : new StringBuilder(String.valueOf(d.getMonth() + 1)).toString()) + (d.getYear() + 1900);
	}

}
