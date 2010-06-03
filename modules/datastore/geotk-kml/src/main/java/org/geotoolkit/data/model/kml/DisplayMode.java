package org.geotoolkit.data.model.kml;

/**
 *
 * <p>Thi enumeration maps displayMode type.</p>
 *
 * <pre>
 * &lt;simpleType name="displayModeEnumType">
 *  &lt;restriction base="string">
 *      &lt;enumeration value="default"/>
 *      &lt;enumeration value="hide"/>
 *  &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public enum DisplayMode {

    DEFAULT("default"),
    HIDE("hide");

    private String mode;

    /**
     *
     * @param mode
     */
    private DisplayMode(String mode){
        this.mode = mode;
    }

    /**
     *
     * @return The String value of the enumeration.
     */
    public String getDisplayMode(){
        return this.mode;
    }

    /**
     *
     * @param mode
     * @return The DisplayMode instance corresponding to the mode parameter.
     */
    public static DisplayMode transform(String mode){
        return transform(mode, null);
    }

    /**
     *
     * @param mode
     * @param defaultValue The default value to return if mode String parameter
     * do not correspond to one DisplayMode instance.
     * @return The DisplayMode instance corresponding to the mode parameter.
     */
    public static DisplayMode transform(String mode, DisplayMode defaultValue){
        DisplayMode resultat = defaultValue;
        for(DisplayMode cm : DisplayMode.values()){
            if(cm.getDisplayMode().equals(mode)){
                resultat = cm;
                break;
            }
        }
        return resultat;
    }

}
