package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 * <p>This interface maps PostOffice element.</p>
 *
 * <p>Specification of a post office. Examples are a rural post office where
 * post is delivered and a post office containing post office boxes.</p>
 *
 * <br />&lt;xs:element name="PostOffice">
 * <br />&lt;xs:complexType>
 * <br />&lt;xs:sequence>
 * <br />&lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;xs:choice>
 * <br />&lt;xs:element name="PostOfficeName" minOccurs="0" maxOccurs="unbounded">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:element name="PostOfficeNumber" minOccurs="0">...
 * <br />&lt;/xs:element>
 * <br />&lt;/xs:choice>
 * <br />&lt;xs:element name="PostalRoute" type="PostalRouteType" minOccurs="0">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:element ref="PostBox" minOccurs="0"/>
 * <br />&lt;xs:element ref="PostalCode" minOccurs="0"/>
 * <br />&lt;xs:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/xs:sequence>
 * <br />&lt;xs:attribute name="Type">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attribute name="Indicator">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
 * <br />&lt;/xs:element>
 *
 * @author Samuel Andrés
 */
public interface PostOffice {

    /**
     *
     * @return
     */
    public List<GenericTypedGrPostal> getAddressLines();

    /*
     * === CHOICE: ===
     */

    /**
     * <p>Specification of the name of the post office.
     * This can be a rural postoffice where post is delivered
     * or a post office containing post office boxes.</p>
     *
     * @return
     */
    public GenericTypedGrPostal getPostOfficeName();

    /**
     * <p>Specification of the number of the postoffice. Common in rural postoffices.</p>
     *
     * @return
     */
    public PostOfficeNumber getPostOfficeNumber();

    /*
     * === END OF CHOICE ===
     */

    /**
     * <p>A Postal van is specific for a route as in Israel, Rural route.</p>
     *
     * @return
     */
    public PostalRoute getPostalRoute();

    /**
     *
     * @return
     */
    public PostBox getPostBox();

    /**
     *
     * @return
     */
    public PostalCode getPostalCode();

    /*
     * === ATTRIBUTES
     */

    /**
     * <p>Could be a Mobile Postoffice Van as in Isreal.</p>
     * @return
     */
    public String getType();

    /**
     * <p>eg. Kottivakkam (P.O) here (P.O) is the Indicator.</p>
     *
     * @return
     */
    public String getIndicator();

}
