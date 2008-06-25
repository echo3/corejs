package nextapp.coredoc.htmlrender;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import nextapp.coredoc.model.ClassBlock;
import nextapp.coredoc.model.Instance;
import nextapp.coredoc.model.Node;
import nextapp.coredoc.util.Resource;

public class Generator {
    
    private Map typeToUrlMap = new HashMap();
    private Instance instance;
    private File outputDirectory;
    private Map classBlockToClassRender = new HashMap();
    private Map customTagNameToTagRender = new HashMap();
    private Map customTypeToDisplayText = new HashMap();
    private String title = "Generated Documentation";
    
    public Generator(Instance instance) {
        this.instance = instance;
    }
    
    public void addCustomTag(CustomTagRender customTag) {
        customTagNameToTagRender.put(customTag.getTagName(), customTag);
        customTag.setGenerator(this);
    }
    
    public void addCustomType(String typeName, String displayText) {
        customTypeToDisplayText.put(typeName, displayText);
    }
    
    public String getCustomType(String typeName) {
        return (String) customTypeToDisplayText.get(typeName);
    }
    
    public void generate(String outputPath) 
    throws Exception {
        ClassBlock[] classes = instance.getClasses();
        for (int i = 0; i < classes.length; ++i) {
            String qualifiedName = classes[i].getQualifiedName();
            typeToUrlMap.put(qualifiedName, "Class." + qualifiedName + ".html");
        }
        
        // Create output directory. 
        outputDirectory = new File(outputPath);
        if (outputDirectory.exists() && (!outputDirectory.isDirectory() || !outputDirectory.canWrite())) {
            throw new IllegalArgumentException("Output path exists and is not writable.");
        }
        if (!outputDirectory.exists()) {
            outputDirectory.mkdir();
        }

        createCss();
        createFrameSet();
    }
    
    private void createAllClasses() 
    throws Exception {
        ClassBlock[] classes = instance.getClasses();
        NameUrlDO[] classDOs = new NameUrlDO[classes.length];
        for (int i = 0; i < classDOs.length; ++i) {
            classDOs[i] = new NameUrlDO(classes[i].getQualifiedName(), 
                    "Class." + classes[i].getQualifiedName() + ".html");
        }

        File indexHtml = new File(outputDirectory, "classes.html");
        FileWriter fw = new FileWriter(indexHtml);
        
        Template template = Velocity.getTemplate("/nextapp/coredoc/htmlrender/template/Classes.html");        

        VelocityContext context = new VelocityContext();
        context.put("classes", classDOs);
        
        template.merge(context, fw);
        fw.flush();
        fw.close();
        
        for (int i = 0; i < classes.length; ++i) {
            ClassRender cr = getClassRender(classes[i]);
            cr.render();
        }
    }

    private void createCss() 
    throws IOException {
        File defaultCss = new File(outputDirectory, "default.css");
        FileWriter fw = new FileWriter(defaultCss);
        
        fw.write(Resource.getResourceAsString("nextapp/coredoc/htmlrender/resource/Default.css"));
        
        fw.flush();
        fw.close();
    }
    
    private void createFrameSet() 
    throws Exception {
        File indexHtml = new File(outputDirectory, "index.html");
        FileWriter fw = new FileWriter(indexHtml);
        
        Template template = Velocity.getTemplate("/nextapp/coredoc/htmlrender/template/Index.html");        
        VelocityContext context = new VelocityContext();
        context.put("framesetTitle", title);
        
        template.merge(context, fw);
        fw.flush();
        fw.close();
        
        createNamespaces();
        createOverview();
        createAllClasses();
    }
    
    private void createNamespaces() 
    throws Exception {
        Node[] namespaces = instance.getNamespaces();
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

        File indexHtml = new File(outputDirectory, "namespaces.html");
        FileWriter fw = new FileWriter(indexHtml);
        
        VelocityContext context = new VelocityContext();
        context.put("namespaces", namespaceDOs);
        
        Template template = Velocity.getTemplate("/nextapp/coredoc/htmlrender/template/Namespaces.html");        
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

        File indexHtml = new File(outputDirectory, "Namespace." + parent.getQualifiedName() + ".html");
        FileWriter fw = new FileWriter(indexHtml);
        
        Template template = Velocity.getTemplate("/nextapp/coredoc/htmlrender/template/NamespaceClasses.html");        

        VelocityContext context = new VelocityContext();
        context.put("namespace", parent.getQualifiedName());
        context.put("classes", classDOs);
        
        template.merge(context, fw);
        fw.flush();
        fw.close();
        
        for (int i = 0; i < classes.length; ++i) {
            getClassRender(classes[i]).render();
        }
    }
    
    private void createOverview() 
    throws Exception {
        File indexHtml = new File(outputDirectory, "overview.html");
        FileWriter fw = new FileWriter(indexHtml);
        
        Template template = Velocity.getTemplate("/nextapp/coredoc/htmlrender/template/Overview.html");        
        VelocityContext context = new VelocityContext();
        context.put("title", title);
        
        template.merge(context, fw);
        fw.flush();
        fw.close();
    }
    
    public ClassRender getClassRender(ClassBlock classBlock) {
        ClassRender classRender = (ClassRender) classBlockToClassRender.get(classBlock);
        if (classRender == null) {
            classRender = new ClassRender(this, classBlock);
            classBlockToClassRender.put(classBlock, classRender);
        }
        return classRender;
    }
    
    public Instance getInstance() {
        return instance;
    }
    
    public File getOutputDirectory() {
        return outputDirectory;
    }
    
    public CustomTagRender getTagRender(String tagName) {
        return (CustomTagRender) customTagNameToTagRender.get(tagName);
    }
   
    public Iterator getTagRenderNames() {
        return Collections.unmodifiableSet(customTagNameToTagRender.keySet()).iterator();
    }
    
    public String getTypeUrl(String type) {
        return (String) typeToUrlMap.get(type);
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
}
