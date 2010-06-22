package org.geotoolkit.data.kml.model;
/**
 * <p>This enumeration maps shapeEnumType type.</p>
 *
 * <pre>
 * &lt;simpleType name="shapeEnumType">
 *  &lt;restriction base="string">
 *      &lt;enumeration value="rectangle"/>
 *      &lt;enumeration value="cylinder"/>
 *      &lt;enumeration value="sphere"/>
 *  &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public enum Shape {

    RECTANGLE("rectangle"),
    CYLINDER("cylinder"),
    SPHERE("sphere");

    private final String shape;

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
        for(Shape s : Shape.values()){
            if(s.getShape().equals(shape)) return s;
        }
        return defaultValue;
    }
}
