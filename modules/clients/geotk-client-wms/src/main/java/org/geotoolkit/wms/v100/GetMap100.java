package org.geotoolkit.wms.v100;

import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.wms.AbstractGetMap;
import org.geotoolkit.wms.WebMapClient;
import org.geotoolkit.wms.xml.WMSVersion;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class GetMap100 extends AbstractGetMap {
    /**
     * Defines the server url and its version.
     *
     * @param serverURL The url of the webservice.
     */
    public GetMap100(final String serverURL, final ClientSecurity security){
        super(serverURL,WMSVersion.v100.getCode(), security);
    }

    public GetMap100(final WebMapClient server, final ClientSecurity security){
        super(server,WMSVersion.v100.getCode(), security);
    }

    @Override
    protected String getCRSParameterName() {
        return "SRS";
    }
}
