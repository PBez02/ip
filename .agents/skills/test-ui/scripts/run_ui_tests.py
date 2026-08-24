#!/usr/bin/env python3
"""Compile Zeus and run the Markdown-defined text UI tests."""

from __future__ import annotations

import argparse
import difflib
import os
from pathlib import Path
import re
import shutil
import subprocess
import sys
import tempfile


REQUIRED_JAVA_MAJOR = 25
REQUIRED_SDKMAN_VERSION = "25.0.3.fx-zulu"


def parse_args() -> argparse.Namespace:
    """Parse command-line options for the test runner."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--plan",
        type=Path,
        help="Path to the UI test plan (defaults to test/ui-test-plan.md)",
    )
    return parser.parse_args()


def java_version(javac: Path) -> int | None:
    """Return the major version of a Java compiler, or None if unavailable."""
    try:
        result = subprocess.run(
            [str(javac), "-version"], capture_output=True, text=True, check=False
        )
    except OSError:
        return None

    match = re.search(r"javac\s+(\d+)", result.stdout + result.stderr)
    return int(match.group(1)) if match else None


def find_java_tools() -> tuple[Path, Path]:
    """Locate Java 25, preferring the course's configured SDKMAN JDK."""
    candidate_homes: list[Path] = []
    configured_home = os.environ.get("UI_TEST_JAVA_HOME")
    if configured_home:
        candidate_homes.append(Path(configured_home))

    candidate_homes.extend(
        [
            Path.home() / ".sdkman/candidates/java" / REQUIRED_SDKMAN_VERSION,
            Path.home() / ".sdkman/candidates/java/current",
        ]
    )

    configured_java_home = os.environ.get("JAVA_HOME")
    if configured_java_home:
        candidate_homes.append(Path(configured_java_home))

    path_javac = shutil.which("javac")
    if path_javac:
        candidate_homes.append(Path(path_javac).resolve().parent.parent)

    for candidate_home in candidate_homes:
        javac = candidate_home / "bin/javac"
        java = candidate_home / "bin/java"
        if java.is_file() and java_version(javac) == REQUIRED_JAVA_MAJOR:
            return javac, java

    raise RuntimeError(
        "Java 25 was not found. Install or select "
        f"{REQUIRED_SDKMAN_VERSION}, or set UI_TEST_JAVA_HOME to a Java 25 JDK."
    )


def fenced_block(section: str, heading: str) -> str:
    """Extract a fenced text block belonging to a test-case heading."""
    pattern = rf"^### {re.escape(heading)}\s*\n\s*```(?:text)?\s*\n(.*?)\n```"
    match = re.search(pattern, section, flags=re.MULTILINE | re.DOTALL)
    if not match:
        raise ValueError(f"Missing or invalid '{heading}' block")
    return match.group(1)


def parse_plan(plan_path: Path) -> list[dict[str, object]]:
    """Parse test cases from the Markdown UI test plan."""
    plan_text = plan_path.read_text(encoding="utf-8").replace("\r\n", "\n")
    headings = list(re.finditer(r"^## (TC-[^\n]+)$", plan_text, flags=re.MULTILINE))
    if not headings:
        raise ValueError("The plan does not contain any '## TC-...' test cases")

    cases: list[dict[str, object]] = []
    for index, heading in enumerate(headings):
        section_end = headings[index + 1].start() if index + 1 < len(headings) else len(plan_text)
        section = plan_text[heading.end() : section_end]
        aim_match = re.search(r"^\*\*Aim:\*\*\s+(.+)$", section, flags=re.MULTILINE)
        if not aim_match:
            raise ValueError(f"{heading.group(1)} is missing its aim")

        cases.append(
            {
                "name": heading.group(1),
                "aim": aim_match.group(1),
                "commands": fenced_block(section, "Input").splitlines(),
                "expected": fenced_block(section, "Expected output").splitlines(),
            }
        )
    return cases


def show_lines(lines: list[str], prefix: str = "") -> None:
    """Print lines while preserving empty lines in a readable transcript."""
    if not lines:
        print(f"{prefix}<none>")
        return
    for line in lines:
        print(f"{prefix}{line}")


def run_tests(repo_root: Path, plan_path: Path) -> int:
    """Compile the application and execute all plan cases fail-fast."""
    cases = parse_plan(plan_path)
    javac, java = find_java_tools()
    source_files = sorted((repo_root / "src/main/java").glob("*.java"))
    if not source_files:
        raise RuntimeError("No Java source files found in src/main/java")

    with tempfile.TemporaryDirectory(prefix="zeus-ui-test-") as build_directory:
        compile_result = subprocess.run(
            [str(javac), "-d", build_directory, *(str(path) for path in source_files)],
            cwd=repo_root,
            capture_output=True,
            text=True,
            check=False,
        )
        if compile_result.returncode != 0:
            print("UI TEST RESULT: FAIL (compilation)")
            print(compile_result.stdout, end="")
            print(compile_result.stderr, end="", file=sys.stderr)
            return 1

        print(f"Compiled {len(source_files)} source file(s) with Java {REQUIRED_JAVA_MAJOR}.")
        for case_number, case in enumerate(cases, start=1):
            commands = case["commands"]
            expected = case["expected"]
            assert isinstance(commands, list)
            assert isinstance(expected, list)

            result = subprocess.run(
                [str(java), "-cp", build_directory, "Zeus"],
                cwd=repo_root,
                input="\n".join(commands) + "\n",
                capture_output=True,
                text=True,
                check=False,
            )
            actual = result.stdout.replace("\r\n", "\n").splitlines()

            print(f"\n=== {case['name']} ===")
            print(f"Aim: {case['aim']}")
            print("Console input:")
            show_lines(commands, prefix="> ")
            print("Console output:")
            show_lines(actual)

            if result.returncode != 0 or actual != expected:
                print("RESULT: FAIL")
                print("\nExpected output:")
                show_lines(expected)
                print("\nActual output:")
                show_lines(actual)
                if result.stderr:
                    print("\nStandard error:")
                    print(result.stderr, end="")
                print("\nDifference (expected -> actual):")
                show_lines(
                    list(
                        difflib.unified_diff(
                            expected,
                            actual,
                            fromfile="expected",
                            tofile="actual",
                            lineterm="",
                        )
                    )
                )
                print(f"\nUI TEST RESULT: FAIL ({case_number - 1}/{len(cases)} passed)")
                return 1

            print("RESULT: PASS")

    print(f"\nUI TEST RESULT: PASS ({len(cases)}/{len(cases)} passed)")
    return 0


def main() -> int:
    """Run the UI test plan and convert setup errors into concise failures."""
    args = parse_args()
    repo_root = Path(__file__).resolve().parents[4]
    plan_path = args.plan.resolve() if args.plan else repo_root / "test/ui-test-plan.md"
    try:
        return run_tests(repo_root, plan_path)
    except (OSError, RuntimeError, ValueError) as error:
        print(f"UI TEST RESULT: FAIL ({error})", file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
