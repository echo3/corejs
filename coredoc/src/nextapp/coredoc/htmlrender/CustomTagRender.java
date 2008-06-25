package nextapp.coredoc.htmlrender;

import java.io.Writer;
import java.util.Iterator;

import nextapp.coredoc.model.ClassBlock;
import nextapp.coredoc.model.DocComment;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

public class CustomTagRender {

    public class CustomTag {
        
        private DocComment.Tag tag;
        
        public CustomTag(DocComment.Tag tag) {
            this.tag = tag; 
        }
        
        public String getName() {
            return DocComment.getParameterName(tag);
        }
        
        public String getDescription() {
            return DocComment.getParameterDescription(tag);
        }
        
        public boolean isCustomType() {
            String type = DocComment.getParameterType(tag);
            return generator.getCustomType(type) != null;
        }
        
        public String getType() {
            String type = DocComment.getParameterType(tag);
            String customType = generator.getCustomType(type);
            if (customType != null) {
                return customType;
            }
            return type;
        }
    }

    private String requiredType;
    private String templateFile;
    private String tagName;
    private Generator generator;
    
    public CustomTagRender(String tagName, String templateFile) {
        super();
        this.tagName = tagName;
        this.templateFile = templateFile;
    }
    
    public String getRequiredType() {
        return requiredType;
    }
    
    public String getTagName() {
        return tagName;
    }
    
    public Iterator getPropertyNames(ClassBlock classBlock) {
        DocComment docComment = classBlock.getDocComment();
        if (docComment == null) {
            return null;
        }
        final Iterator tagIt = docComment.getTags("@" + tagName);
        if (tagIt == null) {
            return null;
        }
        return new Iterator(){
        
            public void remove() {
                throw new UnsupportedOperationException();
            }
        
            public Object next() {
                DocComment.Tag tag = (DocComment.Tag) tagIt.next();
                return new CustomTag(tag);
            }
        
            public boolean hasNext() {
                return tagIt.hasNext();
            }
        };
    }
    
    public String getPropertyDescription(String propertyName) {
        return "";
    }
        
    public void setGenerator(Generator generator) {
        this.generator = generator;
    }
    
    public void render(ClassBlock classBlock, Writer w) 
    throws Exception {
        Template template = Velocity.getTemplate(templateFile);    
        VelocityContext context = new VelocityContext();
        context.put("tagRender", this);
        context.put("classBlock", classBlock);
        generator.getInstance();
        template.merge(context, w);
    }
    
    public void setRequiredType(String requiredType) {
        this.requiredType = requiredType;
    }
}
