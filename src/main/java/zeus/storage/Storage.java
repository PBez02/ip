package zeus.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import zeus.exception.ZeusException;
import zeus.task.Deadline;
import zeus.task.Event;
import zeus.task.Task;
import zeus.task.TaskList;
import zeus.task.Todo;

/** Loads tasks from disk and saves the current task list. */
public class Storage {
    /** File used to persist tasks between Zeus sessions. */
    private final Path dataFile;

    /**
     * Creates storage backed by the specified file.
     * @param filePath path of the task data file
     */
    public Storage(String filePath) {
        dataFile = Path.of(filePath);
    }

    /**
     * Loads all valid tasks and records recoverable problems as warnings.
     * @param warnings destination for user-friendly loading warnings
     * @return valid tasks in saved order, or an empty list when no file is available
     */
    public List<Task> load(List<String> warnings) {
        List<Task> tasks = new ArrayList<>();
        List<String> lines;

        try {
            if (Files.notExists(dataFile)) {
                return tasks;
            } else if (!Files.isRegularFile(dataFile)) {
                warnings.add(dataFile
                        + " is not a readable task file. Starting with an empty list.");
                return tasks;
            }
            lines = Files.readAllLines(dataFile);
        } catch (IOException | SecurityException exception) {
            warnings.add("I couldn't read " + dataFile + ". Starting with an empty list.");
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
     * Writes the current task list to disk, replacing the old file contents.
     * @param tasks tasks to save
     * @throws ZeusException if the data directory or file cannot be written
     */
    public void save(TaskList tasks) throws ZeusException {
        List<String> lines = new ArrayList<>();
        for (Task task : tasks.getTasks()) {
            lines.add(task.toDataString());
        }

        try {
            Path parentDirectory = dataFile.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            Files.write(dataFile, lines);
        } catch (IOException | SecurityException exception) {
            throw new ZeusException("I couldn't save your tasks to " + dataFile + ".");
        }
    }

    /**
     * Splits a saved line at unescaped pipe characters and unescapes its fields.
     * @param line saved task record
     * @return fields contained in the record
     * @throws ZeusException if the record ends with an escape or uses an invalid escape
     */
    private List<String> splitDataLine(String line) throws ZeusException {
        List<String> fields = new ArrayList<>();
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
     * @param line saved task record
     * @return task represented by the record
     * @throws ZeusException if the record is malformed or contains unsupported values
     */
    private Task parseSavedTask(String line) throws ZeusException {
        List<String> fields = splitDataLine(line);
        if (fields.size() < 2) {
            throw new ZeusException("A record needs a task type and completion status.");
        }

        String taskType = fields.get(0);
        int expectedFieldCount = getExpectedFieldCount(taskType);
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

        Task task = createTask(taskType, description, fields);
        if (status.equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Returns the required field count for a serialized task type.
     * @param taskType serialized task type icon
     * @return required number of fields
     * @throws ZeusException if the type is not supported
     */
    private int getExpectedFieldCount(String taskType) throws ZeusException {
        return switch (taskType) {
            case "T" -> 3;
            case "D" -> 4;
            case "E" -> 5;
            default -> throw new ZeusException("Unknown task type '" + taskType + "'.");
        };
    }

    /**
     * Creates the appropriate task subtype from validated saved fields.
     * @param taskType serialized task type icon
     * @param description task description
     * @param fields complete saved fields
     * @return reconstructed task
     * @throws ZeusException if subtype-specific values are invalid
     */
    private Task createTask(String taskType, String description, List<String> fields)
            throws ZeusException {
        if (taskType.equals("T")) {
            return new Todo(description);
        } else if (taskType.equals("D")) {
            String by = fields.get(3);
            if (by.isEmpty()) {
                throw new ZeusException("The deadline's '/by' value is empty.");
            }
            return new Deadline(description, parseDate(by, "deadline"));
        }

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
        return new Event(description, fromDate, toDate);
    }

    /**
     * Parses an ISO date stored in the data file.
     * @param dateText saved date text
     * @param fieldName name used to identify an invalid field
     * @return parsed date
     * @throws ZeusException if the text is not a valid ISO date
     */
    private LocalDate parseDate(String dateText, String fieldName) throws ZeusException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new ZeusException("The " + fieldName
                    + " date must use yyyy-MM-dd, for example 2019-10-15.");
        }
    }

    /**
     * Ensures that a saved event does not finish before it starts.
     * @param from start date
     * @param to end date
     * @throws ZeusException if the end date precedes the start date
     */
    private void validateEventDates(LocalDate from, LocalDate to) throws ZeusException {
        if (to.isBefore(from)) {
            throw new ZeusException("The event end date cannot be before its start date.");
        }
    }
}
