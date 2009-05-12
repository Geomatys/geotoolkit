
package org.geotoolkit.wms;

import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author Olivier Terral (Geomatys)
 */
public abstract class AbstractGetCapabilities extends AbstractRequest implements GetCapabilitiesRequest{

    protected final String version;

    protected AbstractGetCapabilities(String serverURL,String version){
        super(serverURL);
        this.version = version;
    }

    @Override
    public URL getURL() throws MalformedURLException {
        requestParameters.put("SERVICE",    "WMS");
        requestParameters.put("REQUEST",    "GetCapabilities");
        requestParameters.put("VERSION",    version);        
        return super.getURL();
    }

}
