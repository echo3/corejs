package nextapp.coredoc.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Instance
implements Node {

    private Map classToSubclasses = new HashMap();
    private Map qualifiedNameToClass = new HashMap();
    
    private List children;
    private Map childMap;
    
    private Set modules;
    
    public Instance() {
        super();
        modules = new HashSet();
    }
    
    public void addStructureChild(Block block) {
        if (block.getStructureParent() != null) {
            throw new IllegalArgumentException("Attempt to add block that already has its parent set: " + block);
        }
        block.setStructureParent(this);
        if (children == null) {
            children = new ArrayList();
            childMap = new HashMap();
        }
        children.add(block);
        childMap.put(block.getName(), block);
    }
    
    public void addModule(Module module) {
        modules.add(module);
    }
    
    public ClassBlock getClass(String className) {
        return (ClassBlock) qualifiedNameToClass.get(className);
    }
    
    public Block getStructureChild(int index) {
        if (children == null || index >= children.size()) {
            throw new IndexOutOfBoundsException(Integer.toString(index));
        }
        return (Block) children.get(index);
    }

    public Block getStructureChild(String name) {
        return childMap == null ? null : (Block) childMap.get(name);
    }

    public int getStructureChildCount() {
        return children == null ? 0 : children.size();
    }
    
    public Block getObjectChild(int index) {
        if (children == null || index >= children.size()) {
            throw new IndexOutOfBoundsException(Integer.toString(index));
        }
        return (Block) children.get(index);
    }
    public int getObjectChildCount() {
        return children == null ? 0 : children.size();
    }
    
    public String getName() {
        return null;
    }
    
    public String getQualifiedName() {
        return null;
    }
    
    private void findNamespaces(List namespaceList, Node node) {
        if ((node.getModifiers() & Modifiers.NAMESPACE) != 0) {
            if (node.getName() != null) {
                //FIXME deliberately skipping global for the moment.
                namespaceList.add(node);
            }
        }
        int count = node.getStructureChildCount();
        for (int i = 0; i < count; ++i) {
            findNamespaces(namespaceList, node.getStructureChild(i));
        }
    }
    
    public Node[] getNamespaces() {
        List namespaceList = new ArrayList();
        findNamespaces(namespaceList, this);
        
        Node[] namespaces = new Node[namespaceList.size()];
        namespaceList.toArray(namespaces);
        return namespaces;
    }
    
    private void findClasses(Collection classes, Node node) {
        if ((node.getModifiers() & Modifiers.CLASS) != 0) {
            classes.add(node);
        }
        int count = node.getObjectChildCount();
        for (int i = 0; i < count; ++i) {
            findClasses(classes, node.getObjectChild(i));
        }
    }
    
    public ClassBlock[] getClasses() {
        Set classSet = new TreeSet(new Comparator(){
        
            public int compare(Object a, Object b) {
                return ((ClassBlock) a).getQualifiedName().compareTo(((ClassBlock) b).getQualifiedName());
            }
        });

        findClasses(classSet, this);
        ClassBlock[] classes = new ClassBlock[classSet.size()];
        classSet.toArray(classes);
        return classes;
    }
    
    public int getModifiers() {
        return Modifiers.NAMESPACE;
    }
    
    public void process() {
        int count = getStructureChildCount();
        for (int i = 0; i < count; ++i) {
            getStructureChild(i).process();
        }
    }
    
    public void registerClass(ClassBlock superclass, ClassBlock subclass) {
        qualifiedNameToClass.put(subclass.getQualifiedName(), subclass);
        Set subclasses = (Set) classToSubclasses.get(superclass);
        if (subclasses == null) {
            subclasses = new HashSet();
            classToSubclasses.put(superclass, subclasses);
        }
        subclasses.add(subclass);
    }
}
