package org.geotoolkit.data.model.kml;

/**
 *
 * @author Samuel Andrés
 */
public enum Units {

    FRACTION("fraction"),
    PIXELS("pixels"),
    INSET_PIXELS("insetPixels");
    
    private String unit;

    private Units(String unit){
        this.unit = unit;
    }

    public String getUnit(){
        return this.unit;
    }

    public static Units transform(String unit){
        return transform(unit, null);
    }

    public static Units transform(String unit, Units defaultValue){
        Units resultat = defaultValue;
        for(Units u : Units.values()){
            if(u.getUnit().equals(unit)){
                resultat = u;
                break;
            }
        }
        return resultat;
    }

}
