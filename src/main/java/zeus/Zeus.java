package zeus;

import zeus.command.Command;
import zeus.exception.ZeusException;
import zeus.parser.Parser;
import zeus.storage.Storage;
import zeus.task.TaskList;
import zeus.ui.Ui;

import java.util.ArrayList;
import java.util.List;

/** Coordinates the components of the Zeus chatbot application. */
public class Zeus {
    /** Component responsible for console interaction. */
    private final Ui ui;

    /** Component responsible for persistent task storage. */
    private final Storage storage;

    /** In-memory task collection used during this session. */
    private TaskList tasks;

    /**
     * Creates Zeus using the specified task data file.
     * @param filePath path of the task data file
     */
    public Zeus(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        tasks = new TaskList();
    }

    /** Loads saved tasks and runs the command loop until the user exits or input ends. */
    public void run() {
        ui.showWelcome();

        List<String> loadWarnings = new ArrayList<>();
        tasks = new TaskList(storage.load(loadWarnings));
        ui.showLoadingWarnings(loadWarnings);

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            String fullCommand = ui.readCommand();
            try {
                Command command = Parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (ZeusException exception) {
                ui.showError(exception.getMessage());
            }

            if (!isExit) {
                ui.showResponseEnd();
            }
        }
    }

    /**
     * Starts Zeus with its project-relative data file.
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        new Zeus("data/zeus.txt").run();
    }
}
