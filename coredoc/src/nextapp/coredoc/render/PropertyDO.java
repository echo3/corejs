package nextapp.coredoc.render;

import java.util.ArrayList;
import java.util.List;

import nextapp.coredoc.model.Block;
import nextapp.coredoc.model.DocComment;
import nextapp.coredoc.model.Modifiers;
import nextapp.coredoc.util.StringUtil;

public class PropertyDO {
    
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