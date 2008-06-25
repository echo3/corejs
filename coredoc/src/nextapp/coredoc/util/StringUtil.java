package nextapp.coredoc.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class StringUtil {
    
    public static String getTextFile(String filePath) 
    throws IOException {
        StringBuffer out = new StringBuffer();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
            
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
            out.append("\n");
        }
    
        return out.toString();
    }
    
    public static String join(Collection c, String delimiter) {
        StringBuffer out = new StringBuffer();
        Iterator it = c.iterator();
        while (it.hasNext()) {
            out.append(it.next());
            if (it.hasNext()) {
                out.append(delimiter);
            }
        }
        return out.toString();
    }
    
    public static String repeat(char ch, int count, boolean breakLines) {
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < count; ++i) {
            out.append(ch);
        }
        return out.toString();
    }
}
