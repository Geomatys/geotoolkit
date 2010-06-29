package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 * <p>This interface maps Location element.</p>
 *
 * <pre>
 * &lt;element name="Location" type="kml:LocationType" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * &lt;complexType name="LocationType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="kml:longitude" minOccurs="0"/>
 *              &lt;element ref="kml:latitude" minOccurs="0"/>
 *              &lt;element ref="kml:altitude" minOccurs="0"/>
 *              &lt;element ref="kml:LocationSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:LocationObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="LocationSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="LocationObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Location extends AbstractObject {

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
     * @return the list of Location simple extensions
     */
    public List<SimpleType> getLocationSimpleExtensions();

    /**
     *
     * @return the list of Location object extensions
     */
    public List<AbstractObject> getLocationObjectExtensions();

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
     * @param altitude
     */
    public void setAltitude(double altitude);

    /**
     *
     * @param locationSimpleExtensions
     */
    public void setLocationSimpleExtensions(List<SimpleType> locationSimpleExtensions);

    /**
     * 
     * @param locationObjectExtensions
     */
    public void setLocationObjectExtensions(List<AbstractObject> locationObjectExtensions);

}