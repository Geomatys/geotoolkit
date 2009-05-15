
package org.geotoolkit.wms.v130;

import org.geotoolkit.wms.AbstractGetCapabilities;

/**
 * @author Olivier Terral (Geomatys)
 */
public class GetCapabilities130 extends AbstractGetCapabilities {

    public GetCapabilities130(String serverURL){
        super(serverURL, "1.3.0");
    }

}
