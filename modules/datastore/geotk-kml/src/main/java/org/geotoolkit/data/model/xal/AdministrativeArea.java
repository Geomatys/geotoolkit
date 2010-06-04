package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 * <p>This interface maps AdministrativeArea element.</p>
 *
 * <p>Examples of administrative areas are provinces counties, special regions (such as "Rijnmond"), etc.</p>
 *
 * <br />&lt;xs:element name="AdministrativeArea">
 * <br />&lt;xs:complexType>
 * <br />&lt;xs:sequence>
 * <br />&lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;xs:element name="AdministrativeAreaName" minOccurs="0" maxOccurs="unbounded">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:element name="SubAdministrativeArea" minOccurs="0">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:choice minOccurs="0">
 * <br />&lt;xs:element ref="Locality"/>
 * <br />&lt;xs:element ref="PostOffice"/>
 * <br />&lt;xs:element ref="PostalCode"/>
 * <br />&lt;/xs:choice>
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
    public List<AdministrativeAreaName> getAdministrativeAreaNames();

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