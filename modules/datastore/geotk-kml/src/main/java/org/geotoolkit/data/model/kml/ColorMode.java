package org.geotoolkit.data.model.kml;
/**
 *
 * @author Samuel Andrés
 */
public enum ColorMode {

    NORMAL("normal"),
    RANDOM("random");

    private String colorMode;

    private ColorMode(String colorMode){
        this.colorMode = colorMode;
    }

    public String getColorMode(){
        return this.colorMode;
    }

    public static ColorMode transform(String colorMode){
        return transform(colorMode, null);
    }

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
