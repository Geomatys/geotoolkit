
package org.geotoolkit.pending.demo.clients.ignrm;

import java.net.URL;
import org.apache.sis.parameter.Parameters;
import org.geotoolkit.storage.coverage.CoverageStore;
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
import org.geotoolkit.wms.WMSClientFactory;
import org.geotoolkit.wmts.WMTSClientFactory;
import org.opengis.util.GenericName;

public class IGN_INSPIRE_WMS {

    public static void main(String[] args) throws Exception {

        final String key = "yourkey";
        final String login = "yourlogin";
        final String password = "yourpassword";

        final ClientSecurity authentication = new BasicAuthenticationSecurity(login,password);
        final URL url = new URL("https://gpp3-wxs.ign.fr/"+key+"/inspire/r/wms?");

        final Parameters params = Parameters.castOrWrap(WMSClientFactory.PARAMETERS.createValue());
        params.getOrCreate(WMTSClientFactory.URL).setValue(url);
        params.getOrCreate(WMTSClientFactory.SECURITY).setValue(authentication);

        final CoverageStore store = (CoverageStore) DataStores.open(params);

        final MapContext context = MapBuilder.createContext();

        for(GenericName n : store.getNames()){
            final CoverageResource cr = store.findResource(n);

            final CoverageMapLayer cml = MapBuilder.createCoverageLayer(cr);
            cml.setDescription(new DefaultDescription(new SimpleInternationalString(n.tip().toString()), new SimpleInternationalString("")));
            context.layers().add(cml);
        }


        JMap2DFrame.show(context,true,null);

    }

}
