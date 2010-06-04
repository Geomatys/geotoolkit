package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 * <p>This interface maps PostalCode element.</p>
 *
 * <p>PostalCode is the container element for either simple or complex (extended) postal codes.
 * Type: Area Code, Postcode, etc.</p>
 *
 * <br />&lt;xs:element name="PostalCode">
 * <br />&lt;xs:complexType>
 * <br />&lt;xs:sequence>
 * <br />&lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;xs:element name="PostalCodeNumber" minOccurs="0" maxOccurs="unbounded">
 * <br />&lt;/xs:element>
 * <br />&lt;xs:element name="PostalCodeNumberExtension" minOccurs="0" maxOccurs="unbounded">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:element name="PostTown" minOccurs="0">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/xs:sequence>
 * <br />&lt;xs:attribute name="Type">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
 * <br />&lt;/xs:element>
 *
 * @author Samuel Andrés
 */
public interface PostalCode {

    /**
     *
     * @return
     */
    public List<GenericTypedGrPostal> getAddressLines();

    /**
     * <p>Specification of a postcode.
     * The postcode is formatted according to country-specific rules.
     * Example: SW3 0A8-1A, 600074, 2067</p>
     *
     * @return
     */
    public List<GenericTypedGrPostal> getPostalCodeNumbers();

    /**
     *
     * @return
     */
    public List<PostalCodeNumberExtension> getPostalCodenumberExtensions();

    /**
     * 
     * @return
     */
    public PostTown getPostTown();

    /**
     * <p>Area Code, Postcode, Delivery code as in NZ, etc.</p>
     * @return
     */
    public String getType();
}
