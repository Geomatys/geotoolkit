package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

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

}
