package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps Camera element.</p>
 * 
 * <br />&lt;element name="Camera" type="kml:CameraType" substitutionGroup="kml:AbstractViewGroup"/>
 * <br />&lt;complexType name="CameraType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractViewType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:longitude" minOccurs="0"/>
 * <br />&lt;element ref="kml:latitude" minOccurs="0"/>
 * <br />&lt;element ref="kml:altitude" minOccurs="0"/>
 * <br />&lt;element ref="kml:heading" minOccurs="0"/>
 * <br />&lt;element ref="kml:tilt" minOccurs="0"/>
 * <br />&lt;element ref="kml:roll" minOccurs="0"/>
 * <br />&lt;element ref="kml:altitudeModeGroup" minOccurs="0"/>
 * <br />&lt;element ref="kml:CameraSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:CameraObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="CameraSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="CameraObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * @author Samuel Andrés
 */
public interface Camera extends AbstractView {

    /**
     *
     * @return the longitude angle.
     */
    public Angle180 getLongitude();

    /**
     *
     * @return the latiude angle.
     */
    public Angle90 getLatitude();

    /**
     *
     * @return the altitude.
     */
    public double getAltitude();

    /**
     *
     * @return the heading angle.
     */
    public Angle360 getHeading();

    /**
     *
     * @return the tilt angle.
     */
    public Anglepos180 getTilt();

    /**
     *
     * @return the roll angle.
     */
    public Angle180 getRoll();

    /**
     *
     * @return the altitude mode.
     */
    public AltitudeMode getAltitudeMode();

    /**
     *
     * @return the list of Camera simple extensions.
     */
    public List<SimpleType> getCameraSimpleExtensions();

    /**
     *
     * @return the list of Camera object extensions.
     */
    public List<AbstractObject> getCameraObjectExtensions();

}
