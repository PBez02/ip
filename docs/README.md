# Zeus User Guide

Zeus is a task-tracking chatbot. Enter commands in the console or graphical interface to manage
your task list.

## Viewing command help

Enter `help` by itself to display the available commands:

```text
Here are the commands you can use:
  todo DESCRIPTION - Add a task without a date.
  deadline DESCRIPTION /by yyyy-MM-dd - Add a task with a due date.
  event DESCRIPTION /from yyyy-MM-dd /to yyyy-MM-dd - Add a task with start and end dates.
  list - Show all tasks.
  find KEYWORD - Find tasks whose descriptions contain the keyword.
  mark NUMBER - Mark the numbered task as done.
  unmark NUMBER - Mark the numbered task as not done.
  delete NUMBER - Delete the numbered task.
  help - Show this help page.
  bye - Exit Zeus.

Dates must use yyyy-MM-dd, for example 2026-09-06.
```

The command is case-sensitive and does not accept arguments. For example, `HELP` is unknown, while
`help todo` produces an error explaining that `help` must be entered on its own. Viewing help does
not change or save the task list.
