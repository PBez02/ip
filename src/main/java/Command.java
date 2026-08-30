/* Represents an operation that Zeus can execute. */
public abstract class Command {
    /*
     * Executes this command using the application components it needs.
     * @param tasks task collection to read or update
     * @param ui console interface used to display the result
     * @param storage persistent storage used after task changes
     * @throws ZeusException if the command cannot be completed
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws ZeusException;

    /*
     * Reports whether this command should end the command loop.
     * @return true only for an exit command
     */
    public boolean isExit() {
        return false;
    }
}
