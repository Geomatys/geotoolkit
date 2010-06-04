package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 * <p>This interface maps SubAdministrativeArea element.</p>
 *
 * <p>Specification of a sub-administrative area.
 * An example of a sub-administrative areas is a county.
 * There are two places where the name of an administrative
 * area can be specified and in this case,
 * one becomes sub-administrative area.</p>
 *
 * <br />&lt;xs:element name="SubAdministrativeArea" minOccurs="0">
 * <br />&lt;xs:complexType>
 * <br />&lt;xs:sequence>
 * <br />&lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;xs:element name="SubAdministrativeAreaName" minOccurs="0" maxOccurs="unbounded">...
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
public interface SubAdministrativeArea {

    /**
     *
     * @return
     */
    public List<GenericTypedGrPostal> getAddressLines();

    /**
     *
     * @return
     */
    public List<SubAdministrativeAreaName> getSubadministrativeAreaNames();

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
     * <p>Postal or Political - 
     * Sometimes locations must be distinguished between postal system, 
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
