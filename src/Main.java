import filesystem.exceptions.VFSException;

public class Main {
    public static void main(String[] args) {
        String vfsPath = null;
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
        }

        System.out.println("Debug: VFS path = " + vfsPath);
        System.out.println("Debug: Script path = " + scriptPath);
        System.out.println();

        try {
            if (vfsPath == null) vfsPath = "./test_basic";
            ConsoleEmulator vfsEmulator = new ConsoleEmulator(vfsPath);

            if (scriptPath != null) {
                vfsEmulator.executeScript(scriptPath);
            } else {
                vfsEmulator.startREPL();
            }
        } catch (VFSException e) {
            System.err.println("VFS initialization failed: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            System.exit(1);
        }
    }
}
