package filesystem;

import filesystem.exceptions.VFSException;
import filesystem.exceptions.VFSPathException;

public class PathResolver {

    public static VFSNode resolvePath(VFS vfs, String pathStr) throws VFSException {
        if (pathStr == null || pathStr.isEmpty()) {
            return vfs.getCurrentDirectory();
        }

        if (pathStr.startsWith("/")) {
            return resolveAbsolutePath(vfs, pathStr);
        } else {
            return resolveRelativePath(vfs, pathStr);
        }
    }

    private static VFSNode resolveAbsolutePath(VFS vfs, String pathStr) throws VFSException {
        VFSDirectory current = vfs.getRoot();
        String[] parts = pathStr.split("/");

        for (String part : parts) {
            if (part.isEmpty()) continue;

            current = resolveDirectoryPart(current, part);
        }

        return current;
    }

    private static VFSNode resolveRelativePath(VFS vfs, String pathStr) throws VFSException {
        VFSDirectory current = vfs.getCurrentDirectory();
        String[] parts = pathStr.split("/");

        for (String part : parts) {
            if (part.isEmpty()) continue;

            if (part.equals(".")) {
                continue;
            } else if (part.equals("..")) {
                if (current.getParent() != null) {
                    current = (VFSDirectory) current.getParent();
                }
            } else {
                current = resolveDirectoryPart(current, part);
            }
        }

        return current;
    }

    private static VFSDirectory resolveDirectoryPart(VFSDirectory current, String part) throws VFSException {
        VFSNode node = current.getChild(part);

        if (node == null) {
            throw new VFSPathException("wrong path: " + part);
        }

        if (!node.isDirectory()) {
            throw new VFSException("not a dircetory: " + part);
        }

        return (VFSDirectory) node;
    }
}
