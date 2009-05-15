

package org.geotoolkit.wms;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class AbstractRequest implements Request{

    protected final String serverURL;
    protected final Map<String,String> requestParameters = new HashMap<String,String>();

    protected AbstractRequest(String serverURL){

        if(serverURL.contains("?")){
            this.serverURL = serverURL;
        }else{
            this.serverURL = serverURL + "?";
        }

    }

    @Override
    public URL getURL() throws MalformedURLException {
        final StringBuilder sb = new StringBuilder();
        final List<String> keys = new ArrayList<String>(requestParameters.keySet());
        
        sb.append(serverURL);
        
        if(!requestParameters.isEmpty()){

            if(!(serverURL.endsWith("?") || serverURL.endsWith("&"))){
                sb.append("&");
            }

            String key = keys.get(0);
            sb.append(key).append('=').append(requestParameters.get(key));
            
            for(int i=1,n=keys.size();i<n;i++){
                key = keys.get(i);
                sb.append('&').append(key).append('=').append(requestParameters.get(key));
            }
        }

        return new URL(sb.toString());
    }

}
