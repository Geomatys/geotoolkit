package org.geotoolkit.data.xal.model;

import java.util.List;

/**
 * <p>This interface maps PostOffice element.</p>
 *
 * <p>Specification of a post office. Examples are a rural post office where
 * post is delivered and a post office containing post office boxes.</p>
 *
 * <pre>
 * &lt;xs:element name="PostOffice">
 *  &lt;xs:complexType>
 *      &lt;xs:sequence>
 *          &lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;xs:choice>
 *              &lt;xs:element name="PostOfficeName" minOccurs="0" maxOccurs="unbounded">...
 *              &lt;/xs:element>
 *              &lt;xs:element name="PostOfficeNumber" minOccurs="0">...
 *              &lt;/xs:element>
 *          &lt;/xs:choice>
 *          &lt;xs:element name="PostalRoute" type="PostalRouteType" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:element ref="PostBox" minOccurs="0"/>
 *          &lt;xs:element ref="PostalCode" minOccurs="0"/>
 *          &lt;xs:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;/xs:sequence>
 *      &lt;xs:attribute name="Type">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="Indicator">...
 *      &lt;/xs:attribute>
 *      &lt;xs:anyAttribute namespace="##other"/>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
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
    public List<GenericTypedGrPostal> getPostOfficeNames();

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
