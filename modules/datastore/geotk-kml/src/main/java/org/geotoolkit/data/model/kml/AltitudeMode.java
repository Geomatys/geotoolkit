package org.geotoolkit.data.model.kml;

/**
 *
 * <p>Thi enumeration maps altitudeMode whose altitudeModeEnumType
 * is the only one imlementation.</p>
 *
 * <pre>
 * &lt;element name="altitudeMode" type="kml:altitudeModeEnumType" default="clampToGround" substitutionGroup="kml:altitudeModeGroup"/>
 * 
 * &lt;simpleType name="altitudeModeEnumType">
 *  &lt;restriction base="string">
 *      &lt;enumeration value="clampToGround"/>
 *      &lt;enumeration value="relativeToGround"/>
 *      &lt;enumeration value="absolute"/>
 *  &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public enum AltitudeMode {

    CLAMP_TO_GROUND("clampToGround"),
    RELATIVE_TO_GROUND("relativeToGround"),
    ABSOLUTE("absolute");

    private String altitudeMode;

    /**
     *
     * @param altitudeMode
     */
    private AltitudeMode(String altitudeMode){
        this.altitudeMode = altitudeMode;
    }

    /**
     *
     * @return The String value of the enumeration.
     */
    public String getAltitudeMode(){
        return this.altitudeMode;
    }

    /**
     *
     * @param altitudeMode
     * @return The AltitudeMode instance corresponding to the altitudeMode parameter.
     */
    public static AltitudeMode transform(String altitudeMode){
        return transform(altitudeMode, null);
    }

    /**
     *
     * @param altitudeMode
     * @param defaultValue The default value to return if altitudeMode String parameter
     * do not correspond to one AltitudeMode instance.
     * @return The AltitudeMode instance corresponding to the altitudeMode parameter.
     */
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
