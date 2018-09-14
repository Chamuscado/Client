import Elements.Game;
import Elements.Message;
import Elements.Status;
import Elements.User;
import Exceptions.AccessDeniedException;
import Interfaces.IClientRmi;
import model.GameModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class GestInterface {
    private JList onlinePlayerslist;
    private List<User> userList;
    private List<Game> gameList;
    private JPanel Jpanel;
    private JTextArea MessagesArea;
    private JButton SendMessageButton;
    private JTextField mensageToSendField;
    private JLabel ChatUserNameLabel;
    private JTable gamesTable;
    private JPanel MyStatus;
    private JPanel PairStatus;
    private JLabel ShowNameLabel;
    private JLabel ShowUserNameLabel;
    private JLabel ShowWinsLabel;
    private JLabel ShowDefeatsLabel;
    private JButton LogoutButton;
    private JLabel ShowNamePairLabel;
    private JLabel ShowUserNamePairLabel;
    private JLabel ShowWinsPairLabel;
    private JLabel ShowDefeatsPairLabel;
    private JButton EndPairButton;
    private JButton startGame;
    private JTextArea MessagesAreaGeral;
    private JTextField mensageToSendFieldGeral;
    private JButton SendMessageButtonGeral;
    private GestServerCom gestService;
    private JFrame frame;
    private ClientRmi cliService;

    public GestInterface(GestServerCom gestService, ClientRmi cliService) {
        this.gestService = gestService;
        this.cliService = cliService;
        gestService.setGui(this);
        onlinePlayerslist.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int index = onlinePlayerslist.getSelectedIndex();
                if (index < 0 || index >= userList.size()) {
                    ChatUserNameLabel.setText("Error: index = " + index);
                    return;
                }
                String name = (String) onlinePlayerslist.getModel().getElementAt(index);
                name = name.substring(name.indexOf('(') + 1, name.indexOf(')'));
                User user = null;
                for (int i = 0; i < userList.size(); ++i) {
                    if (userList.get(i).username.compareTo(name) == 0)
                        user = userList.get(i);
                }
                if (user != null)
                    changeChatTo(user);
                else
                    ChatUserNameLabel.setText("Error: " + name);
            }
        });

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem("Convidar para formar par");
        menuItem.addActionListener(e -> {
            if (!onlinePlayerslist.isSelectionEmpty()) {
                int index = onlinePlayerslist.getSelectedIndex();
                String username = (String) onlinePlayerslist.getModel().getElementAt(index);
                username = username.substring(username.indexOf('(') + 1, username.indexOf(')'));
                gestService.sendPairInvite(username);
            }
        });
        popupMenu.add(menuItem);


        onlinePlayerslist.addMouseListener(new MouseAdapter() { //Todo -> não convidar o Todos
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e) && !onlinePlayerslist.isSelectionEmpty()) {
                    popupMenu.show(frame, e.getX(), e.getY());
                }
            }
        });


        SendMessageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = onlinePlayerslist.getSelectedIndex();
                if (index < 0 || index >= userList.size()) {
                    ChatUserNameLabel.setText("Error: index = " + index);
                    return;
                }
                String name = (String) onlinePlayerslist.getModel().getElementAt(index);
                name = name.substring(name.indexOf('(') + 1, name.indexOf(')'));

                String msg = mensageToSendField.getText().trim();
                if (!msg.isEmpty()) {
                    try {
                        gestService.sendMensage(name, msg);
                    } catch (AccessDeniedException e1) {
                        Login.startLogin(gestService, frame);
                    }
                }
                mensageToSendField.setText("");
            }
        });
        LogoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gestService.logout();
                clearAll();
                Login.startLogin(gestService, frame);
            }
        });
        EndPairButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    gestService.endPair();
                } catch (AccessDeniedException e1) {
                    Login.startLogin(gestService, frame);
                }
                refreshStatusPanel();
            }
        });
        startGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    GameModel game = new GameModel(gestService.getUsername(), gestService.getGameServerIp(), () -> {
                        refreshStatusPanel();
                        refreshGameTable();
                    });
                } catch (AccessDeniedException e1) {
                    Login.startLogin(gestService, frame);
                }
            }
        });
        SendMessageButtonGeral.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String msg = mensageToSendFieldGeral.getText().trim();
                if (!msg.isEmpty()) {
                    try {
                        gestService.sendMensage(IClientRmi.ForAll, msg);
                    } catch (AccessDeniedException e1) {
                        Login.startLogin(gestService, frame);
                    }
                }
                mensageToSendFieldGeral.setText("");
            }
        });
    }

    private void changeChatTo(User user) {
        ChatUserNameLabel.setText(user.toStringNameAndUserName());
        List<Message> messages = gestService.getMessages(user.username);
        if (messages == null) {
            MessagesArea.setText(">Não existem Mensagens!>");
            return;
        }
        MessagesArea.setText("");
        for (Message msg : messages) {
            MessagesArea.append(String.format("<%s> %s\n", msg.getSource(), msg.getMessage()));
        }
    }

    private void clearAll() {
        ShowNameLabel.setText("");
        ShowUserNameLabel.setText("");
        ShowWinsLabel.setText("");
        ShowDefeatsLabel.setText("");
        ShowNamePairLabel.setText("");
        ShowUserNamePairLabel.setText("");
        ShowWinsPairLabel.setText("");
        ShowDefeatsPairLabel.setText("");
        onlinePlayerslist.removeAll();
        ChatUserNameLabel.setText("");
        mensageToSendField.setText("");
        mensageToSendFieldGeral.setText("");
        MessagesArea.setText("");
        MessagesAreaGeral.setText("");
    }

    public void start() {
        frame = new JFrame("Painel de Controlo");
        frame.setContentPane(Jpanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        onlinePlayerslist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                gestService.logout();
            }
        });
        MessagesArea.setEditable(false);
        MessagesAreaGeral.setEditable(false);
        Login.startLogin(gestService, frame);
        refreshGameTable();
        refreshMessagesGeral();
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
        onlinePlayerslist.setListData(arrayToView);
    }


    public void refreshGameTable() {
        try {
            _refreshGameTable();
        } catch (AccessDeniedException e) {
            Login.startLogin(gestService, frame);
        }
    }

    private void _refreshGameTable() throws AccessDeniedException {
        gameList = gestService.getMyGames();
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Jogador 1");
        model.addColumn("Jogador 2");
        model.addColumn("Vencedor");

        for (Game game : gameList) {
            model.addRow(
                    new Object[]{
                            game.players[0].toStringNameAndUserName(),
                            game.players[1].toStringNameAndUserName(),
                            game.winner != null ? game.winner.toStringNameAndUserName() : ""
                    });
        }


        gamesTable.setModel(model);
    }

    public void refreshStatusPanel() {
        try {
            _refreshStatusPanel();
        } catch (AccessDeniedException e) {
            Login.startLogin(gestService, frame);
        }
    }

    private void _refreshStatusPanel() throws AccessDeniedException {
        Status status = gestService.getMyStatus();

        ShowNameLabel.setText(status.getUser().name);
        ShowUserNameLabel.setText(status.getUser().username);
        ShowWinsLabel.setText(status.getWins() + "");
        ShowDefeatsLabel.setText(status.getDefeats() + "");


        status = gestService.getAdStatus();

        if (status != null) {
            ShowNamePairLabel.setText(status.getUser().name);
            ShowUserNamePairLabel.setText(status.getUser().username);
            ShowWinsPairLabel.setText(status.getWins() + "");
            ShowDefeatsPairLabel.setText(status.getDefeats() + "");
        } else {
            ShowNamePairLabel.setText("");
            ShowUserNamePairLabel.setText("");
            ShowWinsPairLabel.setText("");
            ShowDefeatsPairLabel.setText("");
        }
    }

    public void refreshMessages(String source) {
        ListModel<String> users = (ListModel<String>) onlinePlayerslist.getModel();
        for (int i = 0; i < users.getSize(); ++i) {
            String name = users.getElementAt(i);
            name = name.substring(name.indexOf('(') + 1, name.indexOf(')'));
            if (name.equals(source)) {
                if (i != onlinePlayerslist.getSelectedIndex())
                    onlinePlayerslist.setSelectionInterval(i, i);
                else {
                    User user = null;
                    for (int i2 = 0; i2 < userList.size(); ++i2) {
                        if (userList.get(i2).username.compareTo(name) == 0)
                            user = userList.get(i2);
                    }
                    if (user != null)
                        changeChatTo(user);
                }
                break;
            }
        }
    }

    public void showError(String errorMsg) {
        JOptionPane.showMessageDialog(frame, errorMsg);
    }

    public void showPairInvite(User user) {
        String msg = String.format("O jogador %s(%s) convidou-o para formar um par, aceita?", user.name, user.username);
        int answer = JOptionPane.showConfirmDialog(frame, msg, "Convite para formar par", JOptionPane.YES_NO_OPTION);
        try {
            gestService.sendAnswerPairInvite(user, answer == JOptionPane.YES_OPTION);
        } catch (AccessDeniedException e) {
            Login.startLogin(gestService, frame);
        }
    }

    public void showAnswerOfPairInvite(User user, boolean answer) {
        String msg = String.format("O jogador %s(%s) %s o convite!", user.name, user.username, answer ? "aceitou" : "recusou");
        JOptionPane.showMessageDialog(frame, msg);
    }

    public void setReadyToPlay(boolean ready) {
        startGame.setEnabled(ready);
    }

    public void refreshMessagesGeral() {
        List<Message> messages = gestService.getMessages(IClientRmi.ForAll);
        if (messages == null) {
            MessagesAreaGeral.setText(">Não existem Mensagens!>");
            return;
        }
        MessagesAreaGeral.setText("");
        for (Message msg : messages) {
            MessagesAreaGeral.append(String.format("<%s> %s\n", msg.getSource(), msg.getMessage()));
        }
    }
}
