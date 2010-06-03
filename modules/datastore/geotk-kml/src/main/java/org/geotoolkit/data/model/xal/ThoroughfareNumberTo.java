package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 * <p>This interface maps ThoroughfareNumberTo element.</p>
 *
 * <p>Ending number in the range.</p>
 *
 * <br />&lt;xs:element name="ThoroughfareNumberTo">
 * <br />&lt;xs:complexType mixed="true">
 * <br />&lt;xs:sequence>
 * <br />&lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;xs:element ref="ThoroughfareNumberPrefix" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;xs:element ref="ThoroughfareNumber" maxOccurs="unbounded"/>
 * <br />&lt;xs:element ref="ThoroughfareNumberSuffix" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/xs:sequence>
 * <br />&lt;xs:attributeGroup ref="grPostal"/>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
 * <br />&lt;/xs:element>
 *
 * @author Samuel Andrés
 */
public interface ThoroughfareNumberTo {

    /**
     * <p>The content list elements have to be AddressLine,ThoroughfareNumberPrefix,
     * ThoroughfareNumber or ThoroughfareNumberSuffix instances.</p>
     *
     * @return
     */
    public List<Object> getContent();

    /**
     * 
     * @return
     */
    public GrPostal getGrPostal();
}
