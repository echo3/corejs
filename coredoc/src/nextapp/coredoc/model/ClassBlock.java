package nextapp.coredoc.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class ClassBlock extends Block {

    public ClassBlock(Module module, Bounds bounds) {
        super(module, bounds);
    }
    
    public ClassBlock superclass;
    private Set subclasses;
    
    public void addSubclass(ClassBlock subclass) {
        if (subclasses == null) {
            subclasses = new HashSet();
        }
        subclasses.add(subclass);
        subclass.superclass = this;
    }
    
    private void findClasses(List classList, Node node) {
        if ((node.getModifiers() & Modifiers.CLASS) != 0) {
            classList.add(node);
        }
        int count = node.getStructureChildCount();
        for (int i = 0; i < count; ++i) {
            Node childNode = node.getStructureChild(i);
            if ((childNode.getModifiers() & Modifiers.NAMESPACE) != 0) {
                // Entering new namespace.
                continue;
            }
            findClasses(classList, childNode);
        }
    }
    
    public int getSubclassCount() {
        return subclasses == null ? 0 : subclasses.size();
    }
    
    public Iterator getSubclasses() {
        if (subclasses == null) {
            return Collections.EMPTY_SET.iterator();
        } else {
            return Collections.unmodifiableSet(subclasses).iterator();
        }
    }
    
    public ClassBlock getSuperclass() {
        return superclass;
    }
    
    public ClassBlock[] getClasses() {
        List classList = new ArrayList();
        findClasses(classList, this);
        
        ClassBlock[] classes = new ClassBlock[classList.size()];
        classList.toArray(classes);
        return classes;
    }
    
    public Block getConstructor() {
        int count = getStructureChildCount();
        for (int i = 0; i < count; ++i) {
            if ((getStructureChild(i).getModifiers() & Modifiers.CONSTRUCTOR) != 0) {
                return getStructureChild(i);
            }
        }
        return null;
    }
    
    public int getModifiers() {
        int modifiers = 0;
        
        DocComment docComment = getDocComment();
        if (docComment != null) {
            if (docComment.getTags("@namespace") != null) {
                modifiers |= Modifiers.CLASS | Modifiers.NAMESPACE;
            } else {
                if (docComment.getTags("@class") != null) {
                    modifiers |= Modifiers.CLASS;
                }
            }
        }
        
        return modifiers;
    }
    
    public boolean isInstanceOf(String type) {
        ClassBlock testClass = this;
        while (testClass != null) {
            if (type.equals(testClass.getQualifiedName())) {
                return true;
            }
            testClass = testClass.superclass;
        }
        return false;
    }
}