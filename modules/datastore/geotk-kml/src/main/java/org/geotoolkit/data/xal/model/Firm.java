package org.geotoolkit.data.xal.model;

import java.util.List;

/**
 * <p>This interface maps FirmType type.</p>
 *
 * <pre>
 * &lt;xs:complexType name="FirmType">
 *  &lt;xs:sequence>
 *      &lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;xs:element name="FirmName" minOccurs="0" maxOccurs="unbounded">...
 *      &lt;/xs:element>
 *      &lt;xs:element ref="Department" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;xs:element name="MailStop" type="MailStopType" minOccurs="0">...
 *      &lt;/xs:element>
 *      &lt;xs:element ref="PostalCode" minOccurs="0"/>
 *      &lt;xs:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 *  &lt;/xs:sequence>
 *  &lt;xs:attribute name="Type"/>
 *  &lt;xs:anyAttribute namespace="##other"/>
 * &lt;/xs:complexType>
 * </pre>
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