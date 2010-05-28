package org.geotoolkit.data.model.kml;

/**
 * <p>This enumeration maps styleStateEnumType.</p>
 *
 * <br />&lt;simpleType name="styleStateEnumType">
 * <br />&lt;restriction base="string">
 * <br />&lt;enumeration value="normal"/>
 * <br />&lt;enumeration value="highlight"/>
 * <br />&lt;/restriction>
 * <br />&lt;/simpleType>
 *
 * @author Samuel Andrés
 */
public enum StyleState {

    NORMAL("normal"),
    HIGHLIGHT("highlight");

    private String styleState;

    /**
     * 
     * @param itemIconState
     */
    private StyleState(String itemIconState){
        this.styleState = itemIconState;
    }

    /**
     *
     * @return
     */
    public String getStyleState(){
        return this.styleState;
    }

    /**
     *
     * @param styleState
     * @return The StyleState instance corresponding to the styleState parameter.
     */
    public static StyleState transform(String styleState){
        return transform(styleState, null);
    }

    /**
     *
     * @param styleState
     * @param defaultValue The default value to return if styleState String parameter
     * do not correspond to one StyleState instance.
     * @return The StyleState instance corresponding to the styleState parameter.
     */
    public static StyleState transform(String styleState, StyleState defaultValue){
        StyleState resultat = defaultValue;
        for(StyleState cm : StyleState.values()){
            if(cm.getStyleState().equals(styleState)){
                resultat = cm;
                break;
            }
        }
        return resultat;
    }
}
