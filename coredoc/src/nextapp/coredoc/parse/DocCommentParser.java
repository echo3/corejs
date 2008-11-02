package nextapp.coredoc.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nextapp.coredoc.model.DocComment;

public class DocCommentParser {

    private static final Pattern commentOpenStripper = Pattern.compile("^(\\/\\*\\*)", Pattern.MULTILINE);
    private static final Pattern commentCloseStripper = Pattern.compile("\\*\\/$", Pattern.MULTILINE);
    private static final Pattern docCommentIndent = Pattern.compile("^\\s*\\* ?", Pattern.MULTILINE);
    private static final Pattern tagSplitter = Pattern.compile("(^|[\\r\\f\\n])\\s*(?=@)", Pattern.MULTILINE);
    private static final Pattern tagParser = Pattern.compile("(^@\\w*)", Pattern.MULTILINE);
    
    /**
     * Creates a <code>DocComment</code> object from raw text.
     * 
     * @param commentText the comment text (may include the leading slash-star-star and trailing star-slash)
     * @return a <code>DocComment</code> representation of the comment
     */
    public static DocComment parse(String commentText) {
        DocComment docComment = new DocComment();

        String strippedText = commentText;
        strippedText = commentOpenStripper.matcher(strippedText).replaceFirst("");
        strippedText = commentCloseStripper.matcher(strippedText).replaceFirst("");
        strippedText = docCommentIndent.matcher(strippedText).replaceAll("");
        
        String[] items = tagSplitter.split(strippedText);
        
        for (int i = 0; i < items.length; ++i) {
            Matcher tagMatcher = tagParser.matcher(items[i]);
            if (tagMatcher.find()) {
                String tagType = tagMatcher.group();
                String tagContent = items[i].substring(tagMatcher.end()).trim();
                docComment.addTag(new DocComment.Tag(tagType, tagContent));
            } else {
                String descriptionText = items[i].trim();
                if (descriptionText.length() > 0) {
                    docComment.setDescription(descriptionText);
                }
            }
        }
        
        return docComment;
    }
}
