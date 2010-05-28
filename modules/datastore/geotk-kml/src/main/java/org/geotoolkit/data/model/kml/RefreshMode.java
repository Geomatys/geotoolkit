package org.geotoolkit.data.model.kml;
/**
 * <p>This enumeration maps refreshModeEnumType type.</p>
 *
 * <br />&lt;simpleType name="refreshModeEnumType">
 * <br />&lt;restriction base="string">
 * <br />&lt;enumeration value="onChange"/>
 * <br />&lt;enumeration value="onInterval"/>
 * <br />&lt;enumeration value="onExpire"/>
 * <br />&lt;/restriction>
 * <br />&lt;/simpleType>
 *
 * @author Samuel Andrés
 */
public enum RefreshMode {

    ON_CHANGE("onChange"),
    ON_INTERVAL("onInterval"),
    ON_EXPIRE("onExpire");

    private String refreshMode;

    /**
     * 
     * @param refreshMode
     */
    private RefreshMode(String refreshMode){
        this.refreshMode = refreshMode;
    }

    /**
     *
     * @return
     */
    public String getRefreshMode(){
        return this.refreshMode;
    }

    /**
     *
     * @param refreshMode
     * @return The RefreshMode instance corresponding to the refreshMode parameter.
     */
    public static RefreshMode transform(String refreshMode){
        return transform(refreshMode, null);
    }

    /**
     *
     * @param refreshMode
     * @param defaultValue The default value to return if refreshMode String parameter
     * do not correspond to one RefreshMode instance.
     * @return The RefreshMode instance corresponding to the refreshMode parameter.
     */
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
