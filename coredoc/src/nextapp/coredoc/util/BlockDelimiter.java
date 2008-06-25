package nextapp.coredoc.util;

public class BlockDelimiter 
implements Comparable {

    private int closeIndex; 
    private int openIndex;
    private BlockDelimiter parent;
    
    public BlockDelimiter(BlockDelimiter parent, int openIndex) {
        this.parent = parent;
        this.openIndex = openIndex;
    }

    public int compareTo(Object o) {
        BlockDelimiter that = (BlockDelimiter) o;
        return this.openIndex - that.openIndex;
    }

    public int getCloseIndex() {
        return closeIndex;
    }

    public int getOpenIndex() {
        return openIndex;
    }

    public BlockDelimiter getParent() {
        return parent;
    }

    public void setCloseIndex(int closeIndex) {
        this.closeIndex = closeIndex;
    }
    
    public String toString() {
        return "BlockDelimiter: {" + openIndex + "-" + closeIndex + "}";
    }
}
