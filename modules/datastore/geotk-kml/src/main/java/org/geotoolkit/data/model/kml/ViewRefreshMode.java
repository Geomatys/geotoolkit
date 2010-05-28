package org.geotoolkit.data.model.kml;
/**
 * <p>This enumeration maps viewRefreshModeEnumType type.</p>
 *
 * <br />&lt;simpleType name="viewRefreshModeEnumType">
 * <br />&lt;restriction base="string">
 * <br />&lt;enumeration value="never"/>
 * <br />&lt;enumeration value="onRequest"/>
 * <br />&lt;enumeration value="onStop"/>
 * <br />&lt;enumeration value="onRegion"/>
 * <br />&lt;/restriction>
 * <br />&lt;/simpleType>
 *
 * @author Samuel Andrés
 */
public enum ViewRefreshMode {

    NEVER("never"),
    ON_REQUEST("onRequest"),
    ON_STOP("onStop"),
    ON_REGION("onRegion");

    private String viewRefreshMode;

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
        ViewRefreshMode resultat = defaultValue;
        for(ViewRefreshMode cm : ViewRefreshMode.values()){
            if(cm.getViewRefreshMode().equals(viewRefreshMode)){
                resultat = cm;
                break;
            }
        }
        return resultat;
    }

}
