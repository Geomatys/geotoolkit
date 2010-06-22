package org.geotoolkit.data.kml.model;

/**
 *
 * @author Samuel Andrés
 */
public enum Units {

    FRACTION("fraction"),
    PIXELS("pixels"),
    INSET_PIXELS("insetPixels");
    
    private final String unit;

    /**
     * 
     * @param unit
     */
    private Units(String unit){
        this.unit = unit;
    }

    /**
     *
     * @return
     */
    public String getUnit(){
        return this.unit;
    }

    /**
     *
     * @param unit
     * @return The Units instance corresponding to the unit parameter.
     */
    public static Units transform(String unit){
        return transform(unit, null);
    }

    /**
     *
     * @param unit
     * @param defaultValue The default value to return if unit String parameter
     * do not correspond to one Units instance.
     * @return The Units instance corresponding to the unit parameter.
     */
    public static Units transform(String unit, Units defaultValue){
        for(Units u : Units.values()){
            if(u.getUnit().equals(unit)) return u;
        }
        return defaultValue;
    }

}
