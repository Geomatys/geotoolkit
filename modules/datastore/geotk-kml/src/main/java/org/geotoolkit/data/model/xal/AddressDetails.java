package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps AddressDetails type.</p>
 *
 * <p>This container defines the details of the address.
 * Can define multiple addresses including tracking address history.</p>
 *
 * <br />&lt;xs:element name="AddressDetails" type="AddressDetails">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:complexType name="AddressDetails">
 * <br />&lt;xs:sequence>
 * <br />&lt;xs:element name="PostalServiceElements" minOccurs="0">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:choice minOccurs="0">
 * <br />&lt;xs:element name="Address">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:element name="AddressLines" type="AddressLinesType">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:element name="Country">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:element ref="AdministrativeArea"/>
 * <br />&lt;xs:element ref="Locality"/>
 * <br />&lt;xs:element ref="Thoroughfare"/>
 * <br />&lt;/xs:choice>
 * <br />&lt;xs:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/xs:sequence>
 * <br />&lt;xs:attribute name="AddressType">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attribute name="CurrentStatus">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attribute name="ValidFromDate">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attribute name="ValidToDate">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attribute name="Usage">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attributeGroup ref="grPostal"/>
 * <br />&lt;xs:attribute name="AddressDetailsKey">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
 *
 * @author Samuel Andrés
 */
public interface AddressDetails {

    /**
     *
     * @return
     */
    public PostalServiceElements getPostalServiceElements();

    /*
     * === CHOICE: ===
     * Use the most suitable option. Country contains the most detailed
     information while Locality is missing Country and AdminArea.
     */

    /**
     *
     * @return
     */
    public GenericTypedGrPostal getAddress();

    /**
     *
     * @return
     */
    public AddressLines getAddressLines();

    /**
     *
     * @return
     */
    public Country getCountry();

    /**
     *
     * @return
     */
    public AdministrativeArea getAdministrativeArea();

    /**
     *
     * @return
     */
    public Locality getLocality();

    /**
     *
     * @return
     */
    public Thoroughfare getThoroughfare();

    /*
     * === END OF THE CHOICE ===
     *
     * === ATTRIBUTES: ===
     */

    /**
     * <p>Type of address. Example: Postal, residential,business, primary, secondary, etc.</p>
     * 
     * @return
     */
    public String getAddressType();

    /**
     * <p>Moved, Living, Investment, Deceased, etc...</p>
     * @return
     */
    public String getCurrentStatus();

    /**
     * <p>Start Date of the validity of address</p>
     *
     * @return
     */
    public String getValidFromDate();

    /**
     * <p>End date of the validity of address</p>
     *
     * @return
     */
    public String getValidToDate();

    /**
     * <p>Communication, Contact, etc.</p>
     * 
     * @return
     */
    public String getUsage();

    /**
     *
     * @return
     */
    public GrPostal getGrPostal();

    /**
     * <p>Key identifier for the element for not reinforced references
     * from other elements. Not required to be unique for the document
     * to be valid, but application may get confused if not unique.
     * Extend this schema adding unique contraint if needed.</p>
     *
     * @return
     */
    public String getAddressDetailsKey();
    
}
