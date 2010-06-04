package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 * <p>This interface maps MailStopType type.</p>
 *
 * <br />&lt;xs:complexType name="MailStopType">
 * <br />&lt;xs:sequence>
 * <br />&lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;xs:element name="MailStopName" minOccurs="0">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:element name="MailStopNumber" minOccurs="0">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/xs:sequence>
 * <br />&lt;xs:attribute name="Type"/>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
 * 
 * @author Samuel Andrés
 */
public interface MailStop {

    /**
     * 
     * @return
     */
    public List<GenericTypedGrPostal> getAddressLines();

    /**
     * <p>Name of the the Mail Stop. eg. MSP, MS, etc.</p>
     *
     * @return
     */
    public List<GenericTypedGrPostal> getMailStopNames();

    /**
     * <p>Number of the Mail stop. eg. 123 in MS 123.</p>
     * 
     * @return
     */
    public MailStopNumber getMailStopNumber();

    /**
     *
     * @return
     */
    public String getType();

}
