
package org.geotoolkit.pending.demo.clients.ignrm;

import java.net.URL;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.CoverageStoreFinder;
import org.geotoolkit.gui.swing.render2d.JMap2DFrame;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.security.BasicAuthenticationSecurity;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.style.DefaultDescription;
import org.geotoolkit.style.RandomStyleBuilder;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.wmts.WMTSClientFactory;
import org.geotoolkit.wmts.WebMapTileClient;
import org.opengis.feature.type.Name;
import org.opengis.parameter.ParameterValueGroup;

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

        final ParameterValueGroup params = WMTSClientFactory.PARAMETERS.createValue();
        Parameters.getOrCreate(WMTSClientFactory.URL, params).setValue(url);
        Parameters.getOrCreate(WMTSClientFactory.SECURITY, params).setValue(authentication);
        Parameters.getOrCreate(WMTSClientFactory.IMAGE_CACHE, params).setValue(true);
        Parameters.getOrCreate(WMTSClientFactory.NIO_QUERIES, params).setValue(true);

        final WebMapTileClient store = (WebMapTileClient) CoverageStoreFinder.open(params);


        final MapContext context = MapBuilder.createContext();

        for(Name n : store.getNames()){
            final CoverageReference cr = store.getCoverageReference(n);
            final CoverageMapLayer cml = MapBuilder.createCoverageLayer(cr);
            cml.setDescription(new DefaultDescription(new SimpleInternationalString(n.getLocalPart()), new SimpleInternationalString("")));
            context.layers().add(cml);
        }


        JMap2DFrame.show(context,true,null);

    }

}
