import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class ArcadeSystem {
    private List<User> users = new ArrayList<>();
    private List<ArcadeGame> games = new ArrayList<>();

    // Aggregation: users and games are passed in, not created here
    public void registerUser(User user) {
        users.add(user);
    }

    public void addGame(ArcadeGame game) {
        games.add(game);
    }

    public void playGame(User user, QuizGame game) {
        if (user.getCredits() >= game.getCost()) {
            user.deductCredits(game.getCost());
            System.out.println("\nPlaying: " + game.getName());
            game.play(); // This now runs threaded
            System.out.println("Credits left: " + user.getCredits());
        } else {
            System.out.println("Not enough credits to play " + game.getName());
        }
    }

    public List<ArcadeGame> getGames() {
        return games;
    }
}

class User {
    private String username;
    private int credits;

    public User(String username, int credits) {
        this.username = username;
        this.credits = credits;
    }
    public String getUsername() {
        return username;
    }

    public int getCredits() {
        return credits;
    }

    public void deductCredits(int amount) {
        credits -= amount;
    }

    public void addCredits(int amount) {
        credits += amount;
    }
}

class ArcadeGame {
    private String name;
    private int cost;

    public ArcadeGame(String name, int cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }
}

class QuizGame extends ArcadeGame {

    public QuizGame() {
        super("Quiz Game", 5);
    }


    public void play() {
        Scanner sc = new Scanner(System.in);
        Thread thread = new Thread(() -> {
            System.out.println("Starting Quiz Game...");
            try {
                Thread.sleep(1000);
                System.out.println("Q: What is 3 + 4?");
                int Answer = sc.nextInt();
                System.out.println(Answer);
            } catch (InterruptedException e) {
                System.out.println("Quiz Game interrupted.");
            }
        });

        thread.start();

        try {
            thread.join(); // Wait for the game to finish
        } catch (InterruptedException e) {
            System.out.println("Game interrupted in join.");
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArcadeSystem arcadeSystem = new ArcadeSystem();

        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        // Aggregation: User created independently
        User player = new User(username, 10);
        arcadeSystem.registerUser(player);

        // Aggregation: Games created outside the system
        ArcadeGame quizGame = new QuizGame();
        arcadeSystem.addGame(quizGame);

        System.out.println("\nWelcome to the Arcade, " + player.getUsername() + "!");

        while (true) {
            System.out.println("\nYour credits: " + player.getCredits());
            System.out.println("Available games:");
            int index = 1;
            for (ArcadeGame game : arcadeSystem.getGames()) {
                System.out.println(index++ + ". " + game.getName() + " (Cost: " + game.getCost() + ")");
            }
            System.out.println("0. Exit");

            System.out.print("Choose a game to play: ");
            int choice = scanner.nextInt();

            if (choice == 0) {
                System.out.println("Thank you for visiting the arcade!");
                break;
            }

            if (choice > 0 && choice <= arcadeSystem.getGames().size()) {
                QuizGame selectedGame = (QuizGame) arcadeSystem.getGames().get(choice - 1);
                arcadeSystem.playGame(player, selectedGame);
            } else {
                System.out.println("Invalid choice. Try again.");
            }
        }

        scanner.close();
    }
}
