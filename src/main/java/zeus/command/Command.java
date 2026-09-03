package zeus.command;

import zeus.exception.ZeusException;
import zeus.storage.Storage;
import zeus.task.TaskList;
import zeus.ui.Ui;

/** Represents an operation that Zeus can execute. */
public abstract class Command {
    /** Creates a command. */
    public Command() {
    }

    /**
     * Executes this command using the application components it needs.
     * @param tasks task collection to read or update
     * @param ui console interface used to display the result
     * @param storage persistent storage used after task changes
     * @throws ZeusException if the command cannot be completed
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws ZeusException;

    /**
     * Reports whether this command should end the command loop.
     * @return true only for an exit command
     */
    public boolean isExit() {
        return false;
    }
}
