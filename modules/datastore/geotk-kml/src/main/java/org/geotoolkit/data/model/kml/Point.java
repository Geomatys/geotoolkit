package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p></p>
 *
 * <br />&lt;element name="Point" type="kml:PointType" substitutionGroup="kml:AbstractGeometryGroup"/>
 * <br />&lt;complexType name="PointType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractGeometryType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:extrude" minOccurs="0"/>
 * <br />&lt;element ref="kml:altitudeModeGroup" minOccurs="0"/>
 * <br />&lt;element ref="kml:coordinates" minOccurs="0"/>
 * <br />&lt;element ref="kml:PointSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:PointObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="PointSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
  * <br />&lt;element name="PointObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * @author Samuel Andrés
 */
public interface Point extends AbstractGeometry{

    /**
     *
     * @return
     */
    public boolean getExtrude();

    /**
     *
     * @return
     */
    public AltitudeMode getAltitudeMode();

    /**
     *
     * @return
     */
    public Coordinates getCoordinates();

    /**
     *
     * @return the list of Point simple extensions.
     */
    public List<SimpleType> getPointSimpleExtensions();

    /**
     * 
     * @return the list of Point object extensions.
     */
    public List<AbstractObject> getPointObjectExtensions();
}
