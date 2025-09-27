package filesystem;

public class VFSFile extends VFSNode{
    private String content;

    public VFSFile(String name, VFSNode parent, String content) {
        super(name, parent, false);
        this.content = content;
        if (this.content == null || this.content.isEmpty()) this.size = 0;
        else this.size = content.length();
        permissions = "rw-r--r--";
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public String getType() {
        return "file";
    }

    @Override
    public String getDescription() {
        return "f " +getPermissions() + " " + getSize() + " " + getName();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
