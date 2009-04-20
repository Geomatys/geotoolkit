

package org.geotoolkit.wms;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.internal.jaxb.backend.AbstractWMSCapabilities;
import org.geotoolkit.wms.v111.GetMap111;
import org.geotoolkit.wms.v130.GetMap130;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class WebMapServer {

    public static enum Version{
        v111("1.1.1"),
        v130("1.3.0");

        private final String code;

        Version(String code){
            this.code = code;
        }

        public String getCode() {
            return code;
        }

    };

    private final Version version;
    private final URL serverURL;
    private AbstractWMSCapabilities capabilities;

    public WebMapServer(URL serverURL, String version) {
        final Version vers;
        if(version.equals("1.1.1")){
            vers = Version.v111;
        }else if(version.equals("1.3.0")){
            vers = Version.v130;
        }else{
            throw new IllegalArgumentException("Unknowned version : "+ version);
        }

        this.version = vers;
        this.serverURL = serverURL;
    }

    public WebMapServer(URL serverURL, Version version) {
        this.version = version;
        this.serverURL = serverURL;
    }

    public AbstractWMSCapabilities getCapabilities() {
        if(capabilities == null){
            StringBuilder sb = new StringBuilder();
            String request = serverURL.toString();
            sb.append(request);
            if(!request.endsWith("?")){
                sb.append("?");
            }

            sb.append("REQUEST=GetCapabilities");
            sb.append("&SERVICE=WMS");
            sb.append("&VERSION=").append(version.code);
            System.out.println(sb.toString());
            try {
                URL url = new URL(sb.toString());
                capabilities = XMLUtilities.unmarshall(url, version);
            } catch (Exception ex) {
                Logger.getLogger(WebMapServer.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return capabilities;
    }

    public Version getVersion() {
        return version;
    }

    public GetMapRequest createGetMap(){
        switch(version){
            case v111 : return new GetMap111(serverURL.toString());
            case v130 : return new GetMap130(serverURL.toString());
            default: throw new IllegalArgumentException("Version was not defined");
        }
    }

}
