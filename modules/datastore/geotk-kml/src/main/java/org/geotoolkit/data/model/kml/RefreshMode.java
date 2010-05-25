package org.geotoolkit.data.model.kml;
/**
 *
 * @author Samuel Andrés
 */
public enum RefreshMode {

    ON_CHANGE("onChange"),
    ON_INTERVAL("onInterval"),
    ON_EXPIRE("onExpire");

    private String refreshMode;

    private RefreshMode(String refreshMode){
        this.refreshMode = refreshMode;
    }

    public String getRefreshMode(){
        return this.refreshMode;
    }

    public static RefreshMode transform(String refreshMode){
        return transform(refreshMode, null);
    }

    public static RefreshMode transform(String refreshMode, RefreshMode defaultValue){
        RefreshMode resultat = defaultValue;
        for(RefreshMode cm : RefreshMode.values()){
            if(cm.getRefreshMode().equals(refreshMode)){
                resultat = cm;
                break;
            }
        }
        return resultat;
    }
}
