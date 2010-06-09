package org.geotoolkit.data.model.kml;
/**
 * <p>This enumeration maps viewRefreshModeEnumType type.</p>
 *
 * <pre>
 * &lt;simpleType name="viewRefreshModeEnumType">
 *  &lt;restriction base="string">
 *      &lt;enumeration value="never"/>
 *      &lt;enumeration value="onRequest"/>
 *      &lt;enumeration value="onStop"/>
 *      &lt;enumeration value="onRegion"/>
 *  &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public enum ViewRefreshMode {

    NEVER("never"),
    ON_REQUEST("onRequest"),
    ON_STOP("onStop"),
    ON_REGION("onRegion");

    private final String viewRefreshMode;

    /**
     * 
     * @param viewRefreshMode
     */
    private ViewRefreshMode(String viewRefreshMode){
        this.viewRefreshMode = viewRefreshMode;
    }

    /**
     *
     * @return
     */
    public String getViewRefreshMode(){
        return this.viewRefreshMode;
    }

    /**
     *
     * @param viewRefreshMode
     * @return The ViewRefreshMode instance corresponding to the viewRefreshMode parameter.
     */
    public static ViewRefreshMode transform(String viewRefreshMode){
        return transform(viewRefreshMode, null);
    }

    /**
     *
     * @param refreshMode
     * @param defaultValue The default value to return if viewRefreshMode String parameter
     * do not correspond to one ViewRefreshMode instance.
     * @return The ViewRefreshMode instance corresponding to the viewRefreshMode parameter.
     */
    public static ViewRefreshMode transform(String viewRefreshMode, ViewRefreshMode defaultValue){
        for(ViewRefreshMode cm : ViewRefreshMode.values()){
            if(cm.getViewRefreshMode().equals(viewRefreshMode)) return cm;
        }
        return defaultValue;
    }

}
