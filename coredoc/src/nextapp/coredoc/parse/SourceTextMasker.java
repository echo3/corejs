package nextapp.coredoc.parse;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;

import nextapp.coredoc.util.Patterns;
import nextapp.coredoc.util.StringUtil;

public class SourceTextMasker {
    
    public static String getSafeSource(String source) {
        return new SourceTextMasker(source).safeSource;
    }

    private abstract class Segment 
    implements Comparable {
        
        private int start;
        private String text;
        
        Segment(String text, int start) {
            super();
            this.text = text;
            this.start = start;
        }
        
        public int compareTo(Object o) {
            Segment that = (Segment) o;
            return this.start - that.start;
        }
        
        public String getText() {
            return text;
        }
        
        public abstract String mask();

        public String toString() {
            return start + ":" + text; 
        }
    }

    private class BlockCommentSegment extends Segment {
        
        public BlockCommentSegment(String text, int start) {
            super(text, start);
        }

        public String mask() {
            return StringUtil.repeat(' ', getText().length(), true);
        }
    }

    private class DocCommentSegment extends Segment {
        
        public DocCommentSegment(String text, int start) {
            super(text, start);
        }

        public String mask() {
            return StringUtil.repeat(' ', getText().length(), true);
        }
    }

    private class LineCommentSegment extends Segment {
        
        public LineCommentSegment(String text, int start) {
            super(text, start);
        }

        public String mask() {
            return StringUtil.repeat(' ', getText().length(), true);
        }
    }

    private class StringSegment extends Segment {
        
        public StringSegment(String text, int start) {
            super(text, start);
        }
        
        public String mask() {
            return "\"" + StringUtil.repeat('-', getText().length() - 2, false) + "\"";
        }
    }
    
    private String safeSource;
    
    private SourceTextMasker(String source) {
        super();
        this.safeSource = source;
        parse();
    }
    
    private static String maskFreeFormSegments(Set segments, String source) {
        int i = 0;
        StringBuffer out = new StringBuffer();
        Iterator it = segments.iterator();
        while (it.hasNext()) {
            Segment segment = (Segment) it.next();
            String segmentText = segment.mask();
            if (i > segment.start) {
                // Segment is nested inside another segment.
                continue;
            }
            out.append(source.substring(i, segment.start));
            out.append(segmentText);
            i = segment.start + segment.text.length();
        }
        out.append(source.substring(i));
        return out.toString();
    }

    private void parse() {
        Set segments = new TreeSet();
        
        Matcher stringMatcher = Patterns.string.matcher(safeSource);
        while (stringMatcher.find()) {
            segments.add(new StringSegment(stringMatcher.group(), stringMatcher.start()));
        }
        
        Matcher lineCommentMatcher = Patterns.lineComment.matcher(safeSource);
        while (lineCommentMatcher.find()) {
            segments.add(new LineCommentSegment(lineCommentMatcher.group(), lineCommentMatcher.start()));
        }

        Matcher blockCommentMatcher = Patterns.blockComment.matcher(safeSource);
        while (blockCommentMatcher.find()) {
            segments.add(new BlockCommentSegment(blockCommentMatcher.group(), blockCommentMatcher.start()));
        }
        
        Matcher docCommentMatcher = Patterns.docComment.matcher(safeSource);
        while (docCommentMatcher.find()) {
            segments.add(new DocCommentSegment(docCommentMatcher.group(), docCommentMatcher.start()));
        }
        
        safeSource = maskFreeFormSegments(segments, safeSource);
    }
}
