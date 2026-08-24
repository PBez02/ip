import java.util.Scanner;

/**
 * Entry point for the Zeus chatbot application.
 */
public class Zeus {
    public static void main(String[] args) {
        String banner = " _____\n"
                + "|__  /___ _   _ ___\n"
                + "  / // _ \\ | | / __|\n"
                + " / /|  __/ |_| \\__ \\\n"
                + "/____\\___|\\__,_|___/\n";
        String separator = "____________________________________________________________";

        System.out.println(separator);
        System.out.print(banner);
        System.out.println("Hello! I'm Zeus.");
        System.out.println("What can I do for you?");
        System.out.println(separator);

        Scanner scanner = new Scanner(System.in);
        Task[] tasks = new Task[100];
        int taskCount = 0;

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();

            System.out.println(separator);
            if (command.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(separator);
                break;
            } else if (command.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + "." + tasks[i]);
                }
            } else if (command.startsWith("mark ")) {
                int taskIndex = Integer.parseInt(command.substring(5)) - 1;
                tasks[taskIndex].markAsDone();
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  " + tasks[taskIndex]);
            } else if (command.startsWith("unmark ")) {
                int taskIndex = Integer.parseInt(command.substring(7)) - 1;
                tasks[taskIndex].markAsNotDone();
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("  " + tasks[taskIndex]);
            } else {
                tasks[taskCount] = new Task(command);
                taskCount++;
                System.out.println("added: " + command);
            }

            System.out.println(separator);
        }
    }
}
