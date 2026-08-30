package zeus.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/* Represents a task that takes place between specified start and end dates. */
public class Event extends Task {
    /* Format used to present stored dates to the user. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    /* Date on which this event starts. */
    protected LocalDate from;

    /* Date on which this event ends. */
    protected LocalDate to;

    /*
     * Creates an incomplete event task.
     * @param description description of the event
     * @param from date on which the event starts
     * @param to date on which the event ends
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /*
     * Returns this event in the format stored on disk.
     * @return serialized event task
     */
    @Override
    public String toDataString() {
        return "E | " + super.toDataString() + " | " + from + " | " + to;
    }

    /*
     * Returns this task with its event type icon and start/end text.
     * @return formatted event task
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(DISPLAY_DATE_FORMAT)
                + " to: " + to.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
