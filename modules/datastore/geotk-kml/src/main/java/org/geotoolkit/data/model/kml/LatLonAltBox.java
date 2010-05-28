package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps LatLonAltBox element.</p>
 *
 * <p>Type of the KML element is an extension of AbstractLatLonBoxType type. But there is
 * no substitution group corresponding to this abstract type. LatLonAltBox is member of
 * AbstractObjectSubstitution Group.</p>
 *
 * <p>This interface considers logical to inherit from AbstractLatLonBox interface which both
 * contains AbstractLatLonBoxType mapping and extends AbstractObject.</p>
 *
 * <br />&lt;element name="LatLonAltBox" type="kml:LatLonAltBoxType" substitutionGroup="kml:AbstractObjectGroup"/>
 * <br />&lt;complexType name="LatLonAltBoxType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractLatLonBoxType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:minAltitude" minOccurs="0"/>
 * <br />&lt;element ref="kml:maxAltitude" minOccurs="0"/>
 * <br />&lt;element ref="kml:altitudeModeGroup" minOccurs="0"/>
 * <br />&lt;element ref="kml:LatLonAltBoxSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:LatLonAltBoxObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="LatLonAltBoxSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="LatLonAltBoxObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * @author Samuel Andrés
 */
public interface LatLonAltBox extends AbstractLatLonBox{

    /**
     *
     * @return
     */
    public double getMinAltitude();

    /**
     *
     * @return
     */
    public double getMaxAltitude();

    /**
     *
     * @return
     */
    public AltitudeMode getAltitudeMode();

    /**
     *
     * @return the list of LatLonAltBox simple extensions.
     */
    public List<SimpleType> getLatLonAltBoxSimpleExtensions();

    /**
     *
     * @return the list of LatLonAltBox object extensions.
     */
    public List<AbstractObject> getLatLonAltBoxObjectExtensions();

}
