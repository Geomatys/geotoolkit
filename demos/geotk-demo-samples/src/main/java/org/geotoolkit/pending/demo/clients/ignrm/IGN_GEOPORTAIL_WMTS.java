
package org.geotoolkit.pending.demo.clients.ignrm;

import java.net.URL;
import org.apache.sis.parameter.Parameters;
import org.geotoolkit.gui.swing.render2d.JMap2DFrame;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.security.BasicAuthenticationSecurity;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.style.DefaultDescription;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.coverage.CoverageResource;
import org.geotoolkit.storage.coverage.PyramidalCoverageResource;
import org.geotoolkit.wmts.WMTSClientFactory;
import org.geotoolkit.wmts.WebMapTileClient;
import org.opengis.util.GenericName;

public class IGN_GEOPORTAIL_WMTS {

    public static void main(String[] args) throws Exception {


        final String key = "yourkey";
        final String login = "yourlogin";
        final String password = "yourpassword";

        final ClientSecurity authentication = new BasicAuthenticationSecurity(login,password);
        final URL url = new URL("https://wxs.ign.fr/"+key+"/wmts?");

        final Parameters params = Parameters.castOrWrap(WMTSClientFactory.PARAMETERS.createValue());
        params.getOrCreate(WMTSClientFactory.URL).setValue(url);
        params.getOrCreate(WMTSClientFactory.SECURITY).setValue(authentication);
        params.getOrCreate(WMTSClientFactory.IMAGE_CACHE).setValue(true);
        params.getOrCreate(WMTSClientFactory.NIO_QUERIES).setValue(true);

        final WebMapTileClient store = (WebMapTileClient) DataStores.open(params);


        final MapContext context = MapBuilder.createContext();

        for(GenericName n : store.getNames()){
            final CoverageResource cr = store.findResource(n);
            if(cr instanceof PyramidalCoverageResource){
                PyramidalCoverageResource model = (PyramidalCoverageResource) cr;
                System.out.println(model.getPyramidSet());
            }

            final CoverageMapLayer cml = MapBuilder.createCoverageLayer(cr);
            cml.setDescription(new DefaultDescription(new SimpleInternationalString(n.tip().toString()), new SimpleInternationalString("")));
            context.layers().add(cml);
        }


        JMap2DFrame.show(context, true, null);

    }

}
