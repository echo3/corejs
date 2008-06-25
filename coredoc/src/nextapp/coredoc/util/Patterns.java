package nextapp.coredoc.util;

import java.util.regex.Pattern;

public class Patterns {

    private static final String IDENTIFIER = "[A-Za-z\\$_][A-Za-z0-9\\$_]*";
    private static final String FQNAME = IDENTIFIER + "(?:\\s*\\.\\s*" + IDENTIFIER + ")*";
    private static final String PARAMETER_LIST = "(" + IDENTIFIER + "(\\s*,\\s*" + IDENTIFIER + ")*)?";
    
    public static final Pattern lineComment = Pattern.compile("\\/\\/.*$", Pattern.MULTILINE);
    public static final Pattern blockComment = Pattern.compile("\\/\\*[^*][\\S\\s]*\\*\\/", Pattern.MULTILINE);
    public static final Pattern docComment = Pattern.compile("\\/\\*\\*([^*][\\S\\s]*?)\\*\\/", Pattern.MULTILINE);
    public static final Pattern string = Pattern.compile("([\"\']).*?(?<!\\\\)\\1", Pattern.MULTILINE);
    public static final Pattern braces = Pattern.compile("\\{|\\}");
    public static final Pattern coreExtend = Pattern.compile("(" + FQNAME + ")\\s*[:=]\\s*Core\\s*\\.\\s*extend\\s*\\("
            + "(?:(" + FQNAME + ")\\s*,)?\\s*\\{", Pattern.MULTILINE);
    public static final Pattern objectLiteralAssignment = Pattern.compile("(" + FQNAME + ")\\s*[:=]\\s*\\{", Pattern.MULTILINE);
    public static final Pattern functionAssignment = Pattern.compile("(" + FQNAME + ")\\s*[:=]\\s*" +
            "function\\s*\\(\\s*" + PARAMETER_LIST + "\\s*\\)\\s*\\{", Pattern.MULTILINE);
    public static final Pattern functionDeclaration = Pattern.compile("function\\s*(" + FQNAME + ")\\s*" +
            "\\(\\s*" + PARAMETER_LIST + "\\s*\\)\\s*\\{", Pattern.MULTILINE);
    public static final Pattern fieldAssignment = Pattern.compile("(" + FQNAME + ")\\s*[:=]\\s*", Pattern.MULTILINE);
    
    public static final Pattern fqNameTokenizer = Pattern.compile("(" + IDENTIFIER + ")" + "(?:\\s*\\.\\s*(" + IDENTIFIER + "))*");

    public static final Pattern docCommentParameterTypeParser = Pattern.compile(
            "^\\{\\s*(#?" + FQNAME + ")\\s*\\}", Pattern.MULTILINE);
    public static final Pattern docCommentParameterNameParser = Pattern.compile(
            "^(?:\\{\\s*#?" + FQNAME + "\\s*\\})?\\s*(\\w*)", Pattern.MULTILINE);
    public static final Pattern docCommentParameterDescriptionParser = Pattern.compile(
            "^(?:(?:\\{\\s*#?" + FQNAME + "\\s*\\})?\\s*\\w*)?\\s*([\\S\\s]*)", Pattern.MULTILINE);

    public static final Pattern firstSentence = Pattern.compile("(^[^\\.\\?\\!]*[\\.\\?\\!])", Pattern.MULTILINE);
}

