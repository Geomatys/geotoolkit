package org.geotoolkit.data.kml.model;

/**
 * <p>This enumeration maps gridOriginEnumType type.</p>
 *
 * <pre>
 * &lt;simpleType name="gridOriginEnumType">
 *  &lt;restriction base="string">
 *      &lt;enumeration value="lowerLeft"/>
 *      &lt;enumeration value="upperLeft"/>
 *  &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public enum GridOrigin {

    LOWER_LEFT("lowerLeft"),
    UPPER_LEFT("upperLeft");

    private final String gridOrigin;

    private GridOrigin(String gridOrigin){
        this.gridOrigin = gridOrigin;
    }

    /**
     *
     * @return The String value of the enumeration.
     */
    public String getGridOrigin(){
        return this.gridOrigin;
    }

    /**
     *
     * @param gridOrigin
     * @return The GridOrigin instance corresponding to the gridOrigin parameter.
     */
    public static GridOrigin transform(String gridOrigin){
        return transform(gridOrigin, null);
    }

    /**
     *
     * @param gridOrigin
     * @param defaultValue The default value to return if gridOrigin String parameter
     * do not correspond to one GridOrigin instance.
     * @return The GridOrigin instance corresponding to the gridOrigin parameter.
     */
    public static GridOrigin transform(String gridOrigin, GridOrigin defaultValue){
        for(GridOrigin go : GridOrigin.values()){
            if(go.getGridOrigin().equals(gridOrigin)) return go;
        }
        return defaultValue;
    }
}
