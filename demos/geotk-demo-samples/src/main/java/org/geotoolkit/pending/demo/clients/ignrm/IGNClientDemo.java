
package org.geotoolkit.pending.demo.clients.ignrm;

import java.net.URL;
import javax.swing.JOptionPane;
import org.geotoolkit.gui.swing.render2d.JMap2DFrame;
import org.geotoolkit.ignrm.IGNRMClient;
import org.geotoolkit.ignrm.Token;
import org.geotoolkit.ignrm.TokenClientSecurity;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.pending.demo.Demos;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.security.ClientSecurityStack;
import org.geotoolkit.security.RefererClientSecurity;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.wmsc.WebMapClientCached;
import org.geotoolkit.wmsc.map.WMSCMapLayer;
import org.opengis.util.GenericName;

public class IGNClientDemo {
 
    public static final MutableStyleFactory SF = new DefaultStyleFactory();
    
    public static void main(String[] args) throws Exception {
        Demos.init();
        
        // THIS DEMO REQUIERE ADDITIONAL CRS DEFINITIONS
        // those have been added in the module in path : 
        // src/main/resources/org/geotoolkit/referencing/factory/epsg/epsg.properties
        
        
        
        final String key = JOptionPane.showInputDialog("Enter your IGN contract key "
                + "(this demo is for a free inspire contract, code must be adapted for others) :");
                
        if(key == null || key.isEmpty()){
            return;
        }
        
        
        final MapContext context = createIGNContext(key);
        
        JMap2DFrame.show(context);
        
    }
 
    public static MapContext createIGNContext(final String key) throws Exception{
        final MapContext context = MapBuilder.createContext(CommonCRS.WGS84.normalizedGeographic());

        final ClientSecurity refererInfo = new RefererClientSecurity("http://localhost/");
        
        final IGNRMClient geodrmServer = new IGNRMClient(new URL("http://jeton-api.ign.fr"),refererInfo);
        final Token token = geodrmServer.getToken(key);
        final ClientSecurity tokenInfo = new TokenClientSecurity(token);
        final ClientSecurity tokenAndReferer = ClientSecurityStack.wrap(refererInfo,tokenInfo);
        
        final WebMapClientCached server = new WebMapClientCached(
                new URL("http://wxs.ign.fr/inspire/wmsc?"), tokenAndReferer,true);
        
        for(GenericName name : server.getNames()){
            if(name.tip().toString().contains("ELEVATION.SLOPES") || name.tip().toString().contains("ORTHOIMAGERY.ORTHOPHOTOS")){
                final WMSCMapLayer sloplayer = new WMSCMapLayer(server, name);
                sloplayer.setDescription(SF.description(name.tip().toString(), ""));
                context.layers().add(sloplayer);
            }
        }
        
        return context;
    }
    
    
}
