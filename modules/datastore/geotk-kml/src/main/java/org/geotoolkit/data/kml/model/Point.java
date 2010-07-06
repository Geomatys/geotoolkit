package org.geotoolkit.data.kml.model;

/**
 * <p>This interface maps Point element.</p>
 *
 * <pre>
 * &lt;element name="Point" type="kml:PointType" substitutionGroup="kml:AbstractGeometryGroup"/>
 *
 * &lt;complexType name="PointType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractGeometryType">
 *          &lt;sequence>
 *              &lt;element ref="kml:extrude" minOccurs="0"/>
 *              &lt;element ref="kml:altitudeModeGroup" minOccurs="0"/>
 *              &lt;element ref="kml:coordinates" minOccurs="0"/>
 *              &lt;element ref="kml:PointSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:PointObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="PointSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="PointObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * <h3>OGC Documentation</h3>
 *
 * <p>A geographic location defined by a single geodetic longitude, geodetic latitude, and
 * (optional) altitude coordinate tuple.</p>
 *
 * @author Samuel Andrés
 */
public interface Point extends AbstractGeometry{
    
    /**
     *
     * @return
     */
    Coordinates getCoordinateSequence();

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
     * @param extrude
     */
    public void setExtrude(boolean extrude);

    /**
     *
     * @param altitudeMode
     */
    public void setAltitudeMode(AltitudeMode altitudeMode);
}
