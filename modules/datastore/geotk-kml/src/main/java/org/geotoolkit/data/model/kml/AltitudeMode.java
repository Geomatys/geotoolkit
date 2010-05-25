package org.geotoolkit.data.model.kml;

/**
 *
 * @author Samuel Andrés
 */
public enum AltitudeMode {

    CLAMP_TO_GROUND("clampToGround"),
    RELATIVE_TO_GROUND("relativeToGround"),
    ABSOLUTE("absolute");

    private String altitudeMode;

    private AltitudeMode(String altitudeMode){
        this.altitudeMode = altitudeMode;
    }

    public String getAltitudeMode(){
        return this.altitudeMode;
    }

    public static AltitudeMode transform(String altitudeMode){
        return transform(altitudeMode, null);
    }

    public static AltitudeMode transform(String altitudeMode, AltitudeMode defaultValue){
        AltitudeMode resultat = defaultValue;
        for(AltitudeMode cm : AltitudeMode.values()){
            if(cm.getAltitudeMode().equals(altitudeMode)){
                resultat = cm;
                break;
            }
        }
        return resultat;
    }
}
