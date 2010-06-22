package org.geotoolkit.data.xal.model;

import java.util.List;

/**
 * <p>This interface maps xAL element.</p>
 *
 * <p>Root element for a list of addresses.</p>
 *
 * <pre>
 * &lt;xs:element name="xAL">
 *  &lt;xs:complexType>
 *      &lt;xs:sequence>
 *          &lt;xs:element ref="AddressDetails" maxOccurs="unbounded"/>
 *          &lt;xs:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;/xs:sequence>
 *      &lt;xs:attribute name="Version">...
 *      &lt;/xs:attribute>
 *      &lt;xs:anyAttribute namespace="##other"/>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Xal {

    /**
     *
     * @return
     */
    public List<AddressDetails> getAddressDetails();
    
    /**
     * <p>Specific to DTD to specify the version number of DTD.</p>
     * 
     * @return
     */
    public String getVersion();
}
