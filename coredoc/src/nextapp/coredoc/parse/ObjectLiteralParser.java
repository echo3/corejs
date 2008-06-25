package nextapp.coredoc.parse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//FIXME currently unused object.

public class ObjectLiteralParser {
    
    private static final Pattern OPENERS = Pattern.compile("[\\{\\[\\(]"); 
    private static final Pattern CLOSERS = Pattern.compile("[\\}\\]\\)]");
    private List tokenList;
    
    public class Property {
        
        public int startIndex;
        public int endIndex;
        
        public Property(int startIndex, int endIndex) {
            super();
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }
    }
        
    public ObjectLiteralParser(String code) {
        StringTokenizer st = new StringTokenizer(code, ",");
        int cursorIndex = 0;

        tokenList = new ArrayList();

        while (st.hasMoreTokens()) {
            int openCount = 0;
            int closeCount = 0;
            
            StringBuffer out = new StringBuffer();
            
            do {
                String token = st.nextToken();
                cursorIndex += token.length();

                Matcher openMatch = OPENERS.matcher(token);
                while (openMatch.find()) {
                    ++openCount;
                }
                
                Matcher closeMatch = CLOSERS.matcher(token);
                while (closeMatch.find()) {
                    ++closeCount;
                }
                
                out.append(token);
                if (openCount > closeCount) {
                    out.append(",");
                }
                cursorIndex++;
            } while (st.hasMoreTokens() && openCount > closeCount);

            if (closeCount != openCount) {
                throw new IllegalArgumentException("Invalid code: missing brace/bracket/parenthesis, open count = " + openCount
                        + ", close count = " + closeCount);
            }
    
            tokenList.add(out.toString());
        }
        
        int count = tokenList.size() - 1;
        Iterator it = tokenList.iterator();
        while (it.hasNext()) {
            count += ((String) it.next()).length();
        }
        System.err.println((count == code.length()) + ":"  + count + "/" + code.length());
    }
}
