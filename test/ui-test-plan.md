# UI Test Plan

Each case is run in a fresh Zeus process. Inputs are sent in order through standard input. Expected output contains only program output, so adjacent separators represent the points where console input appears during an interactive session.

## TC-1: Add and list all task types

**Aim:** Verify that `todo`, `deadline`, and `event` create the correct task types and `list` displays their details in insertion order.

### Input

```text
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
list
bye
```

### Expected output

```text
____________________________________________________________
 _____
|__  /___ _   _ ___
  / // _ \ | | / __|
 / /|  __/ |_| \__ \
/____\___|\__,_|___/
Hello! I'm Zeus.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Sunday)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: Sunday)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-2: Mark typed tasks as done

**Aim:** Verify that `mark N` updates inherited task status while preserving each task's subtype formatting.

### Input

```text
todo read book
deadline return book /by June 6th
mark 1
mark 2
list
bye
```

### Expected output

```text
____________________________________________________________
 _____
|__  /___ _   _ ___
  / // _ \ | | / __|
 / /|  __/ |_| \__ \
/____\___|\__,_|___/
Hello! I'm Zeus.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: June 6th)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [D][X] return book (by: June 6th)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: June 6th)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-3: Unmark a typed task

**Aim:** Verify that `unmark N` reverses inherited task status without losing deadline details.

### Input

```text
todo read book
deadline return book /by June 6th
mark 2
unmark 2
list
bye
```

### Expected output

```text
____________________________________________________________
 _____
|__  /___ _   _ ___
  / // _ \ | | / __|
 / /|  __/ |_| \__ \
/____\___|\__,_|___/
Hello! I'm Zeus.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: June 6th)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [D][X] return book (by: June 6th)
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [D][ ] return book (by: June 6th)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: June 6th)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-4: Preserve arbitrary date and time text

**Aim:** Verify that deadline and event date/time fields are stored and displayed as unparsed strings.

### Input

```text
deadline do homework /by no idea :-p
event orientation week /from 4/10/2019 /to 11/10/2019
list
bye
```

### Expected output

```text
____________________________________________________________
 _____
|__  /___ _   _ ___
  / // _ \ | | / __|
 / /|  __/ |_| \__ \
/____\___|\__,_|___/
Hello! I'm Zeus.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] do homework (by: no idea :-p)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] orientation week (from: 4/10/2019 to: 11/10/2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] do homework (by: no idea :-p)
2.[E][ ] orientation week (from: 4/10/2019 to: 11/10/2019)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-5: Reject empty and unknown commands

**Aim:** Verify that empty task commands and unknown commands produce helpful errors without stopping Zeus.

### Input

```text
todo
deadline
event
blah
bye
```

### Expected output

```text
____________________________________________________________
 _____
|__  /___ _   _ ___
  / // _ \ | | / __|
 / /|  __/ |_| \__ \
/____\___|\__,_|___/
Hello! I'm Zeus.
What can I do for you?
____________________________________________________________
____________________________________________________________
OOPS!!! A todo needs a description after 'todo'.
____________________________________________________________
____________________________________________________________
OOPS!!! A deadline needs a description and '/by' date or time.
____________________________________________________________
____________________________________________________________
OOPS!!! An event needs a description, '/from' start, and '/to' end.
____________________________________________________________
____________________________________________________________
OOPS!!! I don't recognize that command. Try todo, deadline, event, list, mark, unmark, or bye.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-6: Reject malformed deadline and event details

**Aim:** Verify that missing descriptions, delimiters, dates, and times produce field-specific errors.

### Input

```text
deadline submit report
deadline /by Sunday
deadline submit report /by
event meeting
event /from Monday /to Tuesday
event meeting /from /to Tuesday
event meeting /from Monday
event meeting /from Monday /to
bye
```

### Expected output

```text
____________________________________________________________
 _____
|__  /___ _   _ ___
  / // _ \ | | / __|
 / /|  __/ |_| \__ \
/____\___|\__,_|___/
Hello! I'm Zeus.
What can I do for you?
____________________________________________________________
____________________________________________________________
OOPS!!! A deadline needs a '/by' date or time.
____________________________________________________________
____________________________________________________________
OOPS!!! A deadline needs a description before '/by'.
____________________________________________________________
____________________________________________________________
OOPS!!! A deadline needs a date or time after '/by'.
____________________________________________________________
____________________________________________________________
OOPS!!! An event needs a start date or time after '/from'.
____________________________________________________________
____________________________________________________________
OOPS!!! An event needs a description before '/from'.
____________________________________________________________
____________________________________________________________
OOPS!!! An event needs a start date or time after '/from'.
____________________________________________________________
____________________________________________________________
OOPS!!! An event needs an end date or time after '/to'.
____________________________________________________________
____________________________________________________________
OOPS!!! An event needs an end date or time after '/to'.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-7: Reject invalid task numbers

**Aim:** Verify that status commands reject missing, non-numeric, and out-of-range task numbers without changing tasks.

### Input

```text
mark
mark two
mark 1
todo valid task
mark 0
mark 2
unmark
unmark two
unmark 2
list
bye
```

### Expected output

```text
____________________________________________________________
 _____
|__  /___ _   _ ___
  / // _ \ | | / __|
 / /|  __/ |_| \__ \
/____\___|\__,_|___/
Hello! I'm Zeus.
What can I do for you?
____________________________________________________________
____________________________________________________________
OOPS!!! Tell me which task to mark, for example 'mark 1'.
____________________________________________________________
____________________________________________________________
OOPS!!! The task number must be a whole number.
____________________________________________________________
____________________________________________________________
OOPS!!! Your task list is empty.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] valid task
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
OOPS!!! There is no task number 0. Choose a number from 1 to 1.
____________________________________________________________
____________________________________________________________
OOPS!!! There is no task number 2. Choose a number from 1 to 1.
____________________________________________________________
____________________________________________________________
OOPS!!! Tell me which task to unmark, for example 'unmark 1'.
____________________________________________________________
____________________________________________________________
OOPS!!! The task number must be a whole number.
____________________________________________________________
____________________________________________________________
OOPS!!! There is no task number 2. Choose a number from 1 to 1.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] valid task
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
