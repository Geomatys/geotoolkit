package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

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
    public Angle180 getLongitude();

    /**
     *
     * @return
     */
    public Angle90 getLatitude();

    /**
     *
     * @return
     */
    public double getAltitude();

    /**
     *
     * @return
     */
    public Angle360 getHeading();

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
     * @return the list of LookAt simple extensions.
     */
    public List<SimpleType> getLookAtSimpleExtensions();

    /**
     *
     * @return the lis of LookAt object extensions.
     */
    public List<AbstractObject> getLookAtObjectExtensions();

    /**
     *
     * @param angle
     */
    public void setLongitude(Angle180 longitude);

    /**
     *
     * @param latitude
     */
    public void setLatitude(Angle90 latitude);

    /**
     *
     * @param altitude
     */
    public void setAltitude(double altitude);

    /**
     *
     * @param heading
     */
    public void setHeading(Angle360 heading);

    /**
     *
     * @param tilt
     */
    public void setTilt(double tilt);

    /**
     *
     * @param range
     */
    public void setRange(double range);

    /**
     *
     * @param lookAtSimpleExtensions
     */
    public void setLookAtSimpleExtensions(List<SimpleType> lookAtSimpleExtensions);

    /**
     *
     * @param lookAtObjectExtensions
     */
    public void setLookAtObjectExtensions(List<AbstractObject> lookAtObjectExtensions);
}
