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


        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            if (part.isEmpty()) continue;

            current = resolveDirectoryPart(current, part);
        }


        String lastPart = parts[parts.length - 1];
        if (lastPart.isEmpty()) {
            return current;
        }

        VFSNode node = current.getChild(lastPart);
        if (node == null) {
            throw new VFSPathException("wrong path: " + lastPart);
        }

        return node;
    }

    private static VFSNode resolveRelativePath(VFS vfs, String pathStr) throws VFSException {
        VFSDirectory current = vfs.getCurrentDirectory();
        String[] parts = pathStr.split("/");

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.isEmpty() || part.equals(".")) {
                continue;
            }

            if (part.equals("..")) {
                if (current.getParent() != null) {
                    current = (VFSDirectory) current.getParent();
                }
                if (i == parts.length - 1) {
                    return current;
                }
                continue;
            }


            VFSNode node = current.getChild(part);
            if (node == null) {
                throw new VFSPathException("wrong path: " + part);
            }


            if (i == parts.length - 1) {
                return node;
            }


            if (!node.isDirectory()) {
                throw new VFSException("not a directory: " + part);
            }

            current = (VFSDirectory) node;
        }


        return current;
    }

    private static VFSDirectory resolveDirectoryPart(VFSDirectory current, String part) throws VFSException {
        VFSNode node = current.getChild(part);

        if (node == null) {
            throw new VFSPathException("wrong path: " + part);
        }

        if (!node.isDirectory()) {
            throw new VFSException("not a directory: " + part);
        }

        return (VFSDirectory) node;
    }
}
