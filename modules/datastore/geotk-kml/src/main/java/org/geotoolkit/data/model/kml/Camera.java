package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps Camera element.</p>
 *
 * <pre>
 * &lt;element name="Camera" type="kml:CameraType" substitutionGroup="kml:AbstractViewGroup"/>
 *
 * &lt;complexType name="CameraType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractViewType">
 *          &lt;sequence>
 *              &lt;element ref="kml:longitude" minOccurs="0"/>
 *              &lt;element ref="kml:latitude" minOccurs="0"/>
 *              &lt;element ref="kml:altitude" minOccurs="0"/>
 *              &lt;element ref="kml:heading" minOccurs="0"/>
 *              &lt;element ref="kml:tilt" minOccurs="0"/>
 *              &lt;element ref="kml:roll" minOccurs="0"/>
 *              &lt;element ref="kml:altitudeModeGroup" minOccurs="0"/>
 *              &lt;element ref="kml:CameraSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:CameraObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="CameraSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="CameraObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Camera extends AbstractView {

    /**
     *
     * @return the longitude angle.
     */
    public double getLongitude();

    /**
     *
     * @return the latiude angle.
     */
    public double getLatitude();

    /**
     *
     * @return the altitude.
     */
    public double getAltitude();

    /**
     *
     * @return the heading angle.
     */
    public double getHeading();

    /**
     *
     * @return the tilt angle.
     */
    public double getTilt();

    /**
     *
     * @return the roll angle.
     */
    public double getRoll();

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

    /**
     *
     * @param longitude
     */
    public void setLongitude(double longitude);

    /**
     *
     * @param latitude
     */
    public void setLatitude(double latitude);

    /**
     *
     * @param atitude
     */
    public void setAltitude(double altitude);

    /**
     *
     * @param heading
     */
    public void setHeading(double heading);

    /**
     *
     * @param tilt
     */
    public void setTilt(double tilt);

    /**
     *
     * @param roll
     */
    public void setRoll(double roll);

    /**
     *
     * @param altitudeMode
     */
    public void setAltitudeMode(AltitudeMode altitudeMode);

    /**
     *
     * @param cameraSimpleExtensions
     */
    public void setCameraSimpleExtensions(List<SimpleType> cameraSimpleExtensions);

    /**
     * 
     * @param cameraObjectExtensions
     */
    public void setCameraObjectExtensions(List<AbstractObject> cameraObjectExtensions);

}
