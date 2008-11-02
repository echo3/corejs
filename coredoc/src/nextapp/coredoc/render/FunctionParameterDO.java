package nextapp.coredoc.render;

import nextapp.coredoc.model.FunctionBlock;

public class FunctionParameterDO {
    
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