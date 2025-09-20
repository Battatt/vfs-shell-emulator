import filesystem.exceptions.VFSException;

// RELEASE

public class Main {
    public static void main(String[] args) throws VFSException {
        // RELEASE 2
        String vfsPath = "./test_basic";
        String scriptPath = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-vfs") || args[i].equals("-v")) {
                if (i + 1 < args.length) {
                    vfsPath = args[i + 1];
                    i++;
                }
            }
            else if (args[i].equals("-script") || args[i].equals("-s")) {
                if (i + 1 < args.length) {
                    scriptPath = args[i + 1];
                    i++;
                }
            }
            else if (args[i].equals("-help") || args[i].equals("-h")) {
                printHelp();
            }
        }

        System.out.println("=== VFS Emulator Configuration ===");
        System.out.println("VFS path: " + (vfsPath != null ? vfsPath : "not specified"));
        System.out.println("Script path: " + (scriptPath != null ? scriptPath : "not specified"));
        System.out.println("===================================");
        System.out.println();




        ConsoleEmulator vfs = new ConsoleEmulator(vfsPath);
        if (scriptPath != null) {
            vfs.executeScript(scriptPath);
        }
        else {
            vfs.startREPL();
        }
    }

    private static void printHelp() {
        System.out.println("Usage: java Main [options]");
        System.out.println("Options:");
        System.out.println("  -v, -vfs <path>     Path to VFS physical location");
        System.out.println("  -s, -script <path>  Path to startup script");
        System.out.println("  -h, -help           Show this help message");
    }
}