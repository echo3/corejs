package nextapp.coredoc.render.xml;

import java.io.File;
import java.io.FileWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import nextapp.coredoc.model.ClassBlock;
import nextapp.coredoc.model.Instance;
import nextapp.coredoc.render.Renderer;

public class XmlRenderer extends Renderer {

    private File outputDirectory;
    
    public XmlRenderer(Instance instance) {
        super(instance);
    }
    
    public void render(String outputPath) 
    throws Exception {
        super.render(outputPath);
        
        createAllClasses();
    }
    
    public String getName() {
        return "xml";
    }

    private void createAllClasses() 
    throws Exception {
        ClassBlock[] classes = getInstance().getClasses();
        
        File indexHtml = new File(outputDirectory, "classes.html");
        FileWriter fw = new FileWriter(indexHtml);
        
        Template template = Velocity.getTemplate("/nextapp/coredoc/htmlrender/template/Classes.html");        

        VelocityContext context = new VelocityContext();
        context.put("classes", classes);
        
        template.merge(context, fw);
        fw.flush();
        fw.close();
        
        for (int i = 0; i < classes.length; ++i) {
            //ClassDO cr = getClassRender(classes[i]);
//            cr.render();
        }
    }
}
