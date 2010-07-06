package org.geotoolkit.data.kml.model;

/**
 *
 * <p>This interface maps LatLonBox element.</p>
 *
 * <p>Type of the KML element is an extension of AbstractLatLonBoxType type. But there is
 * no substitution group corresponding to this abstract type. LatLonBox is member of
 * AbstractObjectSubstitution Group.</p>
 *
 * <p>This interface considers logical to inherit from AbstractLatLonBox interface which both
 * contains AbstractLatLonBoxType mapping and extends AbstractObject.</p>
 *
 * <pre>
 * &lt;element name="LatLonBox" type="kml:LatLonBoxType" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * &lt;complexType name="LatLonBoxType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractLatLonBoxType">
 *          &lt;sequence>
 *              &lt;element ref="kml:rotation" minOccurs="0"/>
 *              &lt;element ref="kml:LatLonBoxSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:LatLonBoxObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="LatLonBoxSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="LatLonBoxObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface LatLonBox extends AbstractLatLonBox {

    /**
     *
     * @return
     */
    public double getRotation();

    /**
     *
     * @param rotation
     */
    public void setRotation(double rotation);

}
