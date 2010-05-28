package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps Location element.</p>
 * 
 * <br />&lt;element name="Location" type="kml:LocationType" substitutionGroup="kml:AbstractObjectGroup"/>
 * <br />&lt;complexType name="LocationType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractObjectType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:longitude" minOccurs="0"/>
 * <br />&lt;element ref="kml:latitude" minOccurs="0"/>
 * <br />&lt;element ref="kml:altitude" minOccurs="0"/>
 * <br />&lt;element ref="kml:LocationSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:LocationObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="LocationSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="LocationObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>

 *
 * @author Samuel Andrés
 */
public interface Location extends AbstractObject {

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
     * @return the list of Location simple extensions
     */
    public List<SimpleType> getLocationSimpleExtensions();

    /**
     *
     * @return the list of Location object extensions
     */
    public List<AbstractObject> getLocationObjectExtensions();

}