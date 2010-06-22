package org.geotoolkit.data.kml.model;

/**
 * <p>This interface maps vec2Type type.</p>
 *
 * <pre>
 * &lt;complexType name="vec2Type" abstract="false">
 *  &lt;attribute name="x" type="double" default="1.0"/>
 *  &lt;attribute name="y" type="double" default="1.0"/>
 *  &lt;attribute name="xunits" type="kml:unitsEnumType" use="optional" default="fraction"/>
 *  &lt;attribute name="yunits" type="kml:unitsEnumType" use="optional" default="fraction"/>
 * &lt;/complexType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Vec2 {

    /**
     *
     * @return
     */
    public double getX();

    /**
     *
     * @return
     */
    public double getY();

    /**
     *
     * @return
     */
    public Units getXUnits();

    /**
     * 
     * @return
     */
    public Units getYUnits();

}
