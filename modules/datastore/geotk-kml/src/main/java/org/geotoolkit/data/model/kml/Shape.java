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

    /**
     *
     * @param shape
     */
    private Shape(String shape){
        this.shape = shape;
    }

    /**
     *
     * @return
     */
    public String getShape(){
        return this.shape;
    }

    /**
     *
     * @param shape
     * @return The Shape instance corresponding to the shape parameter.
     */
    public static Shape transform(String shape){
        return transform(shape, null);
    }

    /**
     *
     * @param unit
     * @param defaultValue The default value to return if shape String parameter
     * do not correspond to one Shape instance.
     * @return The Shape instance corresponding to the shape parameter.
     */
    public static Shape transform(String shape, Shape defaultValue){
        Shape resultat = defaultValue;
        for(Shape s : Shape.values()){
            if(s.getShape().equals(shape)){
                resultat = s;
                break;
            }
        }
        return resultat;
    }
}
