package nextapp.coredoc.render.html;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
import nextapp.coredoc.util.Resource;

public class HtmlRenderer 
extends Renderer {
    
    private static final String TEMPLATE_PATH = "nextapp/coredoc/render/html/template/";
    private static final String RESOURCE_PATH = "nextapp/coredoc/render/html/resource/";
    
    private Map typeToUrlMap = new HashMap();
    
    public HtmlRenderer(Instance instance) {
        super(instance);
    }
    
    public void render(String outputPath) 
    throws Exception {
        super.render(outputPath);
        
        ClassBlock[] classes = getInstance().getClasses();
        for (int i = 0; i < classes.length; ++i) {
            String qualifiedName = classes[i].getQualifiedName();
            typeToUrlMap.put(qualifiedName, "Class." + qualifiedName + ".html");
        }
        
        createCss();
        createFrameSet();
    }
    
    private void createClass(ClassBlock classBlock, ClassDO classDO)
    throws Exception {
        File indexHtml = new File(getOutputDirectory(), "Class." + classBlock.getQualifiedName() + ".html");
        FileWriter fw = new FileWriter(indexHtml);
        
        Template template = Velocity.getTemplate(TEMPLATE_PATH + "Class.html");        
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
    
    private void createAllClasses() 
    throws Exception {
        ClassBlock[] classes = getInstance().getClasses();
        NameUrlDO[] nameUrlDOs = new NameUrlDO[classes.length];
        for (int i = 0; i < nameUrlDOs.length; ++i) {
            nameUrlDOs[i] = new NameUrlDO(classes[i].getQualifiedName(), 
                    "Class." + classes[i].getQualifiedName() + ".html");
        }

        File indexHtml = new File(getOutputDirectory(), "classes.html");
        FileWriter fw = new FileWriter(indexHtml);
        
        Template template = Velocity.getTemplate(TEMPLATE_PATH + "Classes.html");        

        VelocityContext context = new VelocityContext();
        context.put("classes", nameUrlDOs);
        
        template.merge(context, fw);
        fw.flush();
        fw.close();
        
        for (int i = 0; i < classes.length; ++i) {
            ClassDO classDO = getClassRender(classes[i]);
            createClass(classes[i], classDO);
        }
    }

    private void createCss() 
    throws IOException {
        File defaultCss = new File(getOutputDirectory(), "default.css");
        FileWriter fw = new FileWriter(defaultCss);
        
        fw.write(Resource.getResourceAsString(RESOURCE_PATH + "Default.css"));
        
        fw.flush();
        fw.close();
    }
    
    private void createFrameSet() 
    throws Exception {
        File indexHtml = new File(getOutputDirectory(), "index.html");
        FileWriter fw = new FileWriter(indexHtml);
        
        Template template = Velocity.getTemplate(TEMPLATE_PATH + "Index.html");        
        VelocityContext context = new VelocityContext();
        context.put("framesetTitle", getTitle());
        
        template.merge(context, fw);
        fw.flush();
        fw.close();
        
        createNamespaces();
        createOverview();
        createAllClasses();
    }
    
    private void createNamespaces() 
    throws Exception {
        Node[] namespaces = getInstance().getNamespaces();
        Set namespaceDOs = new TreeSet();
        for (int i = 0; i < namespaces.length; ++i) {
            if (namespaces[i] instanceof ClassBlock) {
                createNamespaceClasses((ClassBlock) namespaces[i]);
            }
            
            if (namespaces[i].getName() == null) {
                namespaceDOs.add(new NameUrlDO("[Global]", "GlobalNamespace.html"));
            } else {
                namespaceDOs.add(new NameUrlDO(namespaces[i].getQualifiedName(), 
                        "Namespace." + namespaces[i].getQualifiedName() + ".html"));
            }
        }

        File indexHtml = new File(getOutputDirectory(), "namespaces.html");
        FileWriter fw = new FileWriter(indexHtml);
        
        VelocityContext context = new VelocityContext();
        context.put("namespaces", namespaceDOs);
        
        Template template = Velocity.getTemplate(TEMPLATE_PATH + "Namespaces.html");        
        template.merge(context, fw);

        fw.flush();
        fw.close();
    }
    
    private void createNamespaceClasses(ClassBlock parent) 
    throws Exception {
        ClassBlock[] classes = parent.getClasses();
        Set classDOs = new TreeSet();
        
        for (int i = 0; i < classes.length; ++i) {
            ClassBlock classBlock = classes[i];
            classDOs.add(new NameUrlDO(classBlock.getQualifiedName(), 
                    "Class." + classBlock.getQualifiedName() + ".html"));
        }

        File indexHtml = new File(getOutputDirectory(), "Namespace." + parent.getQualifiedName() + ".html");
        FileWriter fw = new FileWriter(indexHtml);
        
        Template template = Velocity.getTemplate(TEMPLATE_PATH + "NamespaceClasses.html");        

        VelocityContext context = new VelocityContext();
        context.put("namespace", parent.getQualifiedName());
        context.put("classes", classDOs);
        
        template.merge(context, fw);
        fw.flush();
        fw.close();
        
        for (int i = 0; i < classes.length; ++i) {
            createClass(classes[i], getClassRender(classes[i]));
        }
    }
    
    private void createOverview() 
    throws Exception {
        File indexHtml = new File(getOutputDirectory(), "overview.html");
        FileWriter fw = new FileWriter(indexHtml);
        
        Template template = Velocity.getTemplate(TEMPLATE_PATH + "Overview.html");        
        VelocityContext context = new VelocityContext();
        context.put("title", getTitle());
        
        template.merge(context, fw);
        fw.flush();
        fw.close();
    }
    
    public String getName() {
        return "html";
    }
    
    public String getTypeUrl(String type) {
        return (String) typeToUrlMap.get(type);
    }
}
