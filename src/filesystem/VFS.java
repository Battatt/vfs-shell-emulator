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

        this.realRootPath = Paths.get(realPath).toAbsolutePath();

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


    public VFSFile createFile(String name, String content) throws VFSException {
        Path realFilePath = realRootPath.resolve(name).normalize();
        if (!realFilePath.startsWith(realRootPath)) {
            throw new SecurityException("Attempt to escape root directory");
        }

        if (currentDirectory.getChild(name) != null) {
            throw new VFSException("File already exists: " + name);
        }

        try {
            if (Files.exists(realFilePath)) {
                throw new VFSException("File already exists in real FS: " + name);
            }

            Files.write(realFilePath, content.getBytes());
        } catch (IOException e) {
            throw new VFSException("Failed to create file: " + e.getMessage());
        }

        VFSFile newFile = new VFSFile(name, currentDirectory, content);
        currentDirectory.addChild(newFile);

        return newFile;
    }

    public VFSDirectory createDirectory(String name) throws VFSException {
        Path realDirPath = realRootPath.resolve(name).normalize();
        if (!realDirPath.startsWith(realRootPath)) {
            throw new SecurityException("Attempt to escape root directory");
        }

        try {
            Files.createDirectories(realDirPath);
        } catch (IOException e) {
            throw new VFSPathException(e.getMessage());
        }

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
