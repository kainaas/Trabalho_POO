# Java Event Planner

A **desktop** calendar application written in **Java + Swing** to organize
appointments, developed for **SCC0204 — Object-Oriented Programming** (ICMC–USP),
Project 7.

> Create, edit and delete events (with recurrence, attendees and reminders),
> browse the calendar in four view modes (day, week, month and year), search by
> keyword and switch between a light and a dark theme.

## Features

- **Events** with title, date, time, duration, location, description and category
  (meeting, birthday, appointment or other).
- **Create / edit / delete** events from the interface.
- **Recurrence** (daily, weekly or monthly), with the option to apply an action to
  **only one occurrence** or to the **whole series**.
- **Four view modes**, switchable from the top bar:
  - **Day** — an hour-by-hour agenda of the selected day.
  - **Week** — seven columns (Sunday to Saturday) with each day's events.
  - **Month** — the classic month grid, with an **event-count badge** on each day.
  - **Year** — the twelve months at a glance; click a month to open it.
- **Attendees** per event (name and e-mail) and **reminders** (10 min, 1 hour or
  1 day before), shown at runtime.
- **Calendar date picker** in the event form (click 📅 to pick a date with the mouse).
- **Search** by title, description, location or category across every date.
- **Light / dark theme** applied to the whole interface, with theme-specific icons.
- **Automatic persistence** to a text file, reloaded when the program starts.

## Requirements

- A **JDK** installed (tested on **Java 25**; JDK 17+ should work).
- No external libraries for the application itself.
- For the tests only: `junit-platform-console-standalone-1.8.2.jar`.

## Build and run

```bash
git clone https://github.com/kainaas/Trabalho_POO.git
cd Trabalho_POO/src

# compile (Windows: compile.cmd)
javac -encoding UTF-8 -d bin ./Controller/*.java ./Model/*.java ./View/*.java ./Main/*.java

# run (Windows: Run.cmd)
cd bin
java Main.Main
```

> The working directory when running must be `bin`, so the relative icon paths in
> `bin/iconImages` (subfolders `lightMode` and `darkMode`) resolve correctly. The
> first time an event is saved, `events.txt` is created inside `bin`.

## Tests

The business logic is covered by a **JUnit 5** suite (28 tests) under `test/`. With
`junit-platform-console-standalone-1.8.2.jar` in the project root, run from the root:

```bash
test/run-tests.cmd
```

## Documentation

- API documentation (Javadoc) is generated under [`docs/javadoc/`](docs/javadoc/)
  (open `index.html`). Regenerate it with:
  ```bash
  javadoc -encoding UTF-8 -d docs/javadoc -sourcepath src -subpackages Model:View:Controller:Main
  ```
- The project report (required format) is in [`docs/relatorio.pdf`](docs/relatorio.pdf)
  (LaTeX source in [`docs/relatorio.tex`](docs/relatorio.tex)).
- The class diagram is in [`docs/uml.puml`](docs/uml.puml) (PlantUML).

## Project structure

```
Trabalho_POO/
├── src/
│   ├── Model/        # data and business rules
│   ├── View/         # Swing screens (one panel per view mode)
│   ├── Controller/   # bridge between interface and model
│   ├── Main/         # entry point
│   └── bin/          # compilation output + icons
├── test/             # JUnit 5 test suite
└── docs/             # report (LaTeX/PDF), UML and generated Javadoc
```

## Architecture

The project follows the **MVC** pattern together with the **Observer** pattern:

- `Model` holds the data and rules, `View` has the screens and `Controller`
  bridges and validates.
- `CalendarModel` extends `AbstractModel` (which wraps a `PropertyChangeSupport`).
  Any relevant change (new event, date/mode/theme change) fires a notification, and
  `CalendarView` (a `PropertyChangeListener`) repaints the panels automatically —
  without the model knowing the interface.
- The central area swaps between four panels (`DayViewPanel`, `WeekViewPanel`,
  `CalendarMonthPanel`, `YearViewPanel`), all implementing `CalendarSubView`,
  according to the active `ViewMode`.

## Authors

| Author | USP number |
| --- | --- |
| Fabio Kauê Araujo da Silva | 16311045 |
| Kainã Alves Tureso | 15466391 |
| Luís Henrique de Queiroz Veras | 14592414 |
