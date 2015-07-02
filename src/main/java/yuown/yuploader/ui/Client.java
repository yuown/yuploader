package yuown.yuploader.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JPasswordField;

public class Client extends JFrame {

    private JPanel contentPane;
    private JTextField txtUsername;
    private JPasswordField passwordField;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Client frame = new Client();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public Client() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1200, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        SpringLayout sl_contentPane = new SpringLayout();
        contentPane.setLayout(sl_contentPane);
        
        JPanel panel = new JPanel();
        sl_contentPane.putConstraint(SpringLayout.NORTH, panel, 20, SpringLayout.NORTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, panel, -556, SpringLayout.EAST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, panel, 169, SpringLayout.NORTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, panel, 0, SpringLayout.EAST, contentPane);
        contentPane.add(panel);
        SpringLayout sl_panel = new SpringLayout();
        panel.setLayout(sl_panel);
        
        txtUsername = new JTextField();
        sl_panel.putConstraint(SpringLayout.NORTH, txtUsername, 10, SpringLayout.NORTH, panel);
        sl_panel.putConstraint(SpringLayout.EAST, txtUsername, -10, SpringLayout.EAST, panel);
        panel.add(txtUsername);
        txtUsername.setColumns(10);
        
        JLabel lblUsername = new JLabel("Username: ");
        sl_panel.putConstraint(SpringLayout.NORTH, lblUsername, 3, SpringLayout.NORTH, txtUsername);
        sl_panel.putConstraint(SpringLayout.EAST, lblUsername, -6, SpringLayout.WEST, txtUsername);
        panel.add(lblUsername);
        
        passwordField = new JPasswordField();
        sl_panel.putConstraint(SpringLayout.NORTH, passwordField, 6, SpringLayout.SOUTH, txtUsername);
        sl_panel.putConstraint(SpringLayout.WEST, passwordField, 0, SpringLayout.WEST, txtUsername);
        sl_panel.putConstraint(SpringLayout.EAST, passwordField, -80, SpringLayout.EAST, txtUsername);
        panel.add(passwordField);
    }
}
