package filesystem;

import java.util.HashMap;
import java.util.Map;

public class VFSDirectory extends VFSNode{
    private Map<String, VFSNode> children;

    public VFSDirectory(String name, VFSNode parent) {
        super(name, parent, true);
        this.children = new HashMap<String, VFSNode>();
        this.size = 0;
    }

    public Map<String, VFSNode> getChildren() {
        return children;
    }

    public void addChild(VFSNode node) throws IllegalArgumentException {
        if (node == null) throw new IllegalArgumentException("Child cannot be null");
        children.put(node.getName(), node);
        size += node.getSize();
    }

    public VFSNode getChild(String name) {
        return children.get(name);
    }

    @Override
    public long getSize() {
        long resultSize = 0;
        for (VFSNode child : children.values()) {
            resultSize += child.getSize();
        }
        return resultSize;
    }

    @Override
    public String getType() {
        return "directory";
    }

    @Override
    public String getDescription() {
        return "directory name: " + getName() + ", size: " + getSize();
    }

}
