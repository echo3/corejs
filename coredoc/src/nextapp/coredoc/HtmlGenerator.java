package nextapp.coredoc;

import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import nextapp.coredoc.htmlrender.CustomTagRender;
import nextapp.coredoc.htmlrender.HtmlRenderer;
import nextapp.coredoc.model.Instance;
import nextapp.coredoc.util.DomUtil;

public class HtmlGenerator {

    private Instance instance;
    private Document document;
    private String outputDir;
    
    public HtmlGenerator(Document document, Instance instance) {
        super();
        this.document = document;
        this.instance = instance;

        outputDir = DomUtil.getPropertyElementValue(document.getDocumentElement(), "output-dir");
        if (outputDir == null || outputDir.trim().length() == 0) {
            outputDir = new File("CoreDocOutput").getAbsolutePath();
        }
    }
    
    public void generate() 
    throws Exception {
        HtmlRenderer htmlRenderer = new HtmlRenderer(instance);
        
        htmlRenderer.setTitle(DomUtil.getPropertyElementValue(document.getDocumentElement(), "title"));
        
        Element customTagElements[] = DomUtil.getChildElementsByTagName(document.getDocumentElement(), "custom-tag");
        for (int i = 0; i < customTagElements.length; ++i) {
            CustomTagRender customTag = new CustomTagRender(DomUtil.getPropertyElementValue(customTagElements[i], "name"),
                    DomUtil.getPropertyElementValue(customTagElements[i], "template"));
            customTag.setRequiredType(DomUtil.getPropertyElementValue(customTagElements[i], "required-type"));
            htmlRenderer.addCustomTag(customTag);
        }

        Element customTypeElements[] = DomUtil.getChildElementsByTagName(document.getDocumentElement(), "custom-type");
        for (int i = 0; i < customTypeElements.length; ++i) {
            htmlRenderer.addCustomType(DomUtil.getPropertyElementValue(customTypeElements[i], "name"),
                    DomUtil.getPropertyElementValue(customTypeElements[i], "display"));
        }
        
        htmlRenderer.generate(outputDir);
    }
    
    public String getOutputDir() {
        return outputDir;
    }
}
