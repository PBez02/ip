/*Marks a numbered task as completed. */
public class MarkCommand extends Command {
    /* One-based number of the task to mark. */
    private final int taskNumber;

    /*
     * Creates a command for the selected task number.
     * @param taskNumber one-based task number
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /*
     * Marks and saves the task, then displays a confirmation.
     * @param tasks task collection to update
     * @param ui console interface used to display the result
     * @param storage persistent storage used after the change
     * @throws ZeusException if the task number is invalid or saving fails
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ZeusException {
        Task task = tasks.mark(taskNumber);
        storage.save(tasks);
        ui.showTaskMarked(task);
    }
}
