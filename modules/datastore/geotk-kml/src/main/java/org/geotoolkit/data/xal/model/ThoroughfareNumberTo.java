package org.geotoolkit.data.xal.model;

import java.util.List;

/**
 * <p>This interface maps ThoroughfareNumberTo element.</p>
 *
 * <p>Ending number in the range.</p>
 *
 * <pre>
 * &lt;xs:element name="ThoroughfareNumberTo">
 *  &lt;xs:complexType mixed="true">
 *      &lt;xs:sequence>
 *          &lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;xs:element ref="ThoroughfareNumberPrefix" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;xs:element ref="ThoroughfareNumber" maxOccurs="unbounded"/>
 *          &lt;xs:element ref="ThoroughfareNumberSuffix" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;/xs:sequence>
 *      &lt;xs:attributeGroup ref="grPostal"/>
 *      &lt;xs:anyAttribute namespace="##other"/>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
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
