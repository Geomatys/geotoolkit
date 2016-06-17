
package org.geotoolkit.pending.demo.clients.ignrm;

import java.net.URL;
import org.geotoolkit.storage.coverage.CoverageReference;
import org.geotoolkit.storage.coverage.PyramidalCoverageReference;
import org.geotoolkit.gui.swing.render2d.JMap2DFrame;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.security.BasicAuthenticationSecurity;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.style.DefaultDescription;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.wmts.WMTSClientFactory;
import org.geotoolkit.wmts.WebMapTileClient;
import org.opengis.util.GenericName;
import org.opengis.parameter.ParameterValueGroup;

public class IGN_GEOPORTAIL_WMTS {

    public static void main(String[] args) throws Exception {


        final String key = "yourkey";
        final String login = "yourlogin";
        final String password = "yourpassword";

        final ClientSecurity authentication = new BasicAuthenticationSecurity(login,password);
        final URL url = new URL("https://wxs.ign.fr/"+key+"/wmts?");

        final ParameterValueGroup params = WMTSClientFactory.PARAMETERS.createValue();
        Parameters.getOrCreate(WMTSClientFactory.URL, params).setValue(url);
        Parameters.getOrCreate(WMTSClientFactory.SECURITY, params).setValue(authentication);
        Parameters.getOrCreate(WMTSClientFactory.IMAGE_CACHE, params).setValue(true);
        Parameters.getOrCreate(WMTSClientFactory.NIO_QUERIES, params).setValue(true);

        final WebMapTileClient store = (WebMapTileClient) DataStores.open(params);


        final MapContext context = MapBuilder.createContext();

        for(GenericName n : store.getNames()){
            final CoverageReference cr = store.getCoverageReference(n);
            if(cr instanceof PyramidalCoverageReference){
                PyramidalCoverageReference model = (PyramidalCoverageReference) cr;
                System.out.println(model.getPyramidSet());
            }

            final CoverageMapLayer cml = MapBuilder.createCoverageLayer(cr);
            cml.setDescription(new DefaultDescription(new SimpleInternationalString(n.tip().toString()), new SimpleInternationalString("")));
            context.layers().add(cml);
        }


        JMap2DFrame.show(context, true, null);

    }

}
