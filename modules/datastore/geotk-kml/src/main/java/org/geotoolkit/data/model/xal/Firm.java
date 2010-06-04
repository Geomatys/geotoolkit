package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 * <p>This interface maps FirmType type.</p>
 *
 * <br />&lt;xs:complexType name="FirmType">
 * <br />&lt;xs:sequence>
 * <br />&lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;xs:element name="FirmName" minOccurs="0" maxOccurs="unbounded">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:element ref="Department" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;xs:element name="MailStop" type="MailStopType" minOccurs="0">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:element ref="PostalCode" minOccurs="0"/>
 * <br />&lt;xs:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/xs:sequence>
 * <br />&lt;xs:attribute name="Type"/>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
 *
 * @author Samuel Andrés
 */
public interface Firm {

    /**
     * 
     * @return
     */
    public List<GenericTypedGrPostal> getAddressLines();

    /**
     * <p>Name of the firm.</p>
     *
     * @return
     */
    public List<GenericTypedGrPostal> getFirmNames();

    /**
     *
     * @return
     */
    public List<Department> getDepartments();

    /**
     * <p>A MailStop is where the the mail is delivered to within a premise/subpremise/firm or a facility.</p>
     *
     * @return
     */
    public MailStop getMailStop();

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