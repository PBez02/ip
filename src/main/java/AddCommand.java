/* Adds a parsed task to the task list. */
public class AddCommand extends Command {
    /* Task to add when this command executes. */
    private final Task task;

    /*
     * Creates a command for the specified task.
     * @param task task to add
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /*
     * Adds and saves the task, then displays a confirmation.
     * @param tasks task collection to update
     * @param ui console interface used to display the result
     * @param storage persistent storage used after the change
     * @throws ZeusException if the task list cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ZeusException {
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }
}
