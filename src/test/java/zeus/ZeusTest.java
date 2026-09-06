package zeus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests command responses exposed to the graphical interface. */
public class ZeusTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    public void getResponse_addAndListCommands_returnsFormattedReplies() {
        Zeus zeus = new Zeus(temporaryDirectory.resolve("zeus.txt").toString());

        String addResponse = zeus.getResponse("todo read book");
        String listResponse = zeus.getResponse("list");

        assertEquals("Got it. I've added this task:\n"
                + "  [T][ ] read book\n"
                + "Now you have 1 tasks in the list.", addResponse);
        assertEquals("Here are the tasks in your list:\n"
                + "1.[T][ ] read book", listResponse);
        assertFalse(zeus.isExitRequested());
    }

    @Test
    public void getResponse_invalidAndExitCommands_returnsRepliesAndTracksExit() {
        Zeus zeus = new Zeus(temporaryDirectory.resolve("zeus.txt").toString());

        assertEquals("OOPS!!! I don't recognize that command. Try todo, deadline, event, list, find, mark, "
                + "unmark, delete, or bye.", zeus.getResponse("unknown"));
        assertFalse(zeus.isExitRequested());

        assertEquals("Bye. Hope to see you again soon!", zeus.getResponse("bye"));
        assertTrue(zeus.isExitRequested());
    }
}
