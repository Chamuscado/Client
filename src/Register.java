import javax.jws.soap.SOAPBinding;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;

public class Register extends JDialog {
    private JPanel contentPane;
    private JButton buttonCancel;
    private JButton buttonRegist;
    private JLabel Title;
    private JTextField NameField;
    private JPasswordField PasswordField1;
    private JPasswordField PasswordField2;
    private JTextField UserNameField;
    private Component parent;
    private GestServerCom gestService;
    private Register registForm;

    public Register(GestServerCom gestService) {
        registForm = this;
        this.gestService = gestService;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonRegist);
        buttonRegist.setEnabled(false);
        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
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

        buttonRegist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = UserNameField.getText();
                String name = NameField.getText();
                String pass0 = PasswordField1.getText();
                String pass1 = PasswordField2.getText();

                if (pass0.compareTo(pass1) != 0) {
                    JOptionPane.showMessageDialog(registForm, "As Palavras-chave não correspondem");
                    return;
                }
                if (gestService.registerUser(name, username, pass0)) {
                    JOptionPane.showMessageDialog(registForm, "Registado com sucesso");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(registForm, "Não foi possivel realizar o registo");
                }
            }
        });

        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                check();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                check();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                check();
            }

            private void check() {
                if (NameField.getText().isEmpty()
                        || UserNameField.getText().isEmpty()
                        || PasswordField1.getText().isEmpty()
                        || PasswordField2.getText().isEmpty()) {
                    buttonRegist.setEnabled(false);
                } else {
                    buttonRegist.setEnabled(true);
                }
            }
        };

        NameField.getDocument().addDocumentListener(documentListener);
        PasswordField1.getDocument().addDocumentListener(documentListener);
        PasswordField2.getDocument().addDocumentListener(documentListener);
        UserNameField.getDocument().addDocumentListener(documentListener);


    }

    public static void startRegister(GestServerCom gestService, Component parent) {
        Register dialog = new Register(gestService);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.parent = parent;
        dialog.setVisible(true);

    }

    private void onCancel() {
        dispose();
    }

}
