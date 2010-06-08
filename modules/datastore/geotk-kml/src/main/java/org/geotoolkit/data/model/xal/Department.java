package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 * <p>This interface maps Department element.</p>
 *
 * <p>Subdivision in the firm: School of Physics at Victoria University (School of Physics is the department).</p>
 *
 * <pre>
 * &lt;xs:element name="Department">
 *  &lt;xs:complexType>
 *      &lt;xs:sequence>
 *          &lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;xs:element name="DepartmentName" minOccurs="0" maxOccurs="unbounded">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="MailStop" type="MailStopType" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:element ref="PostalCode" minOccurs="0"/>
 *          &lt;xs:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;/xs:sequence>
 *      &lt;xs:attribute name="Type">...
 *      &lt;/xs:attribute>
 *      &lt;xs:anyAttribute namespace="##other"/>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Department {

    /**
     * 
     * @return
     */
    public List<GenericTypedGrPostal> getAddressLines();

    /**
     * <p>Specification of the name of a department.</p>
     *
     * @return
     */
    public List<GenericTypedGrPostal> getDepartmentNames();

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
     * <p>School in Physics School, Division in Radiology division of school of physics.</p>
     *
     * @return
     */
    public String getType();
}
