import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/* Represents a task that must be completed by a specified date. */
public class Deadline extends Task {
    /* Format used to present stored dates to the user. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    /* Date by which this task must be completed. */
    protected LocalDate by;

    /*
     * Creates an incomplete deadline task.
     * @param description description of the task
     * @param by date by which the task must be completed
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /*
     * Returns this deadline in the format stored on disk.
     * @return serialized deadline task
     */
    @Override
    public String toDataString() {
        return "D | " + super.toDataString() + " | " + by;
    }

    /*
     * Returns this task with its deadline type icon and deadline text.
     * @return formatted deadline task
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
