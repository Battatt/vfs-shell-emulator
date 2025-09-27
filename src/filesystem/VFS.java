package filesystem;

import filesystem.exceptions.VFSException;
import filesystem.exceptions.VFSPathException;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class VFS {
    private VFSDirectory root;
    private VFSDirectory currentDirectory;
    private final Path realRootPath;


    public VFS(String realPath) throws VFSException {
        if (realPath.isEmpty()) throw new VFSPathException("Empty path");

        this.realRootPath = Paths.get(realPath).toAbsolutePath().normalize();

        try {
            if (!Files.exists(realRootPath)) {
            Files.createDirectories(realRootPath);
            }
        } catch (IOException e) {
            throw new VFSPathException(e.getMessage());
        }

        if (!Files.isDirectory(realRootPath)) {
            throw new VFSPathException("Path is not a directory: " + realPath);
        }

        this.root = new VFSDirectory("", null);
        this.currentDirectory = root;

        loadFromRealDirectory(realRootPath, root);
    }

    private void loadFromRealDirectory(Path realPath, VFSDirectory vfsDir) throws VFSException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(realPath)) {
            for (Path childPath : stream) {
                try {
                    String name = childPath.getFileName().toString();
                    if (Files.isDirectory(childPath)) {
                        VFSDirectory newDir = new VFSDirectory(name, vfsDir);
                        vfsDir.addChild(newDir);
                        loadFromRealDirectory(childPath, newDir);
                    } else {
                        String content = new String(Files.readAllBytes(childPath));
                        VFSFile newFile = new VFSFile(name, vfsDir, content);
                        vfsDir.addChild(newFile);
                    }
                } catch (VFSException | IOException e) {
                    throw new VFSPathException("Error loading: " + childPath + " - " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new VFSPathException("Error loading: " + realPath + " - " + e.getMessage());
        }
    }

    private void setBasicPermissions(Path path, String permissions) throws IOException {
        // Простая установка базовых прав
        if (permissions.matches("[0-7]{3}")) {
            int mode = Integer.parseInt(permissions, 8);
            boolean readable = (mode & 0444) != 0;  // Кто-то может читать
            boolean writable = (mode & 0222) != 0;  // Кто-то может писать
            boolean executable = (mode & 0111) != 0; // Кто-то может выполнять

            path.toFile().setReadable(readable);
            path.toFile().setWritable(writable);
            path.toFile().setExecutable(executable);
        }
    }

    public VFSDirectory getRoot() { return root; }
    public VFSDirectory getCurrentDirectory() { return currentDirectory; }
    public void setCurrentDirectory(VFSDirectory dir) { currentDirectory = dir; }

    public String getCurrentPath() {
        return buildPath(currentDirectory);
    }

    private String buildPath(VFSNode node) {
        if (node.getParent() == null) return "/";

        StringBuilder path = new StringBuilder();
        VFSNode current = node;

        // Собираем путь от текущего узла до корня
        while (current != null && current.getParent() != null) {
            path.insert(0, "/" + current.getName());
            current = current.getParent();
        }

        return path.length() > 0 ? path.toString() : "/";
    }
}
