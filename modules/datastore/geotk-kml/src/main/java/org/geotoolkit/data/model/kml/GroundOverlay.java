package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps GroundOverlay element.</p>
 *
 * <br />&lt;element name="GroundOverlay" type="kml:GroundOverlayType" substitutionGroup="kml:AbstractOverlayGroup"/>
 * <br />&lt;complexType name="GroundOverlayType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractOverlayType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:altitude" minOccurs="0"/>
 * <br />&lt;element ref="kml:altitudeModeGroup" minOccurs="0"/>
 * <br />&lt;element ref="kml:LatLonBox" minOccurs="0"/>
 * <br />&lt;element ref="kml:GroundOverlaySimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:GroundOverlayObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="GroundOverlaySimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="GroundOverlayObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
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
