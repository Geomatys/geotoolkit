package org.geotoolkit.data.model.kml;

/**
 *
 * @author Samuel Andrés
 */
public enum DisplayMode {

    DEFAULT("default"),
    HIDE("hide");

    private String mode;

    private DisplayMode(String mode){
        this.mode = mode;
    }

    public String getDisplayMode(){
        return this.mode;
    }

    public static DisplayMode transform(String mode){
        return transform(mode, null);
    }

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
