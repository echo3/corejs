package nextapp.coredoc.render.html;

public class NameUrlDO 
implements Comparable {
    
    private String name;
    private String url;
    
    public NameUrlDO(String name, String url) {
        super();
        this.name = name;
        this.url = url;
    }
    
    public String getName() {
        return name;
    }
    
    public String getUrl() {
        return url;
    }
    
    public int compareTo(Object that) {
        return name.compareTo(((NameUrlDO) that).name);
    }
}

