package nextapp.coredoc;

import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import nextapp.coredoc.render.CustomTagRender;
import nextapp.coredoc.render.Renderer;
import nextapp.coredoc.util.DomUtil;

public class Generator {

    private Document document;
    private String outputDir;
    
    public Generator(Document document) {
        super();
        this.document = document;

        outputDir = DomUtil.getPropertyElementValue(document.getDocumentElement(), "output-dir");
        if (outputDir == null || outputDir.trim().length() == 0) {
            outputDir = new File("CoreDocOutput").getAbsolutePath();
        }
    }

    public void generate(Renderer renderer) 
    throws Exception {
        
        renderer.setTitle(DomUtil.getPropertyElementValue(document.getDocumentElement(), "title"));
        
        Element customTagElements[] = DomUtil.getChildElementsByTagName(document.getDocumentElement(), "custom-tag");
        for (int i = 0; i < customTagElements.length; ++i) {
            Element[] templateElements = DomUtil.getChildElementsByTagName(customTagElements[i], "template");
            for (int j = 0; j < templateElements.length; ++j) {
                if (renderer.getName().equals(templateElements[j].getAttribute("type"))) {
                    CustomTagRender customTag = new CustomTagRender(DomUtil.getPropertyElementValue(customTagElements[i], "name"),
                            DomUtil.getElementText(templateElements[j]));
                    customTag.setRequiredType(DomUtil.getPropertyElementValue(customTagElements[i], "required-type"));
                    renderer.addCustomTag(customTag);
                }
            }
        }

        Element customTypeElements[] = DomUtil.getChildElementsByTagName(document.getDocumentElement(), "custom-type");
        for (int i = 0; i < customTypeElements.length; ++i) {
            renderer.addCustomType(DomUtil.getPropertyElementValue(customTypeElements[i], "name"),
                    DomUtil.getPropertyElementValue(customTypeElements[i], "display"));
        }
        
        renderer.render(outputDir);
    }
    
    public String getOutputDir() {
        return outputDir;
    }
}
