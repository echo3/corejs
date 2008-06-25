package nextapp.coredoc.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Provides functionality for obtaining text and binary resource files.
 */
public class Resource {
    
    private static final int BUFFER_SIZE = 4096;
    
    /**
     * A RuntimeException exception that will be thrown in the event that
     * problems are encountered obtaining a resource.
     */
    public static class ResourceException extends RuntimeException {
    
        /** Serial Version UID. */
        private static final long serialVersionUID = 20070101L;
    
        /**
         * Creates a resource exception.
         *
         * @param description A description of the error.
         */
        private ResourceException(String description) {
            super(description);
        }
    }

    /**
     * Retrieves the specified resource as a <code>String</code>.
     *
     * @param resourceName The name of the resource to be retrieved.
     * @return The specified resource as a <code>String</code>.
     */
    public static String getResourceAsString(String resourceName) {
        return getResource(resourceName).toString();
    }
    
    /**
     * Retrieves the specified resource as an array of <code>byte</code>s.
     *
     * @param resourceName The name of the resource to be retrieved.
     * @return The specified resource as an array of <code>byte<code>s.
     */
    public static byte[] getResourceAsByteArray(String resourceName) {
        return getResource(resourceName).toByteArray();
    }
    
    /**
     * An internal method used to retrieve a resource as a
     * <code>ByteArrayOutputStream</code>.
     *
     * @param resourceName The name of the resource to be retrieved.
     * @return A <code>ByteArrayOutputStream</code> of the content of the
     *         resource.
     */
    private static ByteArrayOutputStream getResource(String resourceName) {
        InputStream in = null;
        byte[] buffer = new byte[BUFFER_SIZE];
        ByteArrayOutputStream out = null;
        int bytesRead = 0;
        
        try {
            in = Resource.class.getClassLoader().getResourceAsStream(resourceName);
            if (in == null) {
                throw new ResourceException("Resource does not exist: \"" + resourceName + "\"");
            }
            out = new ByteArrayOutputStream();
            do {
                bytesRead = in.read(buffer);
                if (bytesRead > 0) {
                    out.write(buffer, 0, bytesRead);
                }
            } while (bytesRead > 0);
        } catch (IOException ex) {
            throw new ResourceException("Cannot get resource: \"" + resourceName + "\": " + ex);
        } finally {
            if (in != null) { try { in.close(); } catch (IOException ex) { } } 
        }
        
        return out;
    }

    /** Non-instantiable class. */
    private Resource() { }
}
