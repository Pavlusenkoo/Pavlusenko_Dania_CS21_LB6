package bulletinBoardService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ChatClient extends JFrame {
    private JTextField groupField, portField, nameField, msgField;
    private JTextArea textArea;
    private Messanger messanger = null;
    private UITasks ui;

    public ChatClient() {
        super("Текстова конференція");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Панель для введення даних
        JPanel topPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        topPanel.setBackground(new Color(252, 241, 193));

        groupField = new JTextField("224.0.0.1");
        portField = new JTextField("12345");
        nameField = new JTextField("User");

        JLabel groupLabel = new JLabel("Група:");
        groupLabel.setFont(new Font("Arial", Font.BOLD, 14));
        groupLabel.setForeground(Color.BLUE);
        JLabel portLabel = new JLabel("Порт:");
        portLabel.setFont(new Font("Arial", Font.BOLD, 14));
        portLabel.setForeground(Color.BLUE);
        JLabel nameLabel = new JLabel("Ім'я:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(Color.BLUE);
        topPanel.add(groupLabel);
        topPanel.add(groupField);
        topPanel.add(portLabel);
        topPanel.add(portField);
        topPanel.add(nameLabel);
        topPanel.add(nameField);

        // Область для відображення повідомлень
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setBackground(new Color(189, 246, 184));
        textArea.setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(textArea);

        // Панель для введення та відправлення повідомлень
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        msgField = new JTextField();
        msgField.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(91, 30, 176));
        JButton connectButton = new JButton("З'єднати");
        connectButton.setFont(new Font("Arial", Font.BOLD, 14));
        JButton disconnectButton = new JButton("Роз'єднати");
        disconnectButton.setFont(new Font("Arial", Font.BOLD, 14));
        JButton clearButton = new JButton("Очистити");
        clearButton.setFont(new Font("Arial", Font.BOLD, 14));
        JButton sendButton = new JButton("Надіслати");
        sendButton.setFont(new Font("Arial", Font.BOLD, 14));

        buttonPanel.add(connectButton);
        buttonPanel.add(disconnectButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(sendButton);

        bottomPanel.add(msgField, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Обробники подій для кнопок
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    InetAddress addr = InetAddress.getByName(groupField.getText());
                    int port = Integer.parseInt(portField.getText());
                    String name = nameField.getText();
                    ui = (UITasks) Proxy.newProxyInstance(getClass().getClassLoader(),
                            new Class[]{UITasks.class}, new EDTInvocationHandler(new UITasksImpl()));
                    messanger = new MessanderImpl(addr, port, name, ui);
                    messanger.start();
                } catch (UnknownHostException ex) {
                    showErrorMessage("Невірна адреса групи: " + ex.getMessage());
                } catch (NumberFormatException ex) {
                    showErrorMessage("Невірний номер порту: " + ex.getMessage());
                }
            }
        });

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (messanger != null) {
                    messanger.send();
                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.setText("");
            }
        });

        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (messanger != null) {
                    messanger.stop();
                    messanger = null;
                }
            }
        });
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Помилка", JOptionPane.ERROR_MESSAGE);
    }

    private class UITasksImpl implements UITasks {
        @Override
        public String getMessage() {
            String res = msgField.getText();
            msgField.setText("");
            return res;
        }

        @Override
        public void setText(String txt) {
            textArea.append(txt + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ChatClient().setVisible(true);
            }
        });
    }
}