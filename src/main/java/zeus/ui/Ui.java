package zeus.ui;

import zeus.task.Task;

import java.util.List;
import java.util.Scanner;

/** Handles all console input and output for the Zeus chatbot. */
public class Ui {
    /** Visual boundary used between commands and responses. */
    private static final String SEPARATOR =
            "____________________________________________________________";

    /** Name banner displayed when Zeus starts. */
    private static final String BANNER = " _____\n"
            + "|__  /___ _   _ ___\n"
            + "  / // _ \\ | | / __|\n"
            + " / /|  __/ |_| \\__ \\\n"
            + "/____\\___|\\__,_|___/\n";

    /** Source of commands entered by the user. */
    private final Scanner scanner;

    /** Creates a console UI that reads from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Displays Zeus's banner and greeting. */
    public void showWelcome() {
        System.out.println(SEPARATOR);
        System.out.print(BANNER);
        System.out.println("Hello! I'm Zeus.");
        System.out.println("What can I do for you?");
        System.out.println(SEPARATOR);
    }

    /**
     * Reports whether another command is available.
     * @return true when another input line can be read
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command and displays the response boundary.
     * @return trimmed command entered by the user
     */
    public String readCommand() {
        String command = scanner.nextLine().trim();
        System.out.println(SEPARATOR);
        return command;
    }

    /**
     * Displays recoverable problems found while loading saved tasks.
     * @param warnings loading warning messages
     */
    public void showLoadingWarnings(List<String> warnings) {
        for (String warning : warnings) {
            showError(warning);
        }
        if (!warnings.isEmpty()) {
            System.out.println(SEPARATOR);
        }
    }

    /**
     * Displays all tasks in their current order.
     * @param tasks tasks to display
     */
    public void showTaskList(List<Task> tasks) {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Confirms that a task was marked as done.
     * @param task task whose status changed
     */
    public void showTaskMarked(Task task) {
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + task);
    }

    /**
     * Confirms that a task was marked as not done.
     * @param task task whose status changed
     */
    public void showTaskUnmarked(Task task) {
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + task);
    }

    /**
     * Confirms that a task was removed.
     * @param task task that was removed
     * @param taskCount number of remaining tasks
     */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Confirms that a task was added.
     * @param task task that was added
     * @param taskCount number of stored tasks
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays a user-friendly error message.
     * @param message explanation of the error
     */
    public void showError(String message) {
        System.out.println("OOPS!!! " + message);
    }

    /** Displays Zeus's farewell and closes the final response boundary. */
    public void showGoodbye() {
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(SEPARATOR);
    }

    /** Closes the response boundary for a completed command. */
    public void showResponseEnd() {
        System.out.println(SEPARATOR);
    }
}
