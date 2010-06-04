package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 *
 * <p>This interface maps PostBox element.</p>
 *
 * <p>Specification of a postbox like mail delivery point.
 * Only a single postbox number can be specified.
 * Examples of postboxes are POBox, free mail numbers, etc.</p>
 *
 * <br />&lt;xs:element name="PostBox">
 * <br />&lt;xs:complexType>
 * <br />&lt;xs:sequence>
 * <br />&lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;xs:element name="PostBoxNumber">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:element name="PostBoxNumberPrefix" minOccurs="0">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:element name="PostBoxNumberSuffix" minOccurs="0">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:element name="PostBoxNumberExtension" minOccurs="0">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:element name="Firm" type="FirmType" minOccurs="0">...
 * <br />&lt;/xs:element>
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
public interface PostBox {

    /**
     * 
     * @return
     */
    public List<GenericTypedGrPostal> getAddressLines();

    /**
     *
     * @return
     */
    public PostBoxNumber getPostBoxNumber();

    /**
     *
     * @return
     */
    public PostBoxNumberPrefix getPostBoxNumberPrefix();

    /**
     *
     * @return
     */
    public PostBoxNumberSuffix getPostBoxNumberSuffix();

    /**
     *
     * @return
     */
    public PostBoxNumberExtension getPostBoxNumberExtension();

    /**
     *
     * @return
     */
    public Firm getFirm();

    /**
     *
     * @return
     */
    public PostalCode getPostalCode();

    /**
     * <p>Possible values are, not limited to: POBox and Freepost.</p>
     *
     * @return
     */
    public String getType();

    /**
     * <p>LOCKED BAG NO:1234 where the Indicator is NO: and Type is LOCKED BAG.</p>
     *
     * @return
     */
    public String getIndicator();

}
