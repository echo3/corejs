package nextapp.coredoc.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FunctionBlock extends Block {
    
    public class Parameter {
        
        private String name;
        private String type;
        private String description;
        
        private Parameter(String name, String type, String description) {
            super();
            this.name = name;
            this.type = type;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String getDescription() {
            return description;
        }
    }
    
    private Parameter[] parameters;
    private String returnType;
    private String returnDescription;

    public FunctionBlock(Module module, Bounds bounds) {
        super(module, bounds);
    }
    
    public DocComment findDocComment() {
        DocComment docComment = getDocComment();
        if (docComment == null) {
            //FIXME
        }
        return docComment;
    }
    
    public Parameter[] getParameters() {
        return parameters;
    }
    
    public String getReturnType() {
        return returnType;
    }
    
    public String getReturnDescription() {
        return returnDescription;
    }

    public void process() {
    }

    public void setDocComment(DocComment docComment) {
        super.setDocComment(docComment);
        
        Iterator paramIt = docComment.getTags("@param");
        if (paramIt != null) {
            List parameterList = new ArrayList();
            while(paramIt.hasNext()) {
                DocComment.Tag tag = (DocComment.Tag) paramIt.next();
                parameterList.add(new Parameter(DocComment.getParameterName(tag),
                        DocComment.getParameterType(tag), DocComment.getParameterDescription(tag)));

            }
            parameters = new Parameter[parameterList.size()];
            parameterList.toArray(parameters);
        }
        
        Iterator returnIt = docComment.getTags("@return");
        if (returnIt != null) {
            returnDescription = ((DocComment.Tag) returnIt.next()).getText();
        }
        
        Iterator typeIt = docComment.getTags("@type");
        if (typeIt != null) {
            returnType = ((DocComment.Tag) typeIt.next()).getText();
        }
    }
}
 