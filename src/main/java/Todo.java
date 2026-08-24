/**
 * Represents a task without an associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete to-do task.
     *
     * @param description description of the task
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this task with its to-do type icon.
     *
     * @return formatted to-do task
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
