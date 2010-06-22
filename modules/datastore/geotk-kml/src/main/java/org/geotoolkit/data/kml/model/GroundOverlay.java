package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 * <p>This interface maps GroundOverlay element.</p>
 *
 * <pre>
 * &lt;element name="GroundOverlay" type="kml:GroundOverlayType" substitutionGroup="kml:AbstractOverlayGroup"/>
 *
 * &lt;complexType name="GroundOverlayType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractOverlayType">
 *          &lt;sequence>
 *              &lt;element ref="kml:altitude" minOccurs="0"/>
 *              &lt;element ref="kml:altitudeModeGroup" minOccurs="0"/>
 *              &lt;element ref="kml:LatLonBox" minOccurs="0"/>
 *              &lt;element ref="kml:GroundOverlaySimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:GroundOverlayObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="GroundOverlaySimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="GroundOverlayObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * <h3>OGC Documentation</h3>
 *
 * <p>Specifies the distance above the terrain in meters. It shall be interpreted according to
 * kml:altitudeMode. Only kml:altitudeMode clampToGround or absolute values shall be
 * encoded for kml:GroundOverlay.</p>
 *
 * <p>A kml:GroundOverlay element shall contain the kml:Icon and kml:LatLonBox child
 * elements outside of an update context, that is when not a descendant of kml:Update.</p>
 *
 * @author Samuel Andrés
 */
public interface GroundOverlay extends AbstractOverlay {

    /**
     *
     * @return
     */
    public double getAltitude();

    /**
     *
     * @return
     */
    public AltitudeMode getAltitudeMode();

    /**
     *
     * @return
     */
    public LatLonBox getLatLonBox();

    /**
     *
     * @return
     */
    public List<SimpleType> getGroundOverlaySimpleExtensions();

    /**
     * 
     * @return
     */
    public List<AbstractObject> getGroundOverlayObjectExtensions();

    /**
     *
     * @param altitude
     */
    public void setAltitude(double altitude);

    /**
     *
     * @param altitudeMode
     */
    public void setAltitudeMode(AltitudeMode altitudeMode);

    /**
     *
     * @param latLonBox
     */
    public void setLatLonBox(LatLonBox latLonBox);

    /**
     *
     * @param groundOverlaySimpleExtensions
     */
    public void setGroundOverlaySimpleExtensions(List<SimpleType> groundOverlaySimpleExtensions);

    /**
     *
     * @param groundOverlayObjectExtensions
     */
    public void setGroundOverlayObjectExtensions(List<AbstractObject> groundOverlayObjectExtensions);

}
