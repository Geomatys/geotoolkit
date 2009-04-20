
package org.geotoolkit.wms;

import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public interface Request {

    URL getURL() throws MalformedURLException;
    
}
