package org.geotoolkit.data.model.kml;
/**
 * <p>This enumeration maps shapeEnumType type.</p>
 *
 * <br />&lt;simpleType name="shapeEnumType">
 * <br />&lt;restriction base="string">
 * <br />&lt;enumeration value="rectangle"/>
 * <br />&lt;enumeration value="cylinder"/>
 * <br />&lt;enumeration value="sphere"/>
 * <br />&lt;/restriction>
 * <br />&lt;/simpleType>
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
