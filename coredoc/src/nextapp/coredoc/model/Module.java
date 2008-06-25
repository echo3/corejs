package nextapp.coredoc.model;

import java.util.HashSet;
import java.util.Set;

public class Module 
implements Node {
    
    private Instance instance;
    
    private Set moduleBlocks = new HashSet();
    
    public Module(Instance instance) {
        super();
        this.instance = instance;
    }
    
    public void addStructureChild(Block block) {
        moduleBlocks.add(block);
        instance.addStructureChild(block);
    }
    
    public Block getStructureChild(int index) {
        return instance.getStructureChild(index);
    }
    
    public Block getStructureChild(String name) {
        return instance.getStructureChild(name);
    }
    
    public int getStructureChildCount() {
        return instance.getStructureChildCount();
    }

    public Block getObjectChild(int index) {
        return instance.getObjectChild(index);
    }
    
    public int getObjectChildCount() {
        return instance.getObjectChildCount();
    }
    
    public String getQualifiedName() {
        return instance.getQualifiedName();
    }
    
    public Instance getInstance() {
        return instance;
    }
    
    public int getModifiers() {
        return instance.getModifiers();
    }
    
    public String getName() {
        return instance.getName();
    }
    
    public void process() {
        instance.process();
    }
}
