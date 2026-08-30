import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/* Converts user input into executable commands. */
public final class Parser {
    /* Prevents creation of a stateless parser object. */
    private Parser() {
    }

    /*
     * Parses a full input line into the corresponding command object.
     * @param fullCommand command entered by the user
     * @return command ready to execute
     * @throws ZeusException if the command or its arguments are invalid
     */
    public static Command parse(String fullCommand) throws ZeusException {
        if (fullCommand.equals("bye")) {
            return new ExitCommand();
        } else if (fullCommand.equals("list")) {
            return new ListCommand();
        } else if (isNumberedCommand(fullCommand, "mark")) {
            return new MarkCommand(parseTaskNumber(fullCommand, "mark"));
        } else if (isNumberedCommand(fullCommand, "unmark")) {
            return new UnmarkCommand(parseTaskNumber(fullCommand, "unmark"));
        } else if (isNumberedCommand(fullCommand, "delete")) {
            return new DeleteCommand(parseTaskNumber(fullCommand, "delete"));
        } else if (isTaskCommand(fullCommand)) {
            return new AddCommand(parseTask(fullCommand));
        }

        throw new ZeusException(
                "I don't recognize that command. Try todo, deadline, event, list, mark, "
                        + "unmark, delete, or bye."
        );
    }

    /*
     * Reports whether input begins with the specified numbered command word.
     * @param fullCommand full user input
     * @param commandWord command word to recognize
     * @return true when the word is the complete command name
     */
    private static boolean isNumberedCommand(String fullCommand, String commandWord) {
        return fullCommand.equals(commandWord) || fullCommand.startsWith(commandWord + " ");
    }

    /*
     * Reports whether input requests creation of a supported task type.
     * @param fullCommand full user input
     * @return true for todo, deadline, or event input
     */
    private static boolean isTaskCommand(String fullCommand) {
        return fullCommand.equals("todo") || fullCommand.startsWith("todo ")
                || fullCommand.equals("deadline") || fullCommand.startsWith("deadline ")
                || fullCommand.equals("event") || fullCommand.startsWith("event ");
    }

    /*
     * Converts a task-creation command into the corresponding task subtype.
     * @param fullCommand command entered by the user
     * @return task represented by the command
     * @throws ZeusException if required task details are missing or invalid
     */
    private static Task parseTask(String fullCommand) throws ZeusException {
        if (fullCommand.equals("todo")) {
            throw new ZeusException("A todo needs a description after 'todo'.");
        } else if (fullCommand.startsWith("todo ")) {
            String description = fullCommand.substring(5).trim();
            if (description.isEmpty()) {
                throw new ZeusException("A todo needs a description after 'todo'.");
            }
            return new Todo(description);
        } else if (fullCommand.equals("deadline")) {
            throw new ZeusException("A deadline needs a description and '/by' date.");
        } else if (fullCommand.startsWith("deadline ")) {
            return parseDeadline(fullCommand);
        } else if (fullCommand.equals("event")) {
            throw new ZeusException(
                    "An event needs a description, '/from' start date, and '/to' end date.");
        }
        return parseEvent(fullCommand);
    }

    /*
     * Parses a deadline command after its command word has been recognized.
     * @param fullCommand full deadline command
     * @return parsed deadline
     * @throws ZeusException if its description or date is invalid
     */
    private static Deadline parseDeadline(String fullCommand) throws ZeusException {
        String taskDetails = fullCommand.substring(9).trim();
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
    }

    /*
     * Parses an event command after its command word has been recognized.
     * @param fullCommand full event command
     * @return parsed event
     * @throws ZeusException if its description or dates are invalid
     */
    private static Event parseEvent(String fullCommand) throws ZeusException {
        String taskDetails = fullCommand.substring(6).trim();
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

    /*
     * Extracts the one-based task number from a numbered command.
     * @param fullCommand command entered by the user
     * @param commandWord command word being parsed
     * @return one-based task number
     * @throws ZeusException if the number is missing or malformed
     */
    private static int parseTaskNumber(String fullCommand, String commandWord)
            throws ZeusException {
        String numberText = fullCommand.substring(commandWord.length()).trim();
        if (numberText.isEmpty()) {
            throw new ZeusException("Tell me which task to " + commandWord + ", for example '"
                    + commandWord + " 1'.");
        }

        try {
            return Integer.parseInt(numberText);
        } catch (NumberFormatException exception) {
            throw new ZeusException("The task number must be a whole number.");
        }
    }

    /*
     * Parses a date written in the ISO {@code yyyy-MM-dd} format.
     * @param dateText date entered by the user
     * @param fieldName name used to identify an invalid field
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

    /*
     * Ensures that an event does not finish before it starts.
     * @param from start date
     * @param to end date
     * @throws ZeusException if the end date precedes the start date
     */
    private static void validateEventDates(LocalDate from, LocalDate to) throws ZeusException {
        if (to.isBefore(from)) {
            throw new ZeusException("The event end date cannot be before its start date.");
        }
    }
}
