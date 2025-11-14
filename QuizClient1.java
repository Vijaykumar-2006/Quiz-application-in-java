import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class QuizClient1 {
    private static List<Question> questions;
    private static List<Integer> answers = new ArrayList<>();
    private static int currentQuestion = 0;
    private static JLabel questionLabel;
    private static JRadioButton[] optionButtons = new JRadioButton[4];
    private static ButtonGroup group = new ButtonGroup();
    private static JLabel timerLabel;
    private static javax.swing.Timer timer;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;
    private static JFrame frame;

    public static void main(String[] args) throws Exception {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(240, 248, 255));
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(46, 139, 87), 3) 
                ,
                "Quiz Login",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 18),
                new Color(255, 215, 0)
            ),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userLabel.setForeground(new Color(255, 99, 71));
        loginPanel.add(userLabel, gbc);

        gbc.gridy++;
        JTextField userField = new JTextField(20);
        userField.setFont(new Font("Arial", Font.PLAIN, 14));
        userField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 223, 186)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        loginPanel.add(userField, gbc);

        gbc.gridy++;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passLabel.setForeground(new Color(255, 223, 186));
        loginPanel.add(passLabel, gbc);

        gbc.gridy++;
        JPasswordField passField = new JPasswordField(20);
        passField.setFont(new Font("Arial", Font.PLAIN, 14));
        passField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 239, 184)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        loginPanel.add(passField, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(20, 10, 10, 10);
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(255, 105, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(8, 25, 8, 25));
        loginPanel.add(loginButton, gbc);

        JFrame loginFrame = new JFrame("Quiz Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(400, 350);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.add(loginPanel);
        loginFrame.setVisible(true);

        final boolean[] loginComplete = {false};
        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(loginFrame,
                        "Please enter both username and password",
                        "Login Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Socket socket = new Socket("localhost", 1234);
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                out.writeObject(username);
                out.writeObject(password);

                String response = (String) in.readObject();
                if (response.equals("Already completed")) {
                    JOptionPane.showMessageDialog(loginFrame,
                            "You have already completed the quiz!",
                            "Quiz Completed",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                } else if (!response.equals("Login successful")) {
                    JOptionPane.showMessageDialog(loginFrame, response, "Login Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                questions = (List<Question>) in.readObject();
                loginFrame.dispose();
                SwingUtilities.invokeLater(() -> showQuizUI());
                loginComplete[0] = true;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(loginFrame,
                        "Connection error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        while (!loginComplete[0]) {
            Thread.sleep(100);
        }
    }

    private static void showQuizUI() {
        frame = new JFrame("Quiz Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.getContentPane().setBackground(new Color(255, 182, 193));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(135, 206, 250));
        frame.add(mainPanel);

        JPanel questionPanel = new JPanel(new BorderLayout());
        questionPanel.setBackground(new Color(240, 128, 128));
        questionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        questionLabel = new JLabel("", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 20));
        questionLabel.setForeground(new Color(255, 165, 0));
        questionPanel.add(questionLabel, BorderLayout.CENTER);
        mainPanel.add(questionPanel, BorderLayout.NORTH);

        JPanel optionPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        optionPanel.setBackground(new Color(0, 191, 255));
        optionPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        Color[] optionColors = {
            new Color(255, 182, 193),
            new Color(135, 206, 250),
            new Color(255, 255, 224),
            new Color(216, 191, 216)
        };

        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JRadioButton();
            optionButtons[i].setFont(new Font("Arial", Font.PLAIN, 18));
            optionButtons[i].setBackground(optionColors[i]);
            optionButtons[i].setOpaque(true);
            optionButtons[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(240, 248, 255)),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)));
            group.add(optionButtons[i]);
            optionPanel.add(optionButtons[i]);
        }
        mainPanel.add(optionPanel, BorderLayout.CENTER);

        JPanel timerPanel = new JPanel();
        timerPanel.setBackground(new Color(255, 228, 225) );
        timerLabel = new JLabel("Time: 30", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        timerLabel.setForeground(new Color(220, 20, 60));
        timerPanel.add(timerLabel);
        mainPanel.add(timerPanel, BorderLayout.EAST);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(255, 228, 225) );
        JButton nextButton = new JButton("Next Question \u23E9");
        nextButton.setFont(new Font("Arial", Font.BOLD, 16));
        nextButton.setBackground(new Color(50, 205, 50));
        nextButton.setForeground(Color.WHITE);
        nextButton.setFocusPainted(false);
        nextButton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        nextButton.addActionListener(e -> submitAnswer());
        buttonPanel.add(nextButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
        loadQuestion();
    }

    private static void loadQuestion() {
        if (currentQuestion >= questions.size()) {
            try {
                out.writeObject(answers);
                String result = (String) in.readObject();
                showFinalResults(result);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        Question q = questions.get(currentQuestion);
        questionLabel.setText("<html><div style='text-align: center;'>Q" + (currentQuestion + 1) + ": " + q.getQuestion() + "</div></html>");
        List<String> opts = q.getOptions();

        for (int i = 0; i < 4; i++) {
            if (i < opts.size()) {
                optionButtons[i].setText("<html><div style='padding: 5px;'>" + opts.get(i) + "</div></html>");
                optionButtons[i].setVisible(true);
            } else {
                optionButtons[i].setVisible(false);
            }
        }
        group.clearSelection();

        startTimer();
    }

    private static void submitAnswer() {
        boolean answered = false;
        for (int i = 0; i < 4; i++) {
            if (optionButtons[i].isSelected() && optionButtons[i].isVisible()) {
                answers.add(i);
                String cleanOption = optionButtons[i].getText().replaceAll("\\<.*?\\>", "").trim();
                System.out.println("Question " + (currentQuestion + 1) + " - Selected Option: " + cleanOption);
                answered = true;
                break;
            }
        }
        if (!answered) {
            answers.add(-1);
            System.out.println("Question " + (currentQuestion + 1) + " - No option selected.");
        }
        currentQuestion++;
        loadQuestion();
    }

    private static void showFinalResults(String result) {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBackground(new Color(255, 250, 205));
        resultPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextArea resultArea = new JTextArea(result);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Arial", Font.BOLD, 18));
        resultArea.setForeground( new Color(255, 223, 186));
        resultArea.setBackground(new Color(255, 99, 71));
        resultArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder( new Color(255, 223, 186)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        resultPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 248, 255));
        JButton exitButton = new JButton("Exit Quiz");
        exitButton.setFont(new Font("Arial", Font.BOLD, 16));
        exitButton.setBackground(new Color(220, 20, 60));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        exitButton.addActionListener(e -> System.exit(0));
        buttonPanel.add(exitButton);

        resultPanel.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(resultPanel, BorderLayout.CENTER);

        frame.revalidate();
        frame.repaint();
    }

    private static void startTimer() {
        if (timer != null) timer.stop();
        final int[] timeLeft = {30};
        timer = new javax.swing.Timer(1000, e -> {
            timeLeft[0]--;
            if (timeLeft[0] <= 10) {
                timerLabel.setForeground(new Color(255, 0, 0));
            } else if (timeLeft[0] <= 20) {
                timerLabel.setForeground(new Color(255, 140, 0));
            } else {
                timerLabel.setForeground(new Color(0, 100, 0));
            }
            timerLabel.setText("Time: " + timeLeft[0]);
            if (timeLeft[0] == 0) {
                timer.stop();
                submitAnswer();
            }
        });
        timer.start();
    }
}

class Question implements Serializable {
    private static final long serialVersionUID = 1L;
    private String question;
    private List<String> options;
    private int correctAnswer;

    public Question(String question, List<String> options, int correctAnswer) {
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }
}
