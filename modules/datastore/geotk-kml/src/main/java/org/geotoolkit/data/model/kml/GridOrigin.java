package org.geotoolkit.data.model.kml;
/**
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

}
