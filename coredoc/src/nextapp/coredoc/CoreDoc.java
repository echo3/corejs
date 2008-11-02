package nextapp.coredoc;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.velocity.app.Velocity;
import org.w3c.dom.Document;

import nextapp.coredoc.model.Instance;
import nextapp.coredoc.model.Node;
import nextapp.coredoc.util.DomUtil;
import nextapp.coredoc.util.StringUtil;

import nextapp.coredoc.render.html.HtmlRenderer;


public class CoreDoc {
    
    public static void main(String[] args) 
    throws Exception {
        if (args.length != 1) {
            System.err.println("Invalid arguments: specify location of doc.xml file.");
            System.exit(1);
        }
        
        File docXmlFile = new File(args[0]);
        if (!docXmlFile.exists()) {
            System.err.println("doc.xml file \"" + args[0] + "\" cannot be found.");
            System.exit(1);
        }
        
        File defaultBaseFile = docXmlFile.getParentFile();
        
        System.err.println();
        // Init velocity.
        Properties p = new Properties();
        p.setProperty("resource.loader", "class,file");
        p.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        p.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
        p.setProperty("file.resource.loader.path", defaultBaseFile.getAbsolutePath());
        Velocity.init(p);

        InputStream in = new FileInputStream(docXmlFile);
        Document document = DomUtil.load(in);
        
        Processor processor = new Processor(document, defaultBaseFile);
        Instance instance = processor.process();
        
        Generator generator = new Generator(document);
        HtmlRenderer htmlRenderer = new HtmlRenderer(instance);    
    
        generator.generate(htmlRenderer);
        
        System.err.println("Documentation successfully generated: " + generator.getOutputDir());
    }

    public static void print(Node block, int indent, StringBuffer out) {
        out.append(StringUtil.repeat(' ', indent, false));
        out.append(block);
        out.append("\n");
        int childCount = block.getStructureChildCount();
        for (int i = 0; i < childCount; ++i) {
            print(block.getStructureChild(i), indent + 1, out);
        }
    }
}
