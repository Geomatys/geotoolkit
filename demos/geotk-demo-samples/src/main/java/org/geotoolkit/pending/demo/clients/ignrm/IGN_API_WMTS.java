
package org.geotoolkit.pending.demo.clients.ignrm;

import java.net.URL;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.Resource;
import org.geotoolkit.gui.swing.render2d.JMap2DFrame;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.security.BasicAuthenticationSecurity;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.style.DefaultDescription;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.wmts.WMTSClientFactory;
import org.geotoolkit.wmts.WebMapTileClient;
import org.opengis.util.GenericName;
import org.geotoolkit.storage.coverage.GridCoverageResource;

/**
 * This demo connects to the IGN test serveur provided for developement purpose only.
 * an account can be granted here : http://api.ign.fr
 */
public class IGN_API_WMTS {

    public static void main(String[] args) throws Exception {

        final String key = "yourkey";
        final String login = "yourlogin";
        final String password = "yourpassword";

        final ClientSecurity authentication = new BasicAuthenticationSecurity(login, password);
        final URL url = new URL("https://gpp3-wxs.ign.fr/"+key+"/wmts?");

        final Parameters params = Parameters.castOrWrap(WMTSClientFactory.PARAMETERS.createValue());
        params.getOrCreate(WMTSClientFactory.URL).setValue(url);
        params.getOrCreate(WMTSClientFactory.SECURITY).setValue(authentication);
        params.getOrCreate(WMTSClientFactory.IMAGE_CACHE).setValue(true);
        params.getOrCreate(WMTSClientFactory.NIO_QUERIES).setValue(true);

        final WebMapTileClient store = (WebMapTileClient) DataStores.open(params);


        final MapContext context = MapBuilder.createContext();

        for(GenericName n : store.getNames()){
            final Resource cr = store.findResource(n.toString());
            final CoverageMapLayer cml = MapBuilder.createCoverageLayer(cr);
            cml.setDescription(new DefaultDescription(new SimpleInternationalString(n.tip().toString()), new SimpleInternationalString("")));
            context.layers().add(cml);
        }


        JMap2DFrame.show(context,true,null);

    }

}
