/* Ends the current Zeus session. */
public class ExitCommand extends Command {
    /*
     * Displays Zeus's farewell message.
     * @param tasks task collection, which is not changed
     * @param ui console interface used to display the farewell
     * @param storage persistent storage, which is not needed
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /*
     * Signals that the command loop should stop.
     * @return true
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
