package nextapp.coredoc.model;

public interface Node {

    public Block getStructureChild(String name);
    
    public Block getStructureChild(int index);
    
    public int getStructureChildCount();
    
    public Block getObjectChild(int index);
    
    public int getObjectChildCount();
    
    public String getName();
    
    public String getQualifiedName();
    
    public int getModifiers();
    
    public void process();
}
