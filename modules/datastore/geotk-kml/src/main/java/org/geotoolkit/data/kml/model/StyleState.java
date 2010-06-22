package org.geotoolkit.data.kml.model;

/**
 * <p>This enumeration maps styleStateEnumType.</p>
 *
 * <pre>
 * &lt;simpleType name="styleStateEnumType">
 *  &lt;restriction base="string">
 *      &lt;enumeration value="normal"/>
 *      &lt;enumeration value="highlight"/>
 *  &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public enum StyleState {

    NORMAL("normal"),
    HIGHLIGHT("highlight");

    private final String styleState;

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
        for(StyleState cm : StyleState.values()){
            if(cm.getStyleState().equals(styleState)) return cm;
        }
        return defaultValue;
    }
}
