package nextapp.coredoc.render.xml;

import java.io.File;
import java.io.FileWriter;
import java.util.Set;
import java.util.TreeSet;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import nextapp.coredoc.model.ClassBlock;
import nextapp.coredoc.model.Instance;
import nextapp.coredoc.model.Modifiers;
import nextapp.coredoc.model.Node;
import nextapp.coredoc.render.ClassDO;
import nextapp.coredoc.render.Renderer;

public class XmlRenderer extends Renderer {

    private static final String TEMPLATE_PATH = "nextapp/coredoc/render/xml/template/";

    private ClassBlock[] classes;
    
    public XmlRenderer(Instance instance) {
        super(instance);
        classes = getInstance().getClasses();
    }
    
    public void render(String outputPath) 
    throws Exception {
        super.render(outputPath);
        
        createIndex();

        for (int i = 0; i < classes.length; ++i) {
            ClassDO classDO = getClassRender(classes[i]);
            createClass(classes[i], classDO);
        }
    }
    
    public String getName() {
        return "xml";
    }
    
    private void createClass(ClassBlock classBlock, ClassDO classDO)
    throws Exception {
        File indexHtml = new File(getOutputDirectory(), "Class." + classBlock.getQualifiedName() + ".xml");
        FileWriter fw = new FileWriter(indexHtml);
        
        Template template = Velocity.getTemplate(TEMPLATE_PATH + "Class.xml");        
        VelocityContext context = new VelocityContext();

        context.put("generator", this);
        context.put("qualifiedName", classBlock.getQualifiedName());
        context.put("name", classBlock.getName());
        context.put("cr", classDO);
        context.put("containerName", classBlock.getContainerName());
        context.put("description", classBlock.getDocComment() == null ? null : classBlock.getDocComment().getDescription());;
        if ((classBlock.getModifiers() & Modifiers.ABSTRACT) != 0) {
            context.put("modifiers", "Abstract");
        }

        template.merge(context, fw);
        fw.flush();
        fw.close();
    }

    private void createIndex() 
    throws Exception {
        Node[] namespaces = getInstance().getNamespaces();
        Set namespaceDOs = new TreeSet();
        for (int i = 0; i < namespaces.length; ++i) {
            if (namespaces[i].getName() != null) {
                namespaceDOs.add(namespaces[i].getQualifiedName());
            }
        }
        
        File indexXml = new File(getOutputDirectory(), "Index.xml");
        FileWriter fw = new FileWriter(indexXml);
        
        Template template = Velocity.getTemplate(TEMPLATE_PATH + "Index.xml");        

        VelocityContext context = new VelocityContext();
        context.put("namespaces", namespaceDOs);
        context.put("classes", classes);
        
        template.merge(context, fw);
        fw.flush();
        fw.close();
    }
}
