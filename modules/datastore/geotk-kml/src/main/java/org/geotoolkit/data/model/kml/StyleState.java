package org.geotoolkit.data.model.kml;

/**
 *
 * @author Samuel Andrés
 */
public enum StyleState {

    NORMAL("normal"),
    HIGHLIGHT("highlight");

    private String styleState;

    private StyleState(String itemIconState){
        this.styleState = itemIconState;
    }

    public String getStyleState(){
        return this.styleState;
    }

    public static StyleState transform(String styleState){
        return transform(styleState, null);
    }

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
