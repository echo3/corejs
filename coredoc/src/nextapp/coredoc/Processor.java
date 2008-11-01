package nextapp.coredoc;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import nextapp.coredoc.model.Instance;
import nextapp.coredoc.model.Module;
import nextapp.coredoc.parse.ModuleParser;
import nextapp.coredoc.util.DomUtil;
import nextapp.coredoc.util.StringUtil;

/**
 * Processes a documentation generation request, invoking the parser to parse input and generate a model instance.
 */
public class Processor {

    private static final Pattern PROPERTY_PATTERN = Pattern.compile("\\$\\{([0-9a-zA-Z\\.]+)\\}");

    private static String parseFileValue(String value) {
         StringBuffer out = new StringBuffer();
         int lastIndex = 0;
         Matcher variableMatcher = PROPERTY_PATTERN.matcher(value);
         while (variableMatcher.find()) {
             out.append(value.substring(lastIndex, variableMatcher.start()));
             lastIndex = variableMatcher.end();
             String propertyName = variableMatcher.group(1);
             String propertyValue = System.getProperty(propertyName);
             if (propertyValue == null) {
                 throw new IllegalArgumentException("No property value provided for required property: \"" + propertyName + "\".");
             }
             out.append(propertyValue);
         }
         out.append(value.substring(lastIndex));
         return out.toString();
    }
    
    private Document document;
    private File defaultBaseFile;

    public Processor(Document document, File defaultBaseFile) {
        super();
        this.document = document;
        this.defaultBaseFile = defaultBaseFile;
    }
    
    public Instance process()
    throws IOException {
        Instance instance = new Instance();
        
        Element modulesElements[] = DomUtil.getChildElementsByTagName(document.getDocumentElement(), "modules");
        for (int i = 0; i < modulesElements.length; ++i) {
            File baseFile;
            if (modulesElements[i].hasAttribute("base") ) {
                //FIXME File.isAbsolute() always true, cannot find method in Java API to determine if path is relative or absolute.
                // Test if relative file exists, if not, try absolute path.
                baseFile = new File(defaultBaseFile, parseFileValue(modulesElements[i].getAttribute("base")));
                if (!baseFile.exists()) {
                    baseFile = new File(parseFileValue(modulesElements[i].getAttribute("base")));
                }
            } else {
                baseFile = defaultBaseFile;
            }
            Element moduleElements[] = DomUtil.getChildElementsByTagName(modulesElements[i], "module");
            for (int j = 0; j < moduleElements.length; ++j) {
                String moduleName = parseFileValue(DomUtil.getElementText(moduleElements[j]));
                String moduleSource = StringUtil.getTextFile(new File(baseFile, moduleName).getAbsolutePath());
                Module module = ModuleParser.parse(instance, moduleSource);
                instance.addModule(module);
                System.err.println("Parsing: " + moduleName);
            }
        }
        
        instance.process();
        
        return instance;
    }
}
