package nextapp.coredoc.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import nextapp.coredoc.util.Patterns;

public abstract class Block
implements Comparable, Node {

    private Bounds bounds;
    private List structureChildList;
    private Map structureChildMap;
    private String declaredName;
    private Module module;
    private Node structureParent;
    private String name;
    private DocComment docComment;
    private List objectChildList;
    private int modifiers = 0;
    private Block objectParent;
    
    public Block(Module module, Bounds bounds) {
        super();
        this.module = module;
        this.bounds = bounds;
    }
    
    public void addModifier(int modifier) {
        modifiers |= modifier;
    }

    protected void addObjectChild(Block block) {
        if (objectChildList == null) {
            objectChildList = new ArrayList();
        }
        objectChildList.add(block);
        block.setObjectParent(this);
    }
    
    public void addStructureChild(Block block) {
        block.setStructureParent(this);
        if (structureChildList == null) {
            structureChildList = new ArrayList();
            structureChildMap = new HashMap();
        }
        structureChildList.add(block);
        structureChildMap.put(block.getName(), block);
    }
    
    public int compareTo(Object o) {
        Block that = (Block) o;
        return this.bounds.getStartIndex() - that.bounds.getStartIndex();
    }
    
    public Block findChild(int startIndex) {
        if (structureChildList != null) {
            Iterator it = structureChildList.iterator();
            while (it.hasNext()) {
                Block block = (Block) it.next();
                if (block.getBounds().getStartIndex() == startIndex) {
                    return block;
                }
                block = block.findChild(startIndex);
                if (block != null) {
                    return block;
                }
            }
        }
        return null;
    }
    
    public Bounds getBounds() {
        return bounds;
    }
    
    public Block getObjectChild(int index) {
        if (objectChildList == null || index >= objectChildList.size()) {
            throw new IndexOutOfBoundsException(Integer.toString(index));
        }
        return (Block) objectChildList.get(index);
    }
    
    public int getObjectChildCount() {
        return objectChildList == null ? 0 : objectChildList.size();
    }

    public Block getObjectParent() {
        return objectParent;
    }
    
    public Block getStructureChild(String name) {
        return structureChildMap == null ? null : (Block) structureChildMap.get(name);
    }
    
    public Block getStructureChild(int index) {
        if (structureChildList == null || index >= structureChildList.size()) {
            throw new IndexOutOfBoundsException(Integer.toString(index));
        }
        return (Block) structureChildList.get(index);
    }
    
    public int getStructureChildCount() {
        return structureChildList == null ? 0 : structureChildList.size();
    }

    public String getDeclaredName() {
        return declaredName;
    }
    
    public DocComment getDocComment() {
        return docComment;
    }
    
    public String getQualifiedName() {
        if (objectParent == null || !(objectParent instanceof Block)) {
            return name;
        } else {
            return ((Block) objectParent).getQualifiedName() + "." + name;
        }
    }
    
    public int getModifiers() {
        int modifiers = this.modifiers;
        if (name.charAt(0) == '_') {
            modifiers |= Modifiers.INTERNAL;
        }
        return modifiers;
    }
    
    public Module getModule() {
        return module;
    }

    public Node getStructureParent() {
        return structureParent;
    }
    
    public String getName() {
        return name;
    }
    
    public String getContainerName() {
        if (objectParent == null || !(objectParent instanceof Block)) {
            return null;
        } else {
            return ((Block) objectParent).getQualifiedName();
        }
    }
    
    protected void processChildren() {
        int count = getStructureChildCount();
        for (int i = 0; i < count; ++i) {
            getStructureChild(i).process();
        }
    }
    
    public void setObjectParent(Block parent) {
        if (this.objectParent != null) {
            throw new IllegalStateException("Object parent previously set.");
        }
        this.objectParent = parent;
    }
    
    public void setStructureParent(Node parent) {
        if (this.structureParent != null) {
            throw new IllegalStateException("Structure parent previously set.");
        }
        this.structureParent = parent;
    }
    
    public void setDeclaredName(String declaredName) {
        this.declaredName = declaredName;
        Matcher matcher = Patterns.fqNameTokenizer.matcher(declaredName);
        matcher.find();
        this.name = matcher.group(matcher.group(matcher.groupCount()) == null ? matcher.groupCount() - 1 : matcher.groupCount());
        if (!(this instanceof CoreExtendBlock)) {
            return;
        }
    }
    
    public void setDocComment(DocComment docComment) {
        this.docComment = docComment;
    }
    
    public String toString() {
        return this.name;
    }
}