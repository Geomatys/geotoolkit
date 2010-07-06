package org.geotoolkit.data.kml.model;

/**
 * <p>This interface maps AbstractLatLonBoxType type.</p>
 *
 * <pre>
 * &lt;complexType name="AbstractLatLonBoxType" abstract="true">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="kml:north" minOccurs="0"/>
 *              &lt;element ref="kml:south" minOccurs="0"/>
 *              &lt;element ref="kml:east" minOccurs="0"/>
 *              &lt;element ref="kml:west" minOccurs="0"/>
 *              &lt;element ref="kml:AbstractLatLonBoxSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:AbstractLatLonBoxObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="AbstractLatLonBoxSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="AbstractLatLonBoxObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface AbstractLatLonBox extends AbstractObject {

    /**
     *
     * @return the north angle.
     */
    public double getNorth();

    /**
     *
     * @return the south angle.
     */
    public double getSouth();

    /**
     *
     * @return the east angle.
     */
    public double getEast();

    /**
     *
     * @return the west angle.
     */
    public double getWest();

    /**
     *
     * @param north
     */
    public void setNorth(double north);

    /**
     *
     * @param south
     */
    public void setSouth(double south);

    /**
     *
     * @param east
     */
    public void setEast(double east);

    /**
     *
     * @param west
     */
    public void setWest(double west);

}
