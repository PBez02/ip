package zeus.command;

import zeus.storage.Storage;
import zeus.task.TaskList;
import zeus.ui.Ui;

/**
 * Displays tasks whose descriptions contain a keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches for the specified keyword.
     *
     * @param keyword Keyword to find in task descriptions.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Displays matching tasks without changing or saving them.
     *
     * @param tasks Task collection to search.
     * @param ui Console interface used to display matching tasks.
     * @param storage Persistent storage, which is not needed.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(tasks.find(keyword));
    }
}
