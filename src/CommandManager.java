public class CommandManager {
    private final ConsoleUI ui;

    CommandManager(ConsoleUI ui) {
        this.ui = ui;
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
                default:
                    throw new EmulatorException(command + ": command not found");
            }
        } catch (EmulatorException exception) {
            ui.showError(exception.getMessage());
            return true;
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
        ui.showMessage("  help   - Show this help message");
        ui.showMessage("");
    }
}
