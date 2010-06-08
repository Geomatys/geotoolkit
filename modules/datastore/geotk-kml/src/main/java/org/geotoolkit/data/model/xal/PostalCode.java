package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 * <p>This interface maps PostalCode element.</p>
 *
 * <p>PostalCode is the container element for either simple or complex (extended) postal codes.
 * Type: Area Code, Postcode, etc.</p>
 *
 * <pre>
 * &lt;xs:element name="PostalCode">
 *  &lt;xs:complexType>
 *      &lt;xs:sequence>
 *          &lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;xs:element name="PostalCodeNumber" minOccurs="0" maxOccurs="unbounded">
 *          &lt;/xs:element>
 *          &lt;xs:element name="PostalCodeNumberExtension" minOccurs="0" maxOccurs="unbounded">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="PostTown" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;/xs:sequence>
 *      &lt;xs:attribute name="Type">...
 *      &lt;/xs:attribute>
 *      anyAttribute namespace="##other"/>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
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
    public List<PostalCodeNumberExtension> getPostalCodeNumberExtensions();

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
