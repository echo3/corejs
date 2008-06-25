package nextapp.coredoc;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.velocity.app.Velocity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import nextapp.coredoc.htmlrender.CustomTagRender;
import nextapp.coredoc.htmlrender.Generator;
import nextapp.coredoc.model.Instance;
import nextapp.coredoc.model.Module;
import nextapp.coredoc.model.Node;
import nextapp.coredoc.parse.ModuleParser;
import nextapp.coredoc.util.DomUtil;
import nextapp.coredoc.util.StringUtil;

public class CoreDoc {
    
    private static Element docElement;

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

        Instance instance = new Instance();
        
        InputStream in = new FileInputStream(docXmlFile);
        Document dom = DomUtil.load(in);
        docElement = dom.getDocumentElement();
        
        String outputDir = DomUtil.getPropertyElementValue(docElement, "output-dir");
        if (outputDir == null || outputDir.trim().length() == 0) {
            outputDir = new File("CoreDocOutput").getAbsolutePath();
        }
        
        Element modulesElements[] = DomUtil.getChildElementsByTagName(docElement, "modules");
        for (int i = 0; i < modulesElements.length; ++i) {
            File baseFile;
            if (modulesElements[i].hasAttribute("base") ) {
                //FIXME File.isAbsolute() always true, cannot find method in Java API to determine if path is relative or absolute.
                // Test if relative file exists, if not, try absolute path.
                baseFile = new File(defaultBaseFile, modulesElements[i].getAttribute("base"));
                if (!baseFile.exists()) {
                    baseFile = new File(modulesElements[i].getAttribute("base"));
                }
            } else {
                baseFile = defaultBaseFile;
            }
            Element moduleElements[] = DomUtil.getChildElementsByTagName(modulesElements[i], "module");
            for (int j = 0; j < moduleElements.length; ++j) {
                String moduleName = DomUtil.getElementText(moduleElements[j]);
                String moduleSource = StringUtil.getTextFile(new File(baseFile, moduleName).getAbsolutePath());
                Module module = ModuleParser.parse(instance, moduleSource);
                instance.addModule(module);
                System.err.println("Parsing: " + moduleName);
            }
        }
        
        instance.process();

        Generator generator = new Generator(instance);
        
        generator.setTitle(DomUtil.getPropertyElementValue(docElement, "title"));
        
        Element customTagElements[] = DomUtil.getChildElementsByTagName(docElement, "custom-tag");
        for (int i = 0; i < customTagElements.length; ++i) {
            CustomTagRender customTag = new CustomTagRender(DomUtil.getPropertyElementValue(customTagElements[i], "name"),
                    DomUtil.getPropertyElementValue(customTagElements[i], "template"));
            customTag.setRequiredType(DomUtil.getPropertyElementValue(customTagElements[i], "required-type"));
            generator.addCustomTag(customTag);
        }

        Element customTypeElements[] = DomUtil.getChildElementsByTagName(docElement, "custom-type");
        for (int i = 0; i < customTypeElements.length; ++i) {
            generator.addCustomType(DomUtil.getPropertyElementValue(customTypeElements[i], "name"),
                    DomUtil.getPropertyElementValue(customTypeElements[i], "display"));
        }
        
        generator.generate(outputDir);
        
        System.err.println("Documentation successfully generated: " + outputDir);
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
