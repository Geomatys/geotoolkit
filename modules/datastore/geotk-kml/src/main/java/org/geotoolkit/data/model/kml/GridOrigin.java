package org.geotoolkit.data.model.kml;

/**
 * <p>This enumeration maps gridOriginEnumType type.</p>
 *
 * <br />&lt;simpleType name="gridOriginEnumType">
 * <br />&lt;restriction base="string">
 * <br />&lt;enumeration value="lowerLeft"/>
 * <br />&lt;enumeration value="upperLeft"/>
 * <br />&lt;/restriction>
 * <br />&lt;/simpleType>
 *
 * @author Samuel Andrés
 */
public enum GridOrigin {

    LOWER_LEFT("lowerLeft"),
    UPPER_LEFT("upperLeft");

    private String gridOrigin;

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
        GridOrigin resultat = defaultValue;
        for(GridOrigin go : GridOrigin.values()){
            if(go.getGridOrigin().equals(gridOrigin)){
                resultat = go;
                break;
            }
        }
        return resultat;
    }
}
