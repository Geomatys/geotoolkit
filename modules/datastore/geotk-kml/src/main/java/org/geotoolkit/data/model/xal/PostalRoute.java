package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 * <p>This interface maps PostalRouteType type.</p>
 *
 * <pre>
 * &lt;xs:complexType name="PostalRouteType">
 *  &lt;xs:sequence>
 *      &lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;xs:choice>
 *          &lt;xs:element name="PostalRouteName" maxOccurs="unbounded">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="PostalRouteNumber">...
 *          &lt;/xs:element>
 *      &lt;/xs:choice>
 *      &lt;xs:element ref="PostBox" minOccurs="0"/>
 *      &lt;xs:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 *  &lt;xs:sequence>
 *  &lt;xs:attribute name="Type"/>
 *  &lt;xs:anyAttribute namespace="##other"/>
 * &lt;/xs:complexType>
 * </pre>
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
    public List<GenericTypedGrPostal> getPostalRouteNames();

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
