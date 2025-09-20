package filesystem;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class VFS {
    private VFSDirectory root;
    private VFSDirectory currentDirectory;
    private final Path realRootPath;


    public VFS(String realPath) throws IOException {
        if (realPath.isEmpty()) throw new IOException("Empty path");

        this.realRootPath = Paths.get(realPath).toAbsolutePath();

        if (!Files.exists(realRootPath)) {
            Files.createDirectories(realRootPath);
        }

        if (!Files.isDirectory(realRootPath)) {
            throw new IOException("Path is not a directory: " + realPath);
        }

        this.root = new VFSDirectory("", null);
        this.currentDirectory = root;

        loadFromRealDirectory(realRootPath, root);
    }

    private void loadFromRealDirectory(Path realPath, VFSDirectory vfsDir) throws IOException {
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
                } catch (IOException e) {
                    throw new IOException("Error loading: " + childPath + " - " + e.getMessage());
                }
            }
        }
    }

    public VFSFile createFile(String name, String content) throws IOException {
        // Проверяем, что путь остается внутри корневой директории
        Path realFilePath = realRootPath.resolve(name).normalize();
        if (!realFilePath.startsWith(realRootPath)) {
            throw new SecurityException("Attempt to escape root directory");
        }

        // Создаем в реальной ФС
        Files.write(realFilePath, content.getBytes());

        // Создаем в виртуальной ФС
        VFSFile newFile = new VFSFile(name, currentDirectory, content);
        currentDirectory.addChild(newFile);

        return newFile;
    }

    public VFSDirectory createDirectory(String name) throws IOException {
        Path realDirPath = realRootPath.resolve(name).normalize();
        if (!realDirPath.startsWith(realRootPath)) {
            throw new SecurityException("Attempt to escape root directory");
        }

        Files.createDirectories(realDirPath);

        VFSDirectory newDir = new VFSDirectory(name, currentDirectory);
        currentDirectory.addChild(newDir);

        return newDir;
    }

    public VFSDirectory getRoot() { return root; }
    public VFSDirectory getCurrentDirectory() { return currentDirectory; }
    public void setCurrentDirectory(VFSDirectory dir) { currentDirectory = dir; }

    public String getCurrentPath() {
        return buildPath(currentDirectory);
    }

    private String buildPath(VFSNode node) {
        if (node.getParent() == null) return "/";
        return buildPath(node.getParent()) + node.getName() + "/";
    }
}
