package nextapp.coredoc.model;

public class Bounds 
implements Comparable {

    private int endIndex;
    private Bounds parent;
    private int startIndex;
    
    public Bounds(Bounds parent, int startIndex, int endIndex) {
        super();
        this.parent = parent;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }
    
    /**
     * @see java.lang.Comparable#compareTo(T)
     */
    public int compareTo(Object o) {
        Bounds that = (Bounds) o;
        return this.startIndex - that.startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }
    
    public Bounds getParent() {
        return parent;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public void setParent(Bounds parent) {
        this.parent = parent;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Bounds: {" + startIndex + "-" + endIndex + "}";
    }
}
