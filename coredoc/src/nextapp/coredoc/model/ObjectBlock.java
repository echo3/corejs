package nextapp.coredoc.model;

public class ObjectBlock extends ClassBlock {
    
    private String superclassName = null;
    
    public ObjectBlock(Module module, Bounds bounds) {
        super(module, bounds);
    }

    public void process() {
        int count = getStructureChildCount();
        for (int i = 0; i < count; ++i) {
            Block child = getStructureChild(i);
            addObjectChild(child);
        }

        processChildren();

        Instance instance = getModule().getInstance();
        if (superclassName == null) {
            instance.registerClass(null, this);
        } else {
            ClassBlock superclass = instance.getClass(superclassName);
            instance.registerClass(superclass, this);
            if (superclass != null) {
                superclass.addSubclass(this);
            }
        }
    }
}