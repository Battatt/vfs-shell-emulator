package filesystem;

public abstract class VFSNode {
    protected String name;
    protected VFSNode parent;
    protected long size;
    protected boolean isDirectory;
    protected String permissions;

    public VFSNode(String name, VFSNode parent, boolean isDirectory) {
        setName(name);
        setParent(parent);
        setDirectory(isDirectory);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public VFSNode getParent() {
        return parent;
    }

    public void setParent(VFSNode parent) {
        this.parent = parent;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public abstract long getSize();
    public abstract String getType();
    public abstract String getDescription();

    public String getInfo() {
        return String.format("%s %s %dB %s",
                isDirectory() ? "d" : "-",
                getPermissions(),
                getSize(),
                getName());
    }

    @Override
    public String toString() {
        return "VFS node [name=" + name + ", parent=" + parent +
                ", size=" + getSize() + ", type=" + getType() + "]";
    }

}
