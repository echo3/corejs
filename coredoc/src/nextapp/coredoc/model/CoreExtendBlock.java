package nextapp.coredoc.model;

public class CoreExtendBlock extends ClassBlock {
    
    private boolean isAbstract = false;
    private String superclassName = null;
    
    public CoreExtendBlock(Module module, Bounds bounds) {
        super(module, bounds);
    }
    
    public int getModifiers() {
        int modifiers = super.getModifiers() | Modifiers.CLASS;
        if (isAbstract) {
            modifiers |= Modifiers.ABSTRACT;
        }
        return modifiers;
    }

    public void process() {
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

        int count = getStructureChildCount();
        for (int i = 0; i < count; ++i) {
            Block childBlock = getStructureChild(i);
            if (childBlock instanceof ObjectBlock) {
                ObjectBlock objectBlock = (ObjectBlock) childBlock;
                if ("$static".equals(objectBlock.getName())) {
                    processStatic(objectBlock);
                    continue;
                } if ("$virtual".equals(objectBlock.getName())) {
                    processVirtual(objectBlock);
                    continue;
                } if ("$abstract".equals(objectBlock.getName())) {
                    processAbstract(objectBlock);
                    continue;
                }
            } else if (childBlock instanceof FunctionBlock) {
                FunctionBlock functionBlock = (FunctionBlock) childBlock;
                if ("$load".equals(functionBlock.getName())) {
                    continue;
                } else if ("$construct".equals(functionBlock.getName())) {
                    functionBlock.addModifier(Modifiers.CONSTRUCTOR);
                }
            } else if (childBlock instanceof FieldBlock) {
                FieldBlock fieldBlock = (FieldBlock) childBlock;
                if ("$abstract".equals(fieldBlock.getName())) {
                    processAbstract(null);
                    continue;
                }
            }
            childBlock.addModifier(Modifiers.PROTOTYPE_PROPERTY);
            addObjectChild(childBlock);
        }
    }
    
    public void processAbstract(ObjectBlock abstractBlock) {
        this.isAbstract = true;
        if (abstractBlock == null) {
            return;
        }
        int count = abstractBlock.getStructureChildCount();
        if (count == 0) {
            return;
        }

        for (int i = 0; i < count; ++i) {
            Block childBlock = abstractBlock.getStructureChild(i);
            childBlock.addModifier(Modifiers.PROTOTYPE_PROPERTY | Modifiers.ABSTRACT);
            addObjectChild(childBlock);
        }
    }
    
    public void processStatic(ObjectBlock staticBlock) {
        int count = staticBlock.getStructureChildCount();
        if (count == 0) {
            return;
        }

        for (int i = 0; i < count; ++i) {
            Block childBlock = staticBlock.getStructureChild(i);
             
            addObjectChild(childBlock);
            
            childBlock.process();
        }
    }
    
    public void processVirtual(ObjectBlock virtualBlock) {
        int count = virtualBlock.getStructureChildCount();
        if (count == 0) {
            return;
        }

        for (int i = 0; i < count; ++i) {
            Block childBlock = virtualBlock.getStructureChild(i);
            childBlock.addModifier(Modifiers.PROTOTYPE_PROPERTY | Modifiers.VIRTUAL);
            addObjectChild(childBlock);
        }
    }
    
    public void setSuperclassName(String superclassName) {
        this.superclassName = superclassName;
    }
}
