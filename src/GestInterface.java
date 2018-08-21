import Elements.User;
import Exceptions.AccessDeniedException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class GestInterface {
    private JList list1;
    private List<User> userList;
    private JPanel Jpanel;
    private JTextArea MessagesArea;
    private JButton SendMessageButton;
    private JTextField mensageToSendField;
    private JLabel ChatUserNameLabel;
    private GestServerCom gestService;
    private JFrame frame;

    public GestInterface(GestServerCom gestService) {

        this.gestService = gestService;
        gestService.setGui(this);
        list1.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int index = list1.getSelectedIndex();
                if (index < 0 || index >= userList.size()) {
                    ChatUserNameLabel.setText("Error: index = " + index);
                    return;
                }
                String name = (String) list1.getModel().getElementAt(index);
                name = name.substring(name.indexOf('(') + 1, name.indexOf(')'));
                User user = null;
                for (int i = 0; i < userList.size(); ++i) {
                    if (userList.get(i).username.compareTo(name) == 0)
                        user = userList.get(i);
                }
                if (user != null)
                    ChatUserNameLabel.setText(user.toStringNameAndUserName());
                else
                    ChatUserNameLabel.setText("Error: " + name);
            }
        });
        SendMessageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = list1.getSelectedIndex();
                if (index < 0 || index >= userList.size()) {
                    ChatUserNameLabel.setText("Error: index = " + index);
                    return;
                }
                String name = (String) list1.getModel().getElementAt(index);
                name = name.substring(name.indexOf('(') + 1, name.indexOf(')'));
                String msg = mensageToSendField.getText().trim();
                if (!msg.isEmpty())
                    if (gestService.sendMensage(name, msg))
                        MessagesArea.append(String.format("\n<%s> %s", gestService.getUsername(), msg));
                mensageToSendField.setText("");
            }
        });
    }

    public void start() {
        frame = new JFrame("Painel de Controlo");
        frame.setContentPane(Jpanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        list1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                gestService.logout();
            }
        });
        refreshPlayerList();
        MessagesArea.setEditable(false);
    }

    public void refreshPlayerList() {
        try {
            _refreshPlayerList();
        } catch (AccessDeniedException e) {
            Login.startLogin(gestService, frame);
        }
    }

    private void _refreshPlayerList() throws AccessDeniedException {
        userList = gestService.getLoginUsers();
        String[] arrayToView = new String[userList.size()];
        for (int i = 0; i < userList.size(); ++i) {
            User user = userList.get(i);
            arrayToView[i] = user.toStringNameAndUserName();
        }
        list1.setListData(arrayToView);
    }

    public void addMessageToList(String source, String msg) {
        MessagesArea.append(String.format("\n<%s> %s", source, msg));
    }

    public void showError(String errorMsg) {
        JOptionPane.showMessageDialog(frame, errorMsg);
    }
}
