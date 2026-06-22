@echo off
REM Compiles and runs the JUnit 5 test suite.
REM Requires junit-platform-console-standalone-1.8.2.jar next to this folder
REM (download from https://search.maven.org if it is not present).
REM Run this script from the project root: test\run-tests.cmd

set JAR=junit-platform-console-standalone-1.8.2.jar

if not exist src\bin\Model\Event.class (
    echo Compiling the application first...
    pushd src
    call compile.cmd
    popd
)

if not exist test\bin mkdir test\bin

echo Compiling tests...
javac -encoding UTF-8 -cp "%JAR%;src\bin" -d test\bin test\*.java || exit /b 1

echo Running tests...
java -jar "%JAR%" --classpath "src\bin;test\bin" ^
  --select-class EventTest ^
  --select-class CalendarModelTest ^
  --select-class ControllerTest ^
  --select-class EventStorageTest ^
  --select-class ModelValueTest
