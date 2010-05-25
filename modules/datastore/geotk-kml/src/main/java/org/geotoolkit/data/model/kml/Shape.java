package org.geotoolkit.data.model.kml;
/**
 *
 * @author Samuel Andrés
 */
public enum Shape {

    RECTANGLE("rectangle"),
    CYLINDER("cylinder"),
    SPHERE("sphere");

    private String shape;

    private Shape(String shape){
        this.shape = shape;
    }

}
