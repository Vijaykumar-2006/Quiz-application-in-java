
# CNPRO — Java Quiz Client-Server (Quiz Application)

## Project Summary (1–2 lines)
A simple Java-based quiz application with a server and multiple clients. The server serves quiz questions (from `questions.txt`) and collects results (`results.txt`). Clients connect over TCP sockets; convenient `.bat` launchers are included for Windows users.

## Key features
- Server (`QuizServer1`) manages quiz session(s) and connected clients.
- Two sample client implementations (`QuizClient1`, `QuizClient2`) that communicate with the server.
- Questions stored in `questions.txt`; user accounts and results are in `users.txt` and `results.txt` respectively.
- Ready-to-run `.class` files and `.bat` launchers included for quick testing on Windows.
- Lightweight, plain-Java implementation — good for learning sockets, threads, and basic I/O.

## Repository structure (auto-detected)
```
CNPRO/  
├─ QuizServer1.java            # Server source  
├─ QuizClient1.java            # Client source (variant 1)  
├─ QuizClient2.java            # Client source (variant 2)  
├─ QuizServer1.bat             # Batch script to run server (Windows)  
├─ QuizClient1.bat             # Batch script to run client1 (Windows)  
├─ QuizClient2.bat             # Batch script to run client2 (Windows)  
├─ questions.txt               # Plain-text questions used by the server  
├─ users.txt                   # Optional user data (credentials/profile)  
├─ results.txt                 # Output written by server with user results  
├─ *.class                     # Compiled Java bytecode for quick run  
└─ (other helper files)  
```
> Note: the repo contains compiled `.class` files. If you modify sources, recompile before running.

## Prerequisites
- Java Development Kit (JDK) 8+ installed and `javac`/`java` on PATH.
- (Optional) Windows to use `.bat` files; on Linux/macOS use terminal commands below.
- Basic knowledge of running Java programs and TCP networking fundamentals.

## Build and run (Linux / macOS / Windows WSL)
1. **Compile sources** (if you want to rebuild from `.java`):
```bash
cd CNPRO
javac QuizServer1.java QuizClient1.java QuizClient2.java
```
This will produce `.class` files in the same folder.

2. **Run the server** (terminal):
```bash
# default port used in code (open the Java file to confirm port, commonly 5000 or 8000)
java QuizServer1
```
Or on Windows double-click / run `QuizServer1.bat` (or `./QuizServer1.bat` in PowerShell).

3. **Run one or more clients** (each in its own terminal):
```bash
java QuizClient1
# or
java QuizClient2
```
On Windows you can run `QuizClient1.bat` / `QuizClient2.bat` to launch the clients quickly.

## How the system works (high-level)
- Server opens a TCP ServerSocket and listens for incoming client connections.
- When a client connects, the server starts a handler thread to manage that client.
- The server serves questions (read from `questions.txt`) and receives answers from each client.
- The server records scores/results into `results.txt` and may update `users.txt` (if implemented).
- Clients provide a simple console-based UI to show questions and collect user input.

## Important files to edit
- **`questions.txt`** — Add or edit quiz questions. Check file format (see preview below) before adding new lines to maintain parsing expectations.
- **`users.txt`** — If the project supports authentication, store user records here (format depends on server parsing).
- **`QuizServer1.java`** — Server logic: port, concurrency model, rules, and result output path.
- **`QuizClient*.java`** — Client UI and communication logic.

### Quick preview of `questions.txt` (first lines from the uploaded archive)
```
(Preview of top lines present in the uploaded archive)

(questions.txt not present)
```

## Example: Typical command-line run (explicit port)
If `QuizServer1` accepts a port argument, run it like:
```bash
java QuizServer1 5000
java QuizClient1 localhost 5000
```
If it does not accept args, edit `QuizServer1.java` to make port configurable and recompile.

## Troubleshooting & notes
- **Port in use**: If the server fails to bind, ensure the configured port is free or change it in source.
- **Firewall**: Allow the chosen port through your OS firewall if clients run on different machines.
- **Encoding**: If questions include non-ASCII characters, ensure files are UTF-8 and Java code reads them accordingly.
- **Multiple clients**: Start each client in a separate terminal. The server should accept concurrent connections (thread-per-client model). If you see blocking, the server might be single-threaded—inspect `QuizServer1.java` for `new Thread` usage.

## Suggested improvements (ideas)
- Migrate client-server messages to `ObjectInputStream/ObjectOutputStream` with serializable `Question` objects (structured communication).
- Add a GUI client (Swing/JavaFX) for better UX.
- Persist results to a lightweight DB (SQLite) instead of `results.txt`.
- Add authentication and a simple admin console to add/remove questions at runtime.
- Add unit tests and CI configuration (GitHub Actions) to compile & test on push.

## Contributing
1. Fork the repository.
2. Create a branch: `feature/your-feature`.
3. Make changes and ensure code compiles.
4. Submit a pull request describing your changes and motivation.

## License & Attribution
Add a LICENSE file if you plan to open-source this (MIT recommended for demos).

---
*Generated README by ChatGPT after inspecting the uploaded zip. Edit contact info, port numbers, and any details that depend on your local environment.*
