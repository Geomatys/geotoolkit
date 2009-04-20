
package org.geotoolkit.wms;

import java.awt.Dimension;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractGetMap extends AbstractRequest implements GetMapRequest{

    protected final String version;
    protected final HashMap<String,String> dims = new HashMap<String, String>();
    protected String format = "image/png";
    protected String exception = "application/vnd.ogc.se_inimage";
    protected String[] layers = null;
    protected String[] styles = null;
    protected Envelope enveloppe = null;
    protected Dimension dimension = null;
    protected String sld = null;
    protected String sldBody = null;

    protected AbstractGetMap(String serverURL,String version){
        super(serverURL);
        this.version = version;
    }

    public String[] getLayers() {
        return layers;
    }

    public void setLayers(String... layers) {
        this.layers = layers;
    }

    public Envelope getEnvelope() {
        return enveloppe;
    }

    public void setEnvelope(Envelope env) {
        this.enveloppe = env;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dim) {
        this.dimension = dim;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getExceptions() {
        return exception;
    }

    public void setExceptions(String ex) {
        this.exception = ex;
    }

    public String[] getStyles() {
        return styles;
    }

    public void setStyles(String... styles) {
        this.styles = styles;
    }

    public String getSld(){
        return sld;
    }

    public void setSld(String sld){
        this.sld = sld;
    }

    public String getSldBody(){
        return sldBody;
    }

    public void setSldBody(String sldBody){
        this.sldBody = sldBody;
    }

    public Map<String,String> dimensions(){
        return dims;
    }

    @Override
    public URL getURL() throws MalformedURLException {
        if(layers == null || layers.length == 0){
            throw new IllegalArgumentException("Layers are not defined");
        }
        if(dimension == null){
            throw new IllegalArgumentException("Dimension is not defined");
        }

        requestParameters.put("SERVICE",    "WMS");
        requestParameters.put("REQUEST",    "GetMap");
        requestParameters.put("VERSION",    version);
        requestParameters.put("EXCEPTIONS", exception);
        requestParameters.put("FORMAT",     format);
        requestParameters.put("WIDTH",      String.valueOf(dimension.width));
        requestParameters.put("HEIGHT",     String.valueOf(dimension.height));
        requestParameters.put("LAYERS",     toString(layers));
        requestParameters.put("STYLES",     toString(styles));

        if (sld != null) {
            requestParameters.put("SLD",sld);
        }
        if (sldBody != null) {
            requestParameters.put("SLD_BODY",sldBody);
        }

        requestParameters.putAll(dims);
        requestParameters.putAll(toString(enveloppe));

        return super.getURL();
    }

    protected String getBasePath(){
        if(layers == null || layers.length == 0){
            throw new IllegalArgumentException("Layers are not defined");
        }
        if(dimension == null){
            throw new IllegalArgumentException("Dimension is not defined");
        }

        final StringBuilder sb = new StringBuilder();
        sb.append(serverURL);

        if(!sb.toString().endsWith("?")){
            sb.append("?");
        }

        sb.append("SERVICE=WMS");
        sb.append("&REQUEST=GetMap");
        sb.append("&VERSION=").append(version);
        sb.append("&EXCEPTIONS=").append(exception);
        sb.append("&FORMAT=").append(format);
        sb.append("&WIDTH=").append(dimension.width);
        sb.append("&HEIGHT=").append(dimension.height);
        sb.append("&LAYERS=").append(toString(layers));
        sb.append("&STYLES=").append(toString(styles));

        if (sld != null) {
            sb.append("&SLD=").append(sld);
        }
        if (sldBody != null) {
            sb.append("&SLD_BODY=").append(sldBody);
        }

        for(String key : dims.keySet()){
            sb.append('&').append(key).append('=').append(dims.get(key));
        }

        sb.append(toString(enveloppe));

        return sb.toString();
    }

    private String toString(String[] vars){
        if(vars == null || vars.length == 0) return "";

        final StringBuilder sb = new StringBuilder();
        int i=0;
        for(;i<vars.length-1;i++){
            sb.append(vars[i]).append(',');
        }
        sb.append(vars[i]);

        return sb.toString();
    }

    protected abstract Map<String,String> toString(Envelope env);

}
