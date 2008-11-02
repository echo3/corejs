package nextapp.coredoc.render;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import nextapp.coredoc.model.Block;
import nextapp.coredoc.model.ClassBlock;
import nextapp.coredoc.model.DocComment;
import nextapp.coredoc.model.FieldBlock;
import nextapp.coredoc.model.FunctionBlock;
import nextapp.coredoc.model.Modifiers;
import nextapp.coredoc.model.FunctionBlock.Parameter;
import nextapp.coredoc.util.StringUtil;

public class ClassDO {
    
    public static class PropertyDO {
        
        protected Block block;
        protected DocComment docComment;
        
        public PropertyDO(Block block) {
            this.block = block;
            docComment = block.getDocComment(); 
        }
        
        public String getName() {
            if ((block.getModifiers() & Modifiers.CONSTRUCTOR) == 0) {
                return block.getName();
            } else {
                return block.getQualifiedName();
            }
        }
        
        public String getShortDescription() {
            return docComment == null || docComment.getShortDescription() == null ? "" : docComment.getShortDescription();
        }
        
        public String getDescription() {
            return docComment == null || docComment.getDescription() == null ? "" : docComment.getDescription();
        }
        
        public String getModifiers() {
            List modifierList = new ArrayList();
            int modifiers = block.getModifiers();
            if ((modifiers & Modifiers.INTERNAL) != 0) {
                modifierList.add("Internal");
            } else {
                modifierList.add("Public");
            }

            if ((modifiers & Modifiers.ABSTRACT) != 0) {
                modifierList.add("Abstract");
            } else if ((modifiers & Modifiers.VIRTUAL) != 0) {
                modifierList.add("Virtual");
            }
            
            return StringUtil.join(modifierList, ", ");
        }
        
        public String getParameterString() {
            return "()";
        }
    }
    
    public static class FunctionDO extends PropertyDO {
    
        public FunctionDO(FunctionBlock functionBlock) {
            super(functionBlock);
        }
        
        public boolean hasReturnValue() {
            return ((FunctionBlock) block).getReturnDescription() != null
                   || ((FunctionBlock) block).getReturnType() != null;
        }
        
        public String getReturnType() {
            return ((FunctionBlock) block).getReturnType();
        }
        
        public String getReturnDescription() {
            return ((FunctionBlock) block).getReturnDescription();
        }
        
        public boolean hasParameters() {
            return ((FunctionBlock) block).getParameters() != null;
        }
        
