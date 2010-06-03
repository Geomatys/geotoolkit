package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 * <p>This interface maps AddressLinesType type.</p>
 *
 * <p>Container for Address lines.</p>
 *
 * <br />&lt;xs:element name="AddressLines" type="AddressLinesType">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:complexType name="AddressLinesType">
 * <br />&lt;xs:sequence>
 * <br />&lt;xs:element ref="AddressLine" maxOccurs="unbounded"/>
 * <br />&lt;xs:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/xs:sequence>
 * <br />&lt;xs:anyAttribute namespace="##other"/><!-- These attributes are not implemented -->
 * <br />&lt;/xs:complexType>
 *
 * @author Samuel Andrés
 */
public interface AddressLines {

    /**
     * 
     * @return
     */
    public List<AddressLine> getAddressLines();

}
