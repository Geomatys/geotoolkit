package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 * <p>This interface maps PostalRouteType type.</p>
 *
 * <br />&lt;xs:complexType name="PostalRouteType">
 * <br />&lt;xs:sequence>
 * <br />&lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;xs:choice>
 * <br />&lt;xs:element name="PostalRouteName" maxOccurs="unbounded">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:element name="PostalRouteNumber">...
 * <br />&lt;/xs:element>
 * <br />&lt;/xs:choice>
 * <br />&lt;xs:element ref="PostBox" minOccurs="0"/>
 * <br />&lt;xs:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;xs:sequence>
 * <br />&lt;xs:attribute name="Type"/>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
 *
 * @author Samuel Andrés
 */
public interface PostalRoute {

    public List<GenericTypedGrPostal> getAddressLines();

    /*
     * === CHOICE: ===
     */

    /**
     * <p>Name of the Postal Route.</p>
     *
     * @return
     */
    public List<GenericTypedGrPostal> getPostalRouteName();

    /**
     * <p>Number of the Postal Route.</p>
     *
     * @return
     */
    public PostalRouteNumber getPostalRouteNumber();

    /*
     * === END OF CHOICE ===
     */

    public PostBox getPostBox();

    /*
     * === ATTRIBUTES ===
     */

    public String getType();
}
