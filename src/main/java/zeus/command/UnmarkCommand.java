package zeus.command;

import zeus.exception.ZeusException;
import zeus.storage.Storage;
import zeus.task.Task;
import zeus.task.TaskList;
import zeus.ui.Ui;

/** Marks a numbered task as incomplete. */
public class UnmarkCommand extends Command {
    /** One-based number of the task to unmark. */
    private final int taskNumber;

    /**
     * Creates a command for the selected task number.
     * @param taskNumber one-based task number
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Unmarks and saves the task, then displays a confirmation.
     * @param tasks task collection to update
     * @param ui console interface used to display the result
     * @param storage persistent storage used after the change
     * @throws ZeusException if the task number is invalid or saving fails
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ZeusException {
        Task task = tasks.unmark(taskNumber);
        storage.save(tasks);
        ui.showTaskUnmarked(task);
    }
}
