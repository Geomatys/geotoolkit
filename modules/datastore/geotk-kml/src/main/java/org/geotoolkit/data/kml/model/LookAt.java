package org.geotoolkit.data.kml.model;

/**
 *
 * <p>This interface maps Lod element.</p>
 *
 * <pre>
 * &lt;element name="LookAt" type="kml:LookAtType" substitutionGroup="kml:AbstractViewGroup"/>
 *
 * &lt;complexType name="LookAtType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractViewType">
 *          &lt;sequence>
 *              &lt;element ref="kml:longitude" minOccurs="0"/>
 *              &lt;element ref="kml:latitude" minOccurs="0"/>
 *              &lt;element ref="kml:altitude" minOccurs="0"/>
 *              &lt;element ref="kml:heading" minOccurs="0"/>
 *              &lt;element ref="kml:tilt" minOccurs="0"/>
 *              &lt;element ref="kml:range" minOccurs="0"/>
 *              &lt;element ref="kml:altitudeModeGroup" minOccurs="0"/>
 *              &lt;element ref="kml:LookAtSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:LookAtObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="LookAtSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="LookAtObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface LookAt extends AbstractView {

    /**
     *
     * @return
     */
    public double getLongitude();

    /**
     *
     * @return
     */
    public double getLatitude();

    /**
     *
     * @return
     */
    public double getAltitude();

    /**
     *
     * @return
     */
    public double getHeading();

    /**
     *
     * @return
     */
    public double getTilt();

    /**
     *
     * @return
     */
    public double getRange();

    /**
     *
     * @param angle
     */
    public void setLongitude(double longitude);

    /**
     *
     * @param latitude
     */
    public void setLatitude(double latitude);

    /**
     *
     * @param altitude
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
     * <p>Specific setter for 2.1 Kml version.</p>
     *
     * @param tilt
     * @deprecated
     */
    @Deprecated
    public void setTilt_v2_1(double tilt);

    /**
     *
     * @param range
     */
    public void setRange(double range);

}
