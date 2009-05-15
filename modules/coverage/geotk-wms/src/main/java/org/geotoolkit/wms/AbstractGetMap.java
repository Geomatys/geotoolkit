
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
    protected Boolean transparent = true;

    protected AbstractGetMap(String serverURL,String version){
        super(serverURL);
        this.version = version;
    }

    @Override
    public String[] getLayers() {
        return layers;
    }

    @Override
    public void setLayers(String... layers) {
        this.layers = layers;
    }

    @Override
    public Envelope getEnvelope() {
        return enveloppe;
    }

    @Override
    public void setEnvelope(Envelope env) {
        this.enveloppe = env;
    }

    @Override
    public Dimension getDimension() {
        return dimension;
    }

    @Override
    public void setDimension(Dimension dim) {
        this.dimension = dim;
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public String getExceptions() {
        return exception;
    }

    @Override
    public void setExceptions(String ex) {
        this.exception = ex;
    }

    @Override
    public String[] getStyles() {
        return styles;
    }

    @Override
    public void setStyles(String... styles) {
        this.styles = styles;
    }

    @Override
    public String getSld(){
        return sld;
    }

    @Override
    public void setSld(String sld){
        this.sld = sld;
    }

    @Override
    public String getSldBody(){
        return sldBody;
    }

    @Override
    public void setSldBody(String sldBody){
        this.sldBody = sldBody;
    }
    
    @Override
    public boolean getTransparent(){
        return transparent;
    }

    @Override
    public void setTransparent(boolean transparent){
        this.transparent = transparent;
    }
    
    @Override
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
        requestParameters.put("TRANSPARENT", Boolean.toString(transparent).toUpperCase());

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
