package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 * <p>This interface maps Locality element.</p>
 *
 * <p>Locality is one level lower than adminisstrative area. Eg.: cities, reservations and any other built-up areas.</p>
 *
 * <br />&lt;xs:element name="Locality">
 * <br />&lt;xs:annotation>
 * <br />&lt;xs:documentation></xs:documentation>
 * <br />&lt;/xs:annotation>
 * <br />&lt;xs:complexType>
 * <br />&lt;xs:sequence>
 * <br />&lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;xs:element name="LocalityName" minOccurs="0" maxOccurs="unbounded">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:choice minOccurs="0">
 * <br />&lt;xs:element ref="PostBox"/>
 * <br />&lt;xs:element name="LargeMailUser" type="LargeMailUserType">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:element ref="PostOffice"/>
 * <br />&lt;xs:element name="PostalRoute" type="PostalRouteType">...
 * <br />&lt;/xs:element>
 * <br />&lt;/xs:choice>
 * <br />&lt;xs:element ref="Thoroughfare" minOccurs="0"/>
 * <br />&lt;xs:element ref="Premise" minOccurs="0"/>
 * <br />&lt;xs:element name="DependentLocality" type="DependentLocalityType" minOccurs="0">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:element ref="PostalCode" minOccurs="0"/>
 * <br />&lt;xs:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/xs:sequence>
 * <br />&lt;xs:attribute name="Type">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attribute name="UsageType">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attribute name="Indicator">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
 * <br />&lt;/xs:element>
 *
 * @author Samuel Andrés
 */
public interface Locality {

    /**
     *
     * @return
     */
    public List<GenericTypedGrPostal> getAddressLines();

    /**
     *
     * @return
     */
    public List<LocalityName> getLocalityNames();
    
    /*
     * === CHOICE: ===
     */

    /**
     *
     * @return
     */
    public PostBox getPostBox();

    /**
     * <p>Specification of a large mail user address.
     * Examples of large mail users are postal companies,
     * companies in France with a cedex number,
     * hospitals and airports with their own post code.
     * Large mail user addresses do not have a street name
     * with premise name or premise number in countries like Netherlands.
     * But they have a POBox and street also in countries like France.</p>
     *
     * @return
     */
    public LargeMailUser getLargeMailUser();

    /**
     *
     * @return
     */
    public PostOffice getPostOffice();

    /**
     * <p>A Postal van is specific for a route as in Is`rael, Rural route.</p>
     *
     * @return
     */
    public PostalRoute getPostalRoute();
    
    /*
     * === END OF CHOICE ===
     */

    /**
     *
     * @return
     */
    public Thoroughfare getThoroughfare();

    /**
     *
     * @return
     */
    public Premise getPremise();

    /**
     * <p>Dependent localities are Districts within cities/towns,
     * locality divisions, postal divisions of cities,
     * suburbs, etc. DependentLocality is a recursive element,
     * but no nesting deeper than two exists
     * (Locality-DependentLocality-DependentLocality).</p>
     * 
     * @return
     */
    public DependentLocality getDependentLocality();

    /**
     *
     * @return
     */
    public PostalCode getPostalCode();
    
    /*
     * === ATTRIBUTES: ===
     */
    
    /**
     * <p>Possible values not limited to: City, IndustrialEstate, etc.</p>
     * 
     * @return
     */
    public String getType();
    
    /**
     * <p>Postal or Political - Sometimes locations must be distinguished between 
     * postal system, and physical locations as defined by a political system.</p>
     * 
     * @return
     */
    public String getUsageType();
    
    /**
     * <p>Erode (Dist) where (Dist) is the Indicator.</p>
     * 
     * @return
     */
    public String getIndicator();

}
