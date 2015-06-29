package yuown.yuploader.ui;

import org.springframework.stereotype.Component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;

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
	
	/**
	 * Create the dialog.
	 */
	public AboutDialog() {
		setModal(true);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);
		setBounds(100, 100, 600, 150);
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
			sl_contentPanel.putConstraint(SpringLayout.WEST, lblHeader, 10, SpringLayout.WEST, contentPanel);
			sl_contentPanel.putConstraint(SpringLayout.EAST, lblHeader, -131, SpringLayout.EAST, contentPanel);
			sl_contentPanel.putConstraint(SpringLayout.NORTH, lblNewLabel, 6, SpringLayout.SOUTH, lblHeader);
			sl_contentPanel.putConstraint(SpringLayout.WEST, lblNewLabel, 0, SpringLayout.WEST, lblHeader);
			sl_contentPanel.putConstraint(SpringLayout.NORTH, lblHeader, 10, SpringLayout.NORTH, contentPanel);
			contentPanel.add(lblHeader);
		}
		{
			lblSite = new JLabel("site");
			lblSite.setForeground(Color.blue);
			sl_contentPanel.putConstraint(SpringLayout.NORTH, lblSite, 0, SpringLayout.NORTH, lblNewLabel);
			sl_contentPanel.putConstraint(SpringLayout.WEST, lblSite, 6, SpringLayout.EAST, lblNewLabel);
			sl_contentPanel.putConstraint(SpringLayout.EAST, lblSite, 371, SpringLayout.EAST, lblNewLabel);
			contentPanel.add(lblSite);
		}
		{
			JLabel lblMobile = new JLabel("Mobile: ");
			sl_contentPanel.putConstraint(SpringLayout.NORTH, lblMobile, 6, SpringLayout.SOUTH, lblNewLabel);
			sl_contentPanel.putConstraint(SpringLayout.WEST, lblMobile, 0, SpringLayout.WEST, lblNewLabel);
			contentPanel.add(lblMobile);
		}
		{
			lblMobile_1 = new JLabel("mobile");
			sl_contentPanel.putConstraint(SpringLayout.NORTH, lblMobile_1, 6, SpringLayout.SOUTH, lblSite);
			sl_contentPanel.putConstraint(SpringLayout.WEST, lblMobile_1, 0, SpringLayout.WEST, lblSite);
			sl_contentPanel.putConstraint(SpringLayout.EAST, lblMobile_1, 341, SpringLayout.WEST, lblSite);
			contentPanel.add(lblMobile_1);
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
}
