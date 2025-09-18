import java.io.File;
import java.util.Arrays;
import java.util.Scanner;

public class CommandManager {
    private final ConsoleUI ui;
    private boolean isScriptMode = false;

    CommandManager(ConsoleUI ui) {
        this.ui = ui;
    }

    private void setScriptMode(boolean scriptMode) {
        isScriptMode = scriptMode;
    }

    public void executeScript(String scriptPath) throws EmulatorException {
        setScriptMode(true);
        File scriptFile = new File(scriptPath);
        if (!scriptFile.exists() || !scriptFile.isFile()) {
            throw new EmulatorException("Script file does not exist: " + scriptPath);
        }
        try {
            boolean running = true;
            Scanner scriptScanner = new Scanner(scriptFile);
            while (scriptScanner.hasNextLine() && running) {
                String line = scriptScanner.nextLine();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] input = line.split("\\s+");

                ui.printPrompt("vfs$ "); // ИЗМЕНИТЬ!
                ui.showMessage(line);

                String command = input[0];
                String[] args = Arrays.copyOfRange(input, 1, input.length);

                running = executeCommand(command, args);
            }
        } catch (Exception e) {
            throw new EmulatorException("Error while reading script file: " + scriptPath);
        } finally {
            setScriptMode(false);
        }
    }

    public boolean executeCommand(String command, String[] args) {
        try {
            if (command.isEmpty()) return true;
            switch (command) {
                case "exit":
                    handleExit(args);
                    return false;
                case "ls":
                    handleLS(args);
                    return true;
                case "cd":
                    handleCD(args);
                    return true;
                case "help":
                    handleHelp(args);
                    return true;
                case "hello":
                    handleHelloCat(args);
                    return true;
                default:
                    throw new EmulatorException(command + ": command not found");
            }
        } catch (EmulatorException exception) {
            ui.showError(exception.getMessage());
            return !isScriptMode;
        }
    }

    private void handleLS(String[] args) throws EmulatorException {
        if (args.length > 0) {
            throw new EmulatorException("ls: too many arguments");
        }

        ui.showMessage("ls executed");
    }

    private void handleCD(String[] args) throws EmulatorException {
        if (args.length != 1) {
            throw new EmulatorException("cd: wrong number of arguments");
        }
        for (String arg : args) {
            ui.showMessage("cd args:" + arg);
        }
        ui.showMessage("cd executed");
    }

    private void handleExit(String[] args) throws EmulatorException {
        if (args.length > 0) {
            throw new EmulatorException("exit: too many arguments");
        }
        ui.showMessage("Goodbye!");
    }

    private void handleHelp(String[] args) throws EmulatorException {
        if (args.length > 0) {
            throw new EmulatorException("help: too many arguments");
        }

        ui.showMessage("Available commands:");
        ui.showMessage("  ls     - List directory contents (заглушка)");
        ui.showMessage("  cd     - Change directory (заглушка)");
        ui.showMessage("  exit   - Exit the emulator");
        ui.showMessage("  help   - Show this help message\n");
    }

    private void handleHelloCat(String[] args) {
        ui.showMessage("  /\\_/\\");
        ui.showMessage(" ( o.o )");
        ui.showMessage("  > ^ <\n");
    }
}
