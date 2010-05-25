package org.geotoolkit.data.model.kml;
/**
 *
 * @author Samuel Andrés
 */
public enum ViewRefreshMode {

    NEVER("never"),
    ON_REQUEST("onRequest"),
    ON_STOP("onStop"),
    ON_REGION("onRegion");

    private String viewRefreshMode;

    private ViewRefreshMode(String viewRefreshMode){
        this.viewRefreshMode = viewRefreshMode;
    }

    public String getViewRefreshMode(){
        return this.viewRefreshMode;
    }

    public static ViewRefreshMode transform(String viewRefreshMode){
        return transform(viewRefreshMode, null);
    }

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
