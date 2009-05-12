
package org.geotoolkit.wms;

import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author Olivier Terral (Geomatys)
 */
public interface GetCapabilitiesRequest {

    URL getURL() throws MalformedURLException;
    
}
