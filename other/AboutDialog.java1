package yuown.yuploader.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;

import org.springframework.stereotype.Component;

@Component
public class AboutDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6835274690162080911L;

	private final JPanel contentPanel = new JPanel();
	private JLabel lblNewLabel;
	private JLabel lblSite;
	private JLabel lblHeader;
	private JLabel lblMobile_1;
	private JLabel lblIcon;
	private JLabel lblIcon_1;
	private JLabel lblMobile;
	private JLabel lblYuploaderAppversion;
	private JLabel lblDeveloper;
	private JLabel lblAppVersion;
	private JLabel lblDeveloperEmail;
	
	private String appVersion;
	
	private String developerEmail;
	
	
	/**
	 * Create the dialog.
	 */
	public AboutDialog() {
		setModal(true);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);
		setBounds(100, 100, 758, 312);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		SpringLayout sl_contentPanel = new SpringLayout();
		contentPanel.setLayout(sl_contentPanel);
		{
			lblNewLabel = new JLabel("Website: ");
			contentPanel.add(lblNewLabel);
		}
		{
			lblHeader = new JLabel("Header");
			sl_contentPanel.putConstraint(SpringLayout.EAST, lblHeader, -40, SpringLayout.EAST, contentPanel);
			sl_contentPanel.putConstraint(SpringLayout.NORTH, lblNewLabel, 6, SpringLayout.SOUTH, lblHeader);
			sl_contentPanel.putConstraint(SpringLayout.WEST, lblNewLabel, 0, SpringLayout.WEST, lblHeader);
			sl_contentPanel.putConstraint(SpringLayout.SOUTH, lblHeader, -217, SpringLayout.SOUTH, contentPanel);
			contentPanel.add(lblHeader);
		}
		{
			lblSite = new JLabel("site");
			sl_contentPanel.putConstraint(SpringLayout.NORTH, lblSite, 0, SpringLayout.NORTH, lblNewLabel);
			sl_contentPanel.putConstraint(SpringLayout.WEST, lblSite, 6, SpringLayout.EAST, lblNewLabel);
			sl_contentPanel.putConstraint(SpringLayout.EAST, lblSite, 0, SpringLayout.EAST, lblHeader);
			lblSite.setForeground(Color.blue);
			contentPanel.add(lblSite);
		}
		{
			lblMobile = new JLabel("Mobile: ");
			sl_contentPanel.putConstraint(SpringLayout.NORTH, lblMobile, 6, SpringLayout.SOUTH, lblNewLabel);
			sl_contentPanel.putConstraint(SpringLayout.WEST, lblMobile, 0, SpringLayout.WEST, lblNewLabel);
			sl_contentPanel.putConstraint(SpringLayout.EAST, lblMobile, 57, SpringLayout.WEST, lblNewLabel);
			contentPanel.add(lblMobile);
		}
		{
			lblMobile_1 = new JLabel("mobile");
			sl_contentPanel.putConstraint(SpringLayout.NORTH, lblMobile_1, 0, SpringLayout.NORTH, lblMobile);
			sl_contentPanel.putConstraint(SpringLayout.WEST, lblMobile_1, 6, SpringLayout.EAST, lblMobile);
			sl_contentPanel.putConstraint(SpringLayout.EAST, lblMobile_1, -40, SpringLayout.EAST, contentPanel);
			contentPanel.add(lblMobile_1);
		}
		{
			lblIcon = new JLabel("Icon");
			try {
				BufferedImage logo = ImageIO.read(getClass().getResource("/images/vvv.png"));
				lblIcon_1 = new JLabel(new ImageIcon(logo));
				sl_contentPanel.putConstraint(SpringLayout.EAST, lblIcon_1, -526, SpringLayout.EAST, contentPanel);
				sl_contentPanel.putConstraint(SpringLayout.WEST, lblHeader, 11, SpringLayout.EAST, lblIcon_1);
				sl_contentPanel.putConstraint(SpringLayout.NORTH, lblIcon_1, 8, SpringLayout.NORTH, contentPanel);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			contentPanel.add(lblIcon_1);
		}
		{
			lblYuploaderAppversion = new JLabel("yuploader App Version: ");
			sl_contentPanel.putConstraint(SpringLayout.WEST, lblYuploaderAppversion, 0, SpringLayout.WEST, lblIcon_1);
			contentPanel.add(lblYuploaderAppversion);
		}
		{
			lblDeveloper = new JLabel("Developer: ");
			sl_contentPanel.putConstraint(SpringLayout.SOUTH, lblYuploaderAppversion, -6, SpringLayout.NORTH, lblDeveloper);
			sl_contentPanel.putConstraint(SpringLayout.WEST, lblDeveloper, 0, SpringLayout.WEST, lblIcon_1);
			sl_contentPanel.putConstraint(SpringLayout.SOUTH, lblDeveloper, 0, SpringLayout.SOUTH, contentPanel);
			contentPanel.add(lblDeveloper);
		}
		{
			lblAppVersion = new JLabel("1.0");
			sl_contentPanel.putConstraint(SpringLayout.NORTH, lblAppVersion, 0, SpringLayout.NORTH, lblYuploaderAppversion);
			sl_contentPanel.putConstraint(SpringLayout.WEST, lblAppVersion, 6, SpringLayout.EAST, lblYuploaderAppversion);
			sl_contentPanel.putConstraint(SpringLayout.EAST, lblAppVersion, -40, SpringLayout.EAST, contentPanel);
			contentPanel.add(lblAppVersion);
		}
		{
			lblDeveloperEmail = new JLabel("kiran.nk@yuown.com");
			sl_contentPanel.putConstraint(SpringLayout.WEST, lblDeveloperEmail, 85, SpringLayout.EAST, lblDeveloper);
			sl_contentPanel.putConstraint(SpringLayout.SOUTH, lblDeveloperEmail, 0, SpringLayout.SOUTH, lblDeveloper);
			sl_contentPanel.putConstraint(SpringLayout.EAST, lblDeveloperEmail, -40, SpringLayout.EAST, contentPanel);
			contentPanel.add(lblDeveloperEmail);
		}

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				
				okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        closeAbout();
                    }
                });
			}
		}
	}
	
	protected void closeAbout() {
        this.setVisible(false);
    }

    public void setHeader(String header) {
		this.lblHeader.setText(header);
	}
	
	public void setSite(String site) {
		this.lblSite.setText(site);
	}
	
	public void setMobile(String mobile) {
		this.lblMobile_1.setText(mobile);
	}

	public void setAppVersion(String appVersion) {
		this.lblAppVersion.setText(appVersion);
	}

	public void setDeveloperEmail(String developerEmail) {
		this.lblDeveloperEmail.setText(developerEmail);
	}
}
