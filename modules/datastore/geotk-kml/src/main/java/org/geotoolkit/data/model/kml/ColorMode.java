package org.geotoolkit.data.model.kml;
/**
 * <p>This enumeration maps colorModeEnumType.</p>
 *
 * <br />&lt;simpleType name="colorModeEnumType">
 * <br />&lt;restriction base="string">
 * <br />&lt;enumeration value="normal"/>
 * <br />&lt;enumeration value="random"/>
 * <br />&lt;/restriction>
 * <br />&lt;/simpleType>
 *
 * @author Samuel Andrés
 */
public enum ColorMode {

    NORMAL("normal"),
    RANDOM("random");

    private String colorMode;

    /**
     *
     * @param colorMode
     */
    private ColorMode(String colorMode){
        this.colorMode = colorMode;
    }

    /**
     *
     * @return
     */
    public String getColorMode(){
        return this.colorMode;
    }

    /**
     *
     * @param colorMode
     * @return The ColorMode instance corresponding to the colorMode parameter.
     */
    public static ColorMode transform(String colorMode){
        return transform(colorMode, null);
    }

    /**
     *
     * @param colorMode
     * @param defaultValue The default value to return if colorMode String parameter
     * do not correspond to one ColorMode instance.
     * @return The ColorMode instance corresponding to the colorMode parameter.
     */
    public static ColorMode transform(String colorMode, ColorMode defaultValue){
        ColorMode resultat = defaultValue;
        for(ColorMode cm : ColorMode.values()){
            if(cm.getColorMode().equals(colorMode)){
                resultat = cm;
                break;
            }
        }
        return resultat;
    }
}
