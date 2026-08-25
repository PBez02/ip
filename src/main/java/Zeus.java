import java.util.ArrayList;
import java.util.Scanner;

/**
 * Entry point for the Zeus chatbot application.
 */
public class Zeus {
    /**
     * Identifies the operation requested by a user command.
     */
    private enum CommandType {
        BYE,
        LIST,
        MARK,
        UNMARK,
        DELETE,
        TODO,
        DEADLINE,
        EVENT,
        UNKNOWN
    }

    /**
     * Determines which supported operation a command requests.
     *
     * @param command command entered by the user
     * @return corresponding command type, or {@link CommandType#UNKNOWN}
     */
    private static CommandType getCommandType(String command) {
        if (command.equals("bye")) {
            return CommandType.BYE;
        } else if (command.equals("list")) {
            return CommandType.LIST;
        } else if (command.equals("mark") || command.startsWith("mark ")) {
            return CommandType.MARK;
        } else if (command.equals("unmark") || command.startsWith("unmark ")) {
            return CommandType.UNMARK;
        } else if (command.equals("delete") || command.startsWith("delete ")) {
            return CommandType.DELETE;
        } else if (command.equals("todo") || command.startsWith("todo ")) {
            return CommandType.TODO;
        } else if (command.equals("deadline") || command.startsWith("deadline ")) {
            return CommandType.DEADLINE;
        } else if (command.equals("event") || command.startsWith("event ")) {
            return CommandType.EVENT;
        }
        return CommandType.UNKNOWN;
    }

    /**
     * Converts a task-creation command into the corresponding task subtype.
     *
     * @param command command entered by the user
     * @return task represented by the command
     * @throws ZeusException if required task details are missing
     */
    private static Task parseTask(String command) throws ZeusException {
        if (command.equals("todo")) {
            throw new ZeusException("A todo needs a description after 'todo'.");
        } else if (command.startsWith("todo ")) {
            String description = command.substring(5).trim();
            if (description.isEmpty()) {
                throw new ZeusException("A todo needs a description after 'todo'.");
            }
            return new Todo(description);
        } else if (command.equals("deadline")) {
            throw new ZeusException("A deadline needs a description and '/by' date or time.");
        } else if (command.startsWith("deadline ")) {
            String taskDetails = command.substring(9).trim();
            int byIndex = taskDetails.indexOf("/by");
            if (byIndex < 0) {
                throw new ZeusException("A deadline needs a '/by' date or time.");
            }

            String description = taskDetails.substring(0, byIndex).trim();
            String by = taskDetails.substring(byIndex + 3).trim();
            if (description.isEmpty()) {
                throw new ZeusException("A deadline needs a description before '/by'.");
            } else if (by.isEmpty()) {
                throw new ZeusException("A deadline needs a date or time after '/by'.");
            }
            return new Deadline(description, by);
        } else if (command.equals("event")) {
            throw new ZeusException("An event needs a description, '/from' start, and '/to' end.");
        } else if (command.startsWith("event ")) {
            String taskDetails = command.substring(6).trim();
            int fromIndex = taskDetails.indexOf("/from");
            if (fromIndex < 0) {
                throw new ZeusException("An event needs a start date or time after '/from'.");
            }

            int toIndex = taskDetails.indexOf("/to", fromIndex + 5);
            if (toIndex < 0) {
                throw new ZeusException("An event needs an end date or time after '/to'.");
            }

            String description = taskDetails.substring(0, fromIndex).trim();
            String from = taskDetails.substring(fromIndex + 5, toIndex).trim();
            String to = taskDetails.substring(toIndex + 3).trim();
            if (description.isEmpty()) {
                throw new ZeusException("An event needs a description before '/from'.");
            } else if (from.isEmpty()) {
                throw new ZeusException("An event needs a start date or time after '/from'.");
            } else if (to.isEmpty()) {
                throw new ZeusException("An event needs an end date or time after '/to'.");
            }
            return new Event(description, from, to);
        }

        throw new ZeusException(
                "I don't recognize that command. Try todo, deadline, event, list, mark, unmark, "
                        + "delete, or bye."
        );
    }

    /**
     * Extracts and validates the one-based task number in a status command.
     *
     * @param command command entered by the user
     * @param commandWord status command being parsed, such as {@code mark}
     * @param taskCount number of tasks currently stored
     * @return zero-based index of the selected task
     * @throws ZeusException if the number is missing, malformed, or out of range
     */
    private static int parseTaskIndex(String command, String commandWord, int taskCount)
            throws ZeusException {
        String numberText = command.substring(commandWord.length()).trim();
        if (numberText.isEmpty()) {
            throw new ZeusException("Tell me which task to " + commandWord + ", for example '"
                    + commandWord + " 1'.");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException exception) {
            throw new ZeusException("The task number must be a whole number.");
        }

        if (taskCount == 0) {
            throw new ZeusException("Your task list is empty.");
        } else if (taskNumber < 1 || taskNumber > taskCount) {
            throw new ZeusException("There is no task number " + taskNumber
                    + ". Choose a number from 1 to " + taskCount + ".");
        }
        return taskNumber - 1;
    }

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
        ArrayList<Task> tasks = new ArrayList<>();

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();

            System.out.println(separator);
            try {
                CommandType commandType = getCommandType(command);
                switch (commandType) {
                case BYE -> {
                    System.out.println("Bye. Hope to see you again soon!");
                    System.out.println(separator);
                    return;
                }
                case LIST -> {
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println((i + 1) + "." + tasks.get(i));
                    }
                }
                case MARK -> {
                    int taskIndex = parseTaskIndex(command, "mark", tasks.size());
                    tasks.get(taskIndex).markAsDone();
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + tasks.get(taskIndex));
                }
                case UNMARK -> {
                    int taskIndex = parseTaskIndex(command, "unmark", tasks.size());
                    tasks.get(taskIndex).markAsNotDone();
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + tasks.get(taskIndex));
                }
                case DELETE -> {
                    int taskIndex = parseTaskIndex(command, "delete", tasks.size());
                    Task removedTask = tasks.remove(taskIndex);
                    System.out.println("Noted. I've removed this task:");
                    System.out.println("  " + removedTask);
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                }
                case TODO, DEADLINE, EVENT -> {
                    Task task = parseTask(command);
                    tasks.add(task);
                    System.out.println("Got it. I've added this task:");
                    System.out.println("  " + task);
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                }
                case UNKNOWN -> throw new ZeusException(
                        "I don't recognize that command. Try todo, deadline, event, list, mark, "
                                + "unmark, delete, or bye."
                );
                }
            } catch (ZeusException exception) {
                System.out.println("OOPS!!! " + exception.getMessage());
            }

            System.out.println(separator);
        }
    }
}
