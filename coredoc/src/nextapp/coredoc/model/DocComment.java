package nextapp.coredoc.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import nextapp.coredoc.util.Patterns;

/**
 * A representation of a "Doc Comment" which may contain tags to describe
 * parameters, types, and return values.
 */
public class DocComment {
    
    public static String getParameterType(Tag tag) {
        Matcher matcher = Patterns.docCommentParameterTypeParser.matcher(tag.text);
        return matcher.find() ? matcher.group(1) : null;
    }
    
    public static String getParameterName(Tag tag) {
        Matcher matcher = Patterns.docCommentParameterNameParser.matcher(tag.text);
        return matcher.find() ? matcher.group(1) : null;
    }
    
    public static String getParameterDescription(Tag tag) {
        Matcher matcher = Patterns.docCommentParameterDescriptionParser.matcher(tag.text);
        return matcher.find() ? matcher.group(1) : null;
    }
    
    public static class Tag {
        
        private String type;
        private String text;
        
        public Tag(String type, String text) {
            this.type = type;
            this.text = text;
        }
        
        public String getType() {
            return type;
        }
        
        public String getText() {
            return text;
        }
    }
    
    private String description;
    private Map tagNameToTagList = new HashMap();
    
    public DocComment() {
        super();
    }
    
    public void addTag(Tag tag) {
        List list = (List) tagNameToTagList.get(tag.getType());
        if (list == null) {
            list = new ArrayList();
            tagNameToTagList.put(tag.getType(), list);
        }
        list.add(tag);
    }
    
    /**
     * @param type the tag type, e.g. "@param"
     */
    public Iterator getTags(String type) {
        return tagNameToTagList.containsKey(type) ? ((List) tagNameToTagList.get(type)).iterator() : null;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getShortDescription() {
        if (description == null) {
            return null;
        }
        Matcher matcher = Patterns.firstSentence.matcher(description);
        return matcher.find() ? matcher.group(1) : description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String toString() {
        return description;
    }
}
