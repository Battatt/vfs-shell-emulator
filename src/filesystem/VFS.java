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


    public VFSDirectory createDirectory(String name) throws VFSException {
        // Разрешаем путь чтобы найти правильную родительскую директорию
        VFSDirectory parentDir;
        String dirName;

        if (name.contains("/")) {
            // Если путь содержит /, находим родительскую директорию
            int lastSlash = name.lastIndexOf('/');
            String parentPath = name.substring(0, lastSlash);
            dirName = name.substring(lastSlash + 1);

            try {
                VFSNode parentNode = PathResolver.resolvePath(this, parentPath, true);
                parentDir = (VFSDirectory) parentNode;
            } catch (VFSException e) {
                throw new VFSException("Invalid parent path: " + parentPath);
            }
        } else {
            // Простое имя - используем текущую директорию
            parentDir = currentDirectory;
            dirName = name;
        }

        // ПРОВЕРЯЕМ: существует ли уже такая директория в VFS
        if (parentDir.getChild(dirName) != null) {
            throw new VFSException("Directory already exists: " + dirName);
        }

        // Строим реальный путь от родительской директории
        String parentVfsPath = buildPath(parentDir).replaceFirst("^/", "");
        Path realDirPath = realRootPath;
        if (!parentVfsPath.isEmpty()) {
            realDirPath = realRootPath.resolve(parentVfsPath);
        }
        realDirPath = realDirPath.resolve(dirName).normalize();

        // Проверяем безопасность пути
        Path normalizedRoot = realRootPath.normalize();
        Path normalizedTarget = realDirPath.normalize();

        if (!normalizedTarget.startsWith(normalizedRoot)) {
            throw new SecurityException("Attempt to escape root directory");
        }

        // ПРОВЕРЯЕМ: существует ли уже такая директория в реальной ФС
        try {
            if (Files.exists(realDirPath)) {
                throw new VFSException("Directory already exists in real FS: " + dirName);
            }
            Files.createDirectories(realDirPath);
        } catch (IOException e) {
            throw new VFSException("Failed to create directory: " + e.getMessage());
        }

        // Создаем в VFS
        VFSDirectory newDir = new VFSDirectory(dirName, parentDir);
        parentDir.addChild(newDir);

        return newDir;
    }

    public VFSFile createFile(String name, String content) throws VFSException {
        // Разрешаем путь чтобы найти правильную родительскую директорию
        VFSDirectory parentDir;
        String fileName;

        if (name.contains("/")) {
            int lastSlash = name.lastIndexOf('/');
            String parentPath = name.substring(0, lastSlash);
            fileName = name.substring(lastSlash + 1);

            try {
                VFSNode parentNode = PathResolver.resolvePath(this, parentPath, true);
                parentDir = (VFSDirectory) parentNode;
            } catch (VFSException e) {
                throw new VFSException("Invalid parent path: " + parentPath);
            }
        } else {
            parentDir = currentDirectory;
            fileName = name;
        }

        // Строим реальный путь
        String parentVfsPath = buildPath(parentDir).replaceFirst("^/", "");
        Path realFilePath = realRootPath;
        if (!parentVfsPath.isEmpty()) {
            realFilePath = realRootPath.resolve(parentVfsPath);
        }
        realFilePath = realFilePath.resolve(fileName).normalize();

        // Проверка безопасности
        Path normalizedRoot = realRootPath.normalize();
        Path normalizedTarget = realFilePath.normalize();

        if (!normalizedTarget.startsWith(normalizedRoot)) {
            throw new SecurityException("Attempt to escape root directory");
        }

        // Проверяем существование в VFS
        if (parentDir.getChild(fileName) != null) {
            throw new VFSException("File already exists: " + fileName);
        }

        try {
            if (Files.exists(realFilePath)) {
                throw new VFSException("File already exists in real FS: " + fileName);
            }

            Files.write(realFilePath, content.getBytes());
        } catch (IOException e) {
            throw new VFSException("Failed to create file: " + e.getMessage());
        }

        VFSFile newFile = new VFSFile(fileName, parentDir, content);
        parentDir.addChild(newFile);

        return newFile;
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
