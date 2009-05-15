
package org.geotoolkit.wms.v111;

import org.geotoolkit.wms.AbstractGetCapabilities;

/**
 * @author Olivier Terral (Geomatys)
 */
public class GetCapabilities111 extends AbstractGetCapabilities {

    public GetCapabilities111(String serverURL){
        super(serverURL, "1.1.1");
    }

}
