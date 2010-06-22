package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 *
 * <p>This interface maps LineString element.</p>
 *
 * <pre>
 * &lt;element name="LineString" type="kml:LineStringType" substitutionGroup="kml:AbstractGeometryGroup"/>
 *
 * &lt;complexType name="LineStringType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractGeometryType">
 *          &lt;sequence>
 *              &lt;element ref="kml:extrude" minOccurs="0"/>
 *              &lt;element ref="kml:tessellate" minOccurs="0"/>
 *              &lt;element ref="kml:altitudeModeGroup" minOccurs="0"/>
 *              &lt;element ref="kml:coordinates" minOccurs="0"/>
 *              &lt;element ref="kml:LineStringSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:LineStringObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="LineStringSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="LineStringObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
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
