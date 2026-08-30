# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Moderate
* IDE and level of expertise: Moderate

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## Text UI testing

After every code update:

1. Review `test/ui-test-plan.md` and update it when the change affects commands, messages, formatting, or other observable behavior.
2. Invoke the project-specific `test-ui` skill in `.agents/skills/test-ui`.
3. Stop at the first failing UI test and report its actual and expected output.

## JUnit testing

After every code update, review and update the JUnit tests as needed to keep focused
coverage of approximately the top 50% highest-value methods. Prioritize complex, core,
and critical business logic over trivial accessors or console-printing methods, and run
the JUnit suite using Gradle and Java 25.
