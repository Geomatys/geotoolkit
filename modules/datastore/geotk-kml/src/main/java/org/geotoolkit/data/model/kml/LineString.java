package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * <p>This interface maps LineString element.</p>
 *
 * <br />&lt;element name="LineString" type="kml:LineStringType" substitutionGroup="kml:AbstractGeometryGroup"/>
 * <br />&lt;complexType name="LineStringType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractGeometryType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:extrude" minOccurs="0"/>
 * <br />&lt;element ref="kml:tessellate" minOccurs="0"/>
 * <br />&lt;element ref="kml:altitudeModeGroup" minOccurs="0"/>
 * <br />&lt;element ref="kml:coordinates" minOccurs="0"/>
 * <br />&lt;element ref="kml:LineStringSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:LineStringObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="LineStringSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="LineStringObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * @author Samuel Andrés
 */
public interface LineString extends AbstractGeometry {

    /**
     *
     * @return the exetrude value.
     */
    public boolean getExtrude();

    /**
     *
     * @return the tessalate value.
     */
    public boolean getTessellate();

    /**
     *
     * @return the altitude mode.
     */
    public AltitudeMode getAltitudeMode();

    /**
     *
     * @return the coordinates.
     */
    public Coordinates getCoordinates();

    /**
     *
     * @return the list of LineString simple extensions.
     */
    public List<SimpleType> getLineStringSimpleExtensions();

    /**
     *
     * @return the list of LineString object extensions.
     */
    public List<AbstractObject> getLineStringObjectExtensions();

}
