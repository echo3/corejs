package nextapp.coredoc.render;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nextapp.coredoc.model.ClassBlock;
import nextapp.coredoc.model.Instance;

public abstract class Renderer {
    
    private Instance instance;
    private File outputDirectory;
    private Map classBlockToClassRender = new HashMap();
    private Map customTagNameToTagRender = new HashMap();
    private Map customTypeToDisplayText = new HashMap();
    
    public Renderer(Instance instance) {
        this.instance = instance;
    }

    public void addCustomTag(CustomTagRender customTag) {
        customTagNameToTagRender.put(customTag.getTagName(), customTag);
        customTag.setRenderer(this);
    }
    
    public void addCustomType(String typeName, String displayText) {
        customTypeToDisplayText.put(typeName, displayText);
    }
    
    public String getCustomType(String typeName) {
        return (String) customTypeToDisplayText.get(typeName);
    }
    
    public ClassDO getClassRender(ClassBlock classBlock) {
        ClassDO classRender = (ClassDO) classBlockToClassRender.get(classBlock);
        if (classRender == null) {
            classRender = new ClassDO(this, classBlock);
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
    
    public void render(String outputPath) 
    throws Exception {
        // Create output directory. 
        outputDirectory = new File(outputPath);
        if (outputDirectory.exists() && (!outputDirectory.isDirectory() || !outputDirectory.canWrite())) {
            throw new IllegalArgumentException("Output path exists and is not writable.");
        }
        if (!outputDirectory.exists()) {
            outputDirectory.mkdir();
        }
    }
}