import filesystem.VFS;
import filesystem.exceptions.VFSException;

import java.util.Arrays;
import java.util.Scanner;

public class ConsoleEmulator {
    private final Scanner sc;
    private boolean isRunning;
    private final CommandManager commandManager;
    private final ConsoleUI ui;
    private final VFS vfs;


    public ConsoleEmulator(String vfsPath) throws VFSException {
        this.vfs = new VFS(vfsPath);
        sc = new Scanner(System.in);
        isRunning = false;
        ui = new ConsoleUI();
        commandManager = new CommandManager(ui, vfs);
    }

    public void welcome() {
        ui.showMessage("Welcome to VFS Emulator!");
        ui.showMessage("Type 'help' to see available commands.");
        ui.showMessage("");
    }

    public void executeScript(String scriptPath) {
        try {
            commandManager.executeScript(scriptPath);
        } catch(EmulatorException e) {
            ui.showError(e.getMessage());
            shutdown();
        }
    }

    public void startREPL() {
        isRunning = true;
        welcome();
        while (isRunning) {
            ui.printPrompt("vfs$ "); // ИЗМЕНИТЬ НА ПУТЬ К ВИРТУАЛКЕ

            String[] input = sc.nextLine().split("\\s+");
            if (input.length == 0 || input[0].isEmpty()) continue;

            String command = input[0];
            String[] args = Arrays.copyOfRange(input, 1, input.length);

            isRunning = commandManager.executeCommand(command, args);
        }
        shutdown();
    }

    public void shutdown() {
        sc.close();
    }
}
