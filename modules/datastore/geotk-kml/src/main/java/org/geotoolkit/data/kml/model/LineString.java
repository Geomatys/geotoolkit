package org.geotoolkit.data.kml.model;

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
     * @return
     */
    Coordinates getCoordinateSequence();

    /**
     *
     * @return the exetrude value.
     */
    boolean getExtrude();

    /**
     *
     * @return the tessalate value.
     */
    boolean getTessellate();

    /**
     *
     * @return the altitude mode.
     */
    AltitudeMode getAltitudeMode();

    /**
     * 
     * @param extrude
     */
    void setExtrude(boolean extrude);

    /**
     *
     * @param tesselate
     */
    void setTessellate(boolean tessellate);

    /**
     *
     * @param altitudeMode
     */
    void setAltitudeMode(AltitudeMode altitudeMode);

}
