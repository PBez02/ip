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
