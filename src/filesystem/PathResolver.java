package filesystem;

import filesystem.exceptions.VFSException;
import filesystem.exceptions.VFSPathException;

public class PathResolver {

    public static VFSNode resolvePath(VFS vfs, String pathStr) throws VFSException {
        return resolvePath(vfs, pathStr, true);
    }


    public static VFSNode resolvePath(VFS vfs, String pathStr, boolean expectDirectory) throws VFSException {
        if (pathStr == null || pathStr.isEmpty()) {
            return vfs.getCurrentDirectory();
        }

        VFSNode node;
        if (pathStr.startsWith("/")) {
            node = resolveAbsolutePath(vfs, pathStr);
        } else {
            node = resolveRelativePath(vfs, pathStr);
        }

        if (expectDirectory && !node.isDirectory()) {
            throw new VFSException("not a directory: " + pathStr);
        }
        if (!expectDirectory && node.isDirectory()) {
            throw new VFSException("not a file: " + pathStr);
        }

        return node;
    }

    private static VFSNode resolveAbsolutePath(VFS vfs, String pathStr) throws VFSException {
        VFSDirectory current = vfs.getRoot();
        String[] parts = pathStr.split("/");

        // Особый случай: путь равен только "/"
        if (parts.length == 0 || (parts.length == 1 && parts[0].isEmpty())) {
            return current; // возвращаем корневую директорию
        }

        // Обрабатываем все части пути
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.isEmpty()) {
                continue; // Пропускаем пустые части
            }

            VFSNode node = current.getChild(part);
            if (node == null) {
                throw new VFSPathException("wrong path: " + part);
            }

            // Если это последняя часть - возвращаем найденный узел
            if (i == parts.length - 1) {
                return node;
            }

            // Промежуточная часть должна быть директорией
            if (!node.isDirectory()) {
                throw new VFSException("not a directory: " + part);
            }

            current = (VFSDirectory) node;
        }

        return current;
    }

    private static VFSNode resolveRelativePath(VFS vfs, String pathStr) throws VFSException {
        VFSDirectory current = vfs.getCurrentDirectory();
        String[] parts = pathStr.split("/");

        // Особый случай: путь пустой или только из слешей
        if (parts.length == 0 || (parts.length == 1 && parts[0].isEmpty())) {
            return current; // возвращаем текущую директорию
        }

        // Обрабатываем все части пути
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.isEmpty()) {
                continue; // Пропускаем пустые части
            }

            if (part.equals(".")) {
                continue;
            } else if (part.equals("..")) {
                if (current.getParent() != null) {
                    current = (VFSDirectory) current.getParent();
                }
                // Если это последняя часть - возвращаем директорию
                if (i == parts.length - 1) {
                    return current;
                }
                continue;
            }

            VFSNode node = current.getChild(part);
            if (node == null) {
                throw new VFSPathException("wrong path: " + part);
            }

            // Если это последняя часть - возвращаем найденный узел
            if (i == parts.length - 1) {
                return node;
            }

            // Промежуточная часть должна быть директорией
            if (!node.isDirectory()) {
                throw new VFSException("not a directory: " + part);
            }

            current = (VFSDirectory) node;
        }

        return current;
    }

}
