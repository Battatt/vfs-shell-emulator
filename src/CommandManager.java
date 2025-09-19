import filesystem.*;
import filesystem.exceptions.VFSException;

import java.io.File;
import java.util.Arrays;
import java.util.Scanner;

public class CommandManager {
    private final ConsoleUI ui;
    private final VFS vfs;
    private boolean isScriptMode = false;

    CommandManager(ConsoleUI ui, VFS vfs) {
        this.ui = ui;
        this.vfs = vfs;
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
        }
        finally {
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
                case "pwd":
                    handlePWD(args);
                    return true;
                case "date":
                    handleDATE(args);
                    return true;
                case "clear":
                    handleCLEAR(args);
                    return true;
                case "tail":
                    handleTAIL(args);
                    return true;
                default:
                    throw new EmulatorException(command + ": command not found");
            }
        } catch (EmulatorException | VFSException exception) {
            ui.showError(exception.getMessage());
            return !isScriptMode;
        }
    }

    private void handleLS(String[] args) throws EmulatorException, VFSException {
        VFSDirectory targetDir = vfs.getCurrentDirectory();

        if (args.length > 0) {
            VFSNode node = PathResolver.resolvePath(vfs, args[0]);
            if (!node.isDirectory()) {
                throw new EmulatorException(args[0] + " is not a directory");
            }
            targetDir = (VFSDirectory) node;
        }
        for (VFSNode child: targetDir.getChildren().values()) {
            ui.showMessage(child.getInfo());
        }

    }

    private void handleCD(String[] args) throws EmulatorException, VFSException {
        if (args.length != 1) {
            throw new EmulatorException("cd: wrong number of arguments");
        }
        VFSNode node = PathResolver.resolvePath(vfs, args[0]);
        if (!node.isDirectory()) {
            throw new EmulatorException(args[0] + " is not a directory");
        }
        vfs.setCurrentDirectory((VFSDirectory) node);
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
        ui.showMessage("  pwd    - Print working directory");
        ui.showMessage("  ls     - List directory contents");
        ui.showMessage("  cd     - Change directory");
        ui.showMessage("  date   - Show current date/time");
        ui.showMessage("  clear  - Clear screen");
        ui.showMessage("  exit   - Exit the emulator");
        ui.showMessage("  help   - Show this help message");
        ui.showMessage("");
    }

    private void handleHelloCat(String[] args) {
        ui.showMessage("  /\\_/\\");
        ui.showMessage(" ( o.o )");
        ui.showMessage("  > ^ <\n");
    }

    public void handlePWD(String[] args) throws EmulatorException {
        if (args.length > 0) throw new EmulatorException("pwd: too many arguments");
        ui.showMessage(vfs.getCurrentPath());
    }

    private void handleDATE(String[] args) throws EmulatorException {
        if (args.length > 0) throw new EmulatorException("date: too many arguments");
        ui.showMessage(new java.util.Date().toString());
    }

    private void handleCLEAR(String[] args) throws EmulatorException {
        if (args.length > 0) throw new EmulatorException("clear: too many arguments");
        ui.showMessage("\033[H\033[2J");
        System.out.flush();
    }

    private void handleTAIL(String[] args) throws EmulatorException, VFSException {
        if (args.length != 1) throw new EmulatorException("tail: need filename");

        VFSNode node = PathResolver.resolvePath(vfs, args[0], false);
        if (node.isDirectory()) {
            throw new EmulatorException("tail: is a directory: " + args[0]);
        }

        VFSFile file = (VFSFile) node;
        String content = file.getContent();
        if (content == null || content.isEmpty()) {
            ui.showMessage("");
            return;
        }

        String[] lines = content.split("\n");
        int start = Math.max(0, lines.length - 10);
        for (int i = start; i < lines.length; i++) {
            ui.showMessage(lines[i]);
        }
    }
}
