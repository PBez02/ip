# Zeus

Zeus is a chatbot application written in Java. Given below are instructions on how to set it up.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/Zeus.java` file, right-click it, and choose `Run Zeus.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
   ____________________________________________________________
    _____
   |__  /___ _   _ ___
     / // _ \ | | / __|
    / /|  __/ |_| \__ \
   /____\___|\__,_|___/
   Hello! I'm Zeus.
   What can I do for you?
   ____________________________________________________________
   todo borrow book
   ____________________________________________________________
   Got it. I've added this task:
     [T][ ] borrow book
   Now you have 1 tasks in the list.
   ____________________________________________________________
   deadline return book /by Sunday
   ____________________________________________________________
   Got it. I've added this task:
     [D][ ] return book (by: Sunday)
   Now you have 2 tasks in the list.
   ____________________________________________________________
   event project meeting /from Mon 2pm /to 4pm
   ____________________________________________________________
   Got it. I've added this task:
     [E][ ] project meeting (from: Mon 2pm to: 4pm)
   Now you have 3 tasks in the list.
   ____________________________________________________________
   mark 2
   ____________________________________________________________
   Nice! I've marked this task as done:
     [D][X] return book (by: Sunday)
   ____________________________________________________________
   unmark 2
   ____________________________________________________________
   OK, I've marked this task as not done yet:
     [D][ ] return book (by: Sunday)
   ____________________________________________________________
   list
   ____________________________________________________________
   Here are the tasks in your list:
   1.[T][ ] borrow book
   2.[D][ ] return book (by: Sunday)
   3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
   ____________________________________________________________
   delete 3
   ____________________________________________________________
   Noted. I've removed this task:
     [E][ ] project meeting (from: Mon 2pm to: 4pm)
   Now you have 2 tasks in the list.
   ____________________________________________________________
   todo
   ____________________________________________________________
   OOPS!!! A todo needs a description after 'todo'.
   ____________________________________________________________
   blah
   ____________________________________________________________
   OOPS!!! I don't recognize that command. Try todo, deadline, event, list, mark, unmark, delete, or bye.
   ____________________________________________________________
   bye
   ____________________________________________________________
   Bye. Hope to see you again soon!
   ____________________________________________________________
   ```

## Data storage

Zeus saves the current task list to `data/zeus.txt` whenever a task is added, marked, unmarked, or deleted. It loads valid saved tasks automatically at startup and reports malformed records without crashing.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
