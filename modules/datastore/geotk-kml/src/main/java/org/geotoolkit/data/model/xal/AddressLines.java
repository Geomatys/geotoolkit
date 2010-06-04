package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 * <p>This interface maps AddressLinesType type.</p>
 *
 * <p>Container for Address lines.</p>
 *
 * <pre>
 * &lt;xs:element name="AddressLines" type="AddressLinesType">...
 * &lt;/xs:element>
 *
 * &lt;xs:complexType name="AddressLinesType">
 *  &lt;xs:sequence>
 *      &lt;xs:element ref="AddressLine" maxOccurs="unbounded"/>
 *      &lt;xs:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 *  &lt;/xs:sequence>
 *  &lt;xs:anyAttribute namespace="##other"/><!-- These attributes are not implemented -->
 * &lt;/xs:complexType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface AddressLines {

    /**
     * 
     * @return
     */
    public List<GenericTypedGrPostal> getAddressLines();

}
