package nextapp.coredoc.render;

import java.util.Iterator;

import nextapp.coredoc.model.FunctionBlock;
import nextapp.coredoc.model.FunctionBlock.Parameter;

public class FunctionDO extends PropertyDO {

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