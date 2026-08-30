import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Entry point for the Zeus chatbot application.
 */
public class Zeus {
    /** Location of the file used to persist tasks between runs. */
    private static final Path DATA_FILE = Path.of("data", "zeus.txt");

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
            throw new ZeusException("A deadline needs a description and '/by' date.");
        } else if (command.startsWith("deadline ")) {
            String taskDetails = command.substring(9).trim();
            int byIndex = taskDetails.indexOf("/by");
            if (byIndex < 0) {
                throw new ZeusException("A deadline needs a '/by' date.");
            }

            String description = taskDetails.substring(0, byIndex).trim();
            String by = taskDetails.substring(byIndex + 3).trim();
            if (description.isEmpty()) {
                throw new ZeusException("A deadline needs a description before '/by'.");
            } else if (by.isEmpty()) {
                throw new ZeusException("A deadline needs a date after '/by'.");
            }
            return new Deadline(description, parseDate(by, "deadline"));
        } else if (command.equals("event")) {
            throw new ZeusException(
                    "An event needs a description, '/from' start date, and '/to' end date.");
        } else if (command.startsWith("event ")) {
            String taskDetails = command.substring(6).trim();
            int fromIndex = taskDetails.indexOf("/from");
            if (fromIndex < 0) {
                throw new ZeusException("An event needs a start date after '/from'.");
            }

            int toIndex = taskDetails.indexOf("/to", fromIndex + 5);
            if (toIndex < 0) {
                throw new ZeusException("An event needs an end date after '/to'.");
            }

            String description = taskDetails.substring(0, fromIndex).trim();
            String from = taskDetails.substring(fromIndex + 5, toIndex).trim();
            String to = taskDetails.substring(toIndex + 3).trim();
            if (description.isEmpty()) {
                throw new ZeusException("An event needs a description before '/from'.");
            } else if (from.isEmpty()) {
                throw new ZeusException("An event needs a start date after '/from'.");
            } else if (to.isEmpty()) {
                throw new ZeusException("An event needs an end date after '/to'.");
            }

            LocalDate fromDate = parseDate(from, "event start");
            LocalDate toDate = parseDate(to, "event end");
            validateEventDates(fromDate, toDate);
            return new Event(description, fromDate, toDate);
        }

        throw new ZeusException(
                "I don't recognize that command. Try todo, deadline, event, list, mark, unmark, "
                        + "delete, or bye."
        );
    }

    /**
     * Parses a date written in the ISO {@code yyyy-MM-dd} format.
     *
     * @param dateText date entered by the user or read from storage
     * @param fieldName name used to identify the invalid field in an error message
     * @return parsed date
     * @throws ZeusException if the text is not a valid ISO date
     */
    private static LocalDate parseDate(String dateText, String fieldName) throws ZeusException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new ZeusException("The " + fieldName
                    + " date must use yyyy-MM-dd, for example 2019-10-15.");
        }
    }

    /**
     * Ensures that an event does not finish before it starts.
     *
     * @param from start date
     * @param to end date
     * @throws ZeusException if the end date precedes the start date
     */
    private static void validateEventDates(LocalDate from, LocalDate to) throws ZeusException {
        if (to.isBefore(from)) {
            throw new ZeusException("The event end date cannot be before its start date.");
        }
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

    /**
     * Splits a saved line at unescaped pipe characters and unescapes its fields.
     *
     * @param line saved task record
     * @return fields contained in the record
     * @throws ZeusException if the record ends with an escape or uses an invalid escape
     */
    private static List<String> splitDataLine(String line) throws ZeusException {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean isEscaped = false;

        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (isEscaped) {
                if (character != '\\' && character != '|') {
                    throw new ZeusException("Invalid escape sequence '\\" + character + "'.");
                }
                field.append(character);
                isEscaped = false;
            } else if (character == '\\') {
                isEscaped = true;
            } else if (character == '|') {
                fields.add(field.toString().trim());
                field.setLength(0);
            } else {
                field.append(character);
            }
        }

        if (isEscaped) {
            throw new ZeusException("The record ends with an incomplete escape sequence.");
        }
        fields.add(field.toString().trim());
        return fields;
    }

    /**
     * Converts one validated data-file record into a task.
     *
     * @param line saved task record
     * @return task represented by the record
     * @throws ZeusException if the record is malformed or contains unsupported values
     */
    private static Task parseSavedTask(String line) throws ZeusException {
        List<String> fields = splitDataLine(line);
        if (fields.size() < 2) {
            throw new ZeusException("A record needs a task type and completion status.");
        }

        String taskType = fields.get(0);
        int expectedFieldCount;
        if (taskType.equals("T")) {
            expectedFieldCount = 3;
        } else if (taskType.equals("D")) {
            expectedFieldCount = 4;
        } else if (taskType.equals("E")) {
            expectedFieldCount = 5;
        } else {
            throw new ZeusException("Unknown task type '" + taskType + "'.");
        }

        if (fields.size() != expectedFieldCount) {
            throw new ZeusException("Task type '" + taskType + "' needs " + expectedFieldCount
                    + " fields, but this record has " + fields.size() + ".");
        }

        String status = fields.get(1);
        if (!status.equals("0") && !status.equals("1")) {
            throw new ZeusException("Completion status must be 0 or 1, not '" + status + "'.");
        }

        String description = fields.get(2);
        if (description.isEmpty()) {
            throw new ZeusException("The task description is empty.");
        }

        Task task;
        if (taskType.equals("T")) {
            task = new Todo(description);
        } else if (taskType.equals("D")) {
            String by = fields.get(3);
            if (by.isEmpty()) {
                throw new ZeusException("The deadline's '/by' value is empty.");
            }
            task = new Deadline(description, parseDate(by, "deadline"));
        } else {
            String from = fields.get(3);
            String to = fields.get(4);
            if (from.isEmpty()) {
                throw new ZeusException("The event's '/from' value is empty.");
            } else if (to.isEmpty()) {
                throw new ZeusException("The event's '/to' value is empty.");
            }
            LocalDate fromDate = parseDate(from, "event start");
            LocalDate toDate = parseDate(to, "event end");
            validateEventDates(fromDate, toDate);
            task = new Event(description, fromDate, toDate);
        }

        if (status.equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Loads all valid tasks from disk and records recoverable problems as warnings.
     *
     * @param warnings destination for user-friendly loading warnings
     * @return valid tasks loaded in file order, or an empty list when no file is available
     */
    private static List<Task> loadTasks(List<String> warnings) {
        ArrayList<Task> tasks = new ArrayList<>();
        List<String> lines;

        try {
            if (Files.notExists(DATA_FILE)) {
                return tasks;
            } else if (!Files.isRegularFile(DATA_FILE)) {
                warnings.add(DATA_FILE
                        + " is not a readable task file. Starting with an empty list.");
                return tasks;
            }
            lines = Files.readAllLines(DATA_FILE);
        } catch (IOException | SecurityException exception) {
            warnings.add("I couldn't read " + DATA_FILE + ". Starting with an empty list.");
            return tasks;
        }

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }

            try {
                tasks.add(parseSavedTask(line));
            } catch (ZeusException exception) {
                warnings.add("Saved data line " + (i + 1) + " was ignored: "
                        + exception.getMessage());
            }
        }
        return tasks;
    }

    /**
     * Writes the current task list to the data file, replacing its old contents.
     *
     * @param tasks tasks to save
     * @throws ZeusException if the data directory or file cannot be written
     */
    private static void saveTasks(List<Task> tasks) throws ZeusException {
        ArrayList<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(task.toDataString());
        }

        try {
            Files.createDirectories(DATA_FILE.getParent());
            Files.write(DATA_FILE, lines);
        } catch (IOException | SecurityException exception) {
            throw new ZeusException("I couldn't save your tasks to " + DATA_FILE + ".");
        }
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
        List<String> loadWarnings = new ArrayList<>();
        List<Task> tasks = loadTasks(loadWarnings);
        for (String warning : loadWarnings) {
            System.out.println("OOPS!!! " + warning);
        }
        if (!loadWarnings.isEmpty()) {
            System.out.println(separator);
        }

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
                    saveTasks(tasks);
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + tasks.get(taskIndex));
                }
                case UNMARK -> {
                    int taskIndex = parseTaskIndex(command, "unmark", tasks.size());
                    tasks.get(taskIndex).markAsNotDone();
                    saveTasks(tasks);
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + tasks.get(taskIndex));
                }
                case DELETE -> {
                    int taskIndex = parseTaskIndex(command, "delete", tasks.size());
                    Task removedTask = tasks.remove(taskIndex);
                    saveTasks(tasks);
                    System.out.println("Noted. I've removed this task:");
                    System.out.println("  " + removedTask);
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                }
                case TODO, DEADLINE, EVENT -> {
                    Task task = parseTask(command);
                    tasks.add(task);
                    saveTasks(tasks);
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
