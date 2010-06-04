package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 * <p>This interface maps LargeMailUserType type.</p>
 *
 * <br />&lt;xs:complexType name="LargeMailUserType">
 * <br />&lt;xs:sequence>
 * <br />&lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;xs:element name="LargeMailUserName" minOccurs="0" maxOccurs="unbounded">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:element name="LargeMailUserIdentifier" minOccurs="0">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:element name="BuildingName" type="BuildingNameType" minOccurs="0" maxOccurs="unbounded">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:element ref="Department" minOccurs="0"/>
 * <br />&lt;xs:element ref="PostBox" minOccurs="0"/>
 * <br />&lt;xs:element ref="Thoroughfare" minOccurs="0"/>
 * <br />&lt;xs:element ref="PostalCode" minOccurs="0"/>
 * <br />&lt;xs:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/xs:sequence>
 * <br />&lt;xs:attribute name="Type" type="xs:string"/>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
 *
 * @author Samuel Andrés
 */
public interface LargeMailUser {

    /**
     *
     * @return
     */
    public List<GenericTypedGrPostal> getAddressLines();

    /**
     * <p>Name of the large mail user. eg. Smith Ford International airport.</p>
     *
     * @return
     */
    public List<LargeMailUserName> getLargeMailUserNames();

    /**
     * <p>Specification of the identification number of a large mail user.
     * An example are the Cedex codes in France.</p>
     *
     * @return
     */
    public LargeMailUserIdentifier getLargeMailUserIdentifier();

    /**
     *
     * @return
     */
    public List<BuildingName> getBuildingNames();

    /**
     *
     * @return
     */
    public Department getDepartment();

    /**
     *
     * @return
     */
    public PostBox getPostBox();

    /**
     *
     * @return
     */
    public Thoroughfare getThoroughfare();

    /**
     *
     * @return
     */
    public PostalCode getPostalCode();

    /**
     * 
     * @return
     */
    public String getType();
}
