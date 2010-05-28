package org.geotoolkit.data.model.kml;

/**
 * <p>This interface maps vec2Type type.</p>
 *
 * <br />&lt;complexType name="vec2Type" abstract="false">
 * <br />&lt;attribute name="x" type="double" default="1.0"/>
 * <br />&lt;attribute name="y" type="double" default="1.0"/>
 * <br />&lt;attribute name="xunits" type="kml:unitsEnumType" use="optional" default="fraction"/>
 * <br />&lt;attribute name="yunits" type="kml:unitsEnumType" use="optional" default="fraction"/>
 * <br />&lt;/complexType>
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
