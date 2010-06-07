package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 * <p>This interface maps AdministrativeArea element.</p>
 *
 * <p>Examples of administrative areas are provinces counties, special regions (such as "Rijnmond"), etc.</p>
 *
 * <pre>
 * &lt;xs:element name="AdministrativeArea">
 *  &lt;xs:complexType>
 *      &lt;xs:sequence>
 *          &lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;xs:element name="AdministrativeAreaName" minOccurs="0" maxOccurs="unbounded">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="SubAdministrativeArea" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:choice minOccurs="0">
 *              &lt;xs:element ref="Locality"/>
 *              &lt;xs:element ref="PostOffice"/>
 *              &lt;xs:element ref="PostalCode"/>
 *          &lt;/xs:choice>
 *          &lt;xs:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;/xs:sequence>
 *      &lt;xs:attribute name="Type">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="UsageType">...
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
public interface AdministrativeArea {

    /**
     *
     * @return
     */
    public List<GenericTypedGrPostal> getAddressLines();

    /**
     *
     * @return
     */
    public List<GenericTypedGrPostal> getAdministrativeAreaNames();

    /**
     *
     * @return
     */
    public SubAdministrativeArea getSubAdministrativeArea();

    /*
     * === CHOICE: ===
     */

    /**
     *
     * @return
     */
    public Locality getLocality();

    /**
     *
     * @return
     */
    public PostOffice getPostOffice();

    /**
     *
     * @return
     */
    public PostalCode getPostalCode();

    /*
     * === END OF CHOICE ===
     *
     * === ATTRIBUTES: ===
     */

    /**
     * <p>Province or State or County or Kanton, etc.</p>
     *
     * @return
     */
    public String getType();

    /**
     * <p>ostal or Political - Sometimes locations must be distinguished between postal system,
     * and physical locations as defined by a political system.</p>
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