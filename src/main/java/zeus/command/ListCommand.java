package zeus.command;

import zeus.storage.Storage;
import zeus.task.TaskList;
import zeus.ui.Ui;

/* Displays every task in the task list. */
public class ListCommand extends Command {
    /*
     * Displays the tasks without changing or saving them.
     * @param tasks task collection to display
     * @param ui console interface used to display the tasks
     * @param storage persistent storage, which is not needed
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks.getTasks());
    }
}
