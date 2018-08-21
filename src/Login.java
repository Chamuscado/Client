import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Login extends JDialog {
    private JPanel contentPane;
    private JButton RegistButton;
    private JButton ExitButton;
    private JButton loginButton;
    private JPasswordField palavraPassePasswordField;
    private JTextField nomeDeUtilizadorTextField;
    private JLabel Title;
    private JLabel passWordLabel;
    private JLabel UserNameLabel;
    private GestServerCom gestService;
    private Login loginForm;
    private Component parent;

    public Login(GestServerCom gestService) {
        loginForm = this;
        this.gestService = gestService;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(RegistButton);

        RegistButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Register.startRegister(gestService, parent);
            }
        });

        ExitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gestService.login(nomeDeUtilizadorTextField.getText(), palavraPassePasswordField.getText()))
                    dispose();
                else
                    JOptionPane.showMessageDialog(loginForm, "Nome de Utilizador e/ou Palavra-passe errado(s)");
            }
        });
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void startLogin(GestServerCom gestService, Component parent) {
        Login dialog = new Login(gestService);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.parent = parent;
        dialog.setVisible(true);

    }
}
