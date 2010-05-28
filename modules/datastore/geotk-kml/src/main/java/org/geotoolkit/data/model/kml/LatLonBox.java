package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

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
 * <br />&lt;element name="LatLonBox" type="kml:LatLonBoxType" substitutionGroup="kml:AbstractObjectGroup"/>
 * <br />&lt;complexType name="LatLonBoxType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractLatLonBoxType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:rotation" minOccurs="0"/>
 * <br />&lt;element ref="kml:LatLonBoxSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:LatLonBoxObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="LatLonBoxSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="LatLonBoxObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>

 * @author Samuel Andrés
 */
public interface LatLonBox extends AbstractLatLonBox {

    /**
     *
     * @return
     */
    public Angle180 getRotation();

    /**
     *
     * @return the list of LatLonBox simple extensions.
     */
    public List<SimpleType> getLatLonBoxSimpleExtensions();

    /**
     *
     * @return the list of LatLonBox object extensions.
     */
    public List<AbstractObject> getLatLonBoxObjectExtensions();

}