        public Iterator getParameters() {
            FunctionBlock functionBlock = (FunctionBlock) block;
            final Parameter[] parameters = functionBlock.getParameters();
            if (parameters == null) {
                return null;
            } else {
                return new Iterator() {
                    private int i = 0;
                    
                    public boolean hasNext() {
                        return i < parameters.length;
                    }
                    
                    public Object next() {
                        return new FunctionParameterDO(parameters[i++]);
                    }
                    
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        }
    }
    
    public static class FunctionParameterDO {
        
        private FunctionBlock.Parameter parameter;
        
        public FunctionParameterDO(FunctionBlock.Parameter parameter) {
            super();
            this.parameter = parameter;
        }
        
        public String getDescription() {
            return parameter.getDescription();
        }
        
        public String getName() {
            return parameter.getName();
        }
        
        public String getType() {
            return parameter.getType();
        }
    }

    private List classMethods = new ArrayList();
    private List instanceMethods = new ArrayList();
    private List classProperties = new ArrayList();
    private List instanceProperties = new ArrayList();
    private List classFields = new ArrayList();
    private List instanceFields = new ArrayList();
    private ClassBlock classBlock;
    private Renderer renderer;
    private PropertyDO constructor;
    private Set descendantClasses = new TreeSet();
    private String qualifiedName;
    
    public ClassDO(Renderer renderer, ClassBlock classBlock) {
        super();
        
        this.renderer = renderer;
        this.classBlock = classBlock;
        qualifiedName = classBlock.getQualifiedName();
        
        System.err.println("Processing: " + qualifiedName);
        
        int childCount = classBlock.getObjectChildCount();
        
        for (int i = 0; i < childCount; ++i) {
            Block childBlock = classBlock.getObjectChild(i);
            if (childBlock instanceof FunctionBlock) {
                FunctionBlock functionBlock = (FunctionBlock) childBlock;
                if ((childBlock.getModifiers() & Modifiers.CONSTRUCTOR) != 0) {
                    constructor = new FunctionDO(functionBlock);
                } else if ((childBlock.getModifiers() & Modifiers.PROTOTYPE_PROPERTY) != 0) {
                    instanceMethods.add(new FunctionDO(functionBlock));
                } else {
                    classMethods.add(new FunctionDO(functionBlock));
                }
            } else if (childBlock instanceof FieldBlock) {
                FieldBlock fieldBlock = (FieldBlock) childBlock;
                if ((childBlock.getModifiers() & Modifiers.PROTOTYPE_PROPERTY) != 0) {
                    instanceFields.add(new PropertyDO(fieldBlock));
                } else {
                    classFields.add(new PropertyDO(fieldBlock));
                }
            } else {
//                if ((childBlock.getModifiers() & Modifiers.CLASS) != 0) {
//                    if ((childBlock.getModifiers() & Modifiers.PROTOTYPE_PROPERTY) == 0) {
//                        System.err.println("***CLASS:" + childBlock.getName());
//                    } else {
//                        System.err.println("***PROTOTYPE:" + childBlock.getName());
//                    }
//                }
            }
        }
        
        ClassBlock[] classes = classBlock.getClasses();
        descendantClasses = new TreeSet();
        for (int i = 0; i < classes.length; ++i) {
            if (!qualifiedName.equals(classes[i].getQualifiedName())) {
                descendantClasses.add(classes[i].getQualifiedName());
            }
        }        
    }
    
    public PropertyDO getConstructor() {
        return constructor;
    }
    
    public Iterator getClassFields() {
        return classFields.size() == 0 ? null : classFields.iterator();
    }
    
    public Iterator getInstanceFields() {
        return instanceFields.size() == 0 ? null : instanceFields.iterator();
    }
    
    public Iterator getClassMethods() {
        return classMethods.size() == 0 ? null : classMethods.iterator();
    }
    
    public Iterator getClassHierarchy(boolean reverse, boolean includeSelf) {
        List hierarchy = new ArrayList();
        ClassBlock searchClassBlock = classBlock;
        if (!includeSelf) {
            searchClassBlock = searchClassBlock.getSuperclass();
        }
        while (searchClassBlock != null) {
            if (reverse) {
                hierarchy.add(searchClassBlock);
            } else {
                hierarchy.add(0, searchClassBlock);
            }
            searchClassBlock = searchClassBlock.getSuperclass();
        }
        return hierarchy.iterator();
    }
    
    public Iterator getCustomSummaryBlocks() 
    throws Exception {
        DocComment docComment = classBlock.getDocComment();
        if (docComment == null) {
            return null;
        }
        
        List customSummaryBlocks = null;
        
        Iterator tagRenderNameIt = renderer.getTagRenderNames();
        while (tagRenderNameIt.hasNext()) {
            String tagRenderName = (String) tagRenderNameIt.next();
            CustomTagRender tagRender = renderer.getTagRender(tagRenderName);
            String requiredType = tagRender.getRequiredType();
            if (requiredType != null && !classBlock.isInstanceOf(requiredType)) {
                // Class does not meet required type specification.
                continue;
            }
            
            Iterator tagIt = docComment.getTags("@" + tagRenderName);
            if (tagIt == null) {
                // No instances of the custom tag found in doc comment.
                continue;
            }
            
            if (customSummaryBlocks == null) {
                customSummaryBlocks = new ArrayList();
            }
            
            StringWriter sw = new StringWriter();
            tagRender.render(classBlock, sw);
            sw.flush();
            
            customSummaryBlocks.add(sw.toString());
        }
        
        return customSummaryBlocks == null ? null : customSummaryBlocks.iterator();
    }
    
    public Iterator getDescendantClasses() {
        return descendantClasses.iterator();
    }
    
    public boolean hasDescendantClasses() {
        return descendantClasses.size() > 0;
    }
    
    public ClassBlock getSuperclass() {
        return classBlock.getSuperclass();
    }
    
    public Iterator getSubclasses() {
        Set sortedSet = new TreeSet(new Comparator() {
            public int compare(Object a, Object b) {
                return ((ClassBlock) a).getQualifiedName().compareTo(((ClassBlock) b).getQualifiedName());
            }
        });
        Iterator it = classBlock.getSubclasses();
        while (it.hasNext()) {
            sortedSet.add(it.next());
        }
        return sortedSet.iterator();
    }
    
    public int getSubclassCount() {
        return classBlock.getSubclassCount();
    }
    
    public Iterator getClassProperties() {
        return classProperties.size() == 0 ? null : classProperties.iterator();
    }
    
    public int getInstanceMethodCount() {
        return instanceMethods.size();
    }
    
    public int getInstanceMethodCount(ClassBlock classBlock) {
        ClassDO cr = renderer.getClassRender(classBlock);
        return cr.getInstanceMethodCount();
    }
    
    public Iterator getInstanceMethods() {
        return instanceMethods.size() == 0 ? null : instanceMethods.iterator();
    }
    
    public Iterator getInstanceMethods(ClassBlock classBlock) {
        ClassDO cr = renderer.getClassRender(classBlock);
        return cr.getInstanceMethods();
    }
    
    public Iterator getInstanceProperties() {
        return instanceProperties.size() == 0 ? null : instanceProperties.iterator();
    }
}
