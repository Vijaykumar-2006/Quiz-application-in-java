import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class QuizServer1 {
    private static final int PORT = 1234;
    private static Map<String, String> credentials = new ConcurrentHashMap<>();
    private static Map<String, Integer> scores = new ConcurrentHashMap<>();
    private static List<Question> questions = Collections.synchronizedList(new ArrayList<>());
    private static Set<String> completedUsers = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) throws IOException {
        loadQuestions("questions.txt");
        loadCredentials("users.txt");
        loadResults("results.txt");
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Quiz Server started on port " + PORT);

        while (true) {
            Socket socket = serverSocket.accept();
            new ClientHandler(socket).start();
        }
    }

    private static void loadQuestions(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            String question = line;
            List<String> options = new ArrayList<>();
            
            if (question.contains("[YN]")) {
                question = question.replace("[YN]", "").trim();
                options.add("Yes");
                options.add("No");
                int correctIndex = Integer.parseInt(reader.readLine());
                questions.add(new Question(question, options, correctIndex));
                continue;
            }
            
            for (int i = 0; i < 4; i++) {
                options.add(reader.readLine());
            }
            int correctIndex = Integer.parseInt(reader.readLine());
            questions.add(new Question(question, options, correctIndex));
        }
        reader.close();
    }

    private static void loadCredentials(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) file.createNewFile();

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 2) {
                credentials.put(parts[0], parts[1]);
            }
        }
        reader.close();
    }

    private static void loadResults(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) return;

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 2) {
                String username = parts[0];
                completedUsers.add(username);
                
                // Extract score from "score/total" format
                String[] scoreParts = parts[1].split("/");
                if (scoreParts.length >= 1) {
                    try {
                        int score = Integer.parseInt(scoreParts[0]);
                        scores.put(username, score);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid score format for user: " + username);
                    }
                }
            }
        }
        reader.close();
    }

    private static String getLeaderboard() {
        StringBuilder leaderboard = new StringBuilder("Leaderboard:\n");
        scores.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .forEach(entry -> leaderboard.append(entry.getKey())
                .append(": ")
                .append(entry.getValue())
                .append("/")
                .append(questions.size())
                .append("\n"));
        return leaderboard.toString();
    }

    static class ClientHandler extends Thread {
        private Socket socket;
        private ObjectOutputStream out;
        private ObjectInputStream in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                String username = (String) in.readObject();
                String password = (String) in.readObject();

                if (!credentials.containsKey(username)) {
                    credentials.put(username, password);
                    BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true));
                    writer.write(username + "," + password + "\n");
                    writer.close();
                } else if (!credentials.get(username).equals(password)) {
                    out.writeObject("Username already registered");
                    return;
                }

                if (completedUsers.contains(username)) {
                    out.writeObject("Already completed");
                    return;
                }

                out.writeObject("Login successful");
                completedUsers.add(username);

                out.writeObject(questions);

                List<Integer> answers = (List<Integer>) in.readObject();
                int score = 0;
                for (int i = 0; i < answers.size(); i++) {
                    if (questions.get(i).getCorrectAnswerIndex() == answers.get(i)) {
                        score++;
                    }
                }
                scores.put(username, score);

                BufferedWriter resultWriter = new BufferedWriter(new FileWriter("results.txt", true));
                resultWriter.write(username + "," + score + "/" + questions.size() + "\n");
                resultWriter.close();

                String result = "Your Score: " + score + "/" + questions.size() + "\n";
                result += getLeaderboard();
                System.out.println(result);
                out.writeObject(result);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

class Question implements Serializable {
    private static final long serialVersionUID = 1L;
    private String question;
    private List<String> options;
    private int correctAnswerIndex;

    public Question(String question, List<String> options, int correctAnswerIndex) {
        this.question = question;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }
}