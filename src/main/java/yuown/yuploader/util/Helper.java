package yuown.yuploader.util;

import org.springframework.beans.factory.annotation.Value;

import javax.swing.JOptionPane;

public class Helper {

    @Value("${yuploader.app.title}")
    private String appTitle;

    public void alert(java.awt.Component frame, String message) {
        JOptionPane.showMessageDialog(frame, message, appTitle, JOptionPane.WARNING_MESSAGE);
    }

}
