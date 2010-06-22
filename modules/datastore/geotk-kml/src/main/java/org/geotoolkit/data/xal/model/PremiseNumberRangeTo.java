package org.geotoolkit.data.xal.model;

import java.util.List;

/**
 * <p>End number details of the premise number range.</p>
 *
 * <pre>
 * &lt;xs:element name="PremiseNumberRangeTo">
 *  &lt;xs:complexType>
 *      &lt;xs:sequence>
 *          &lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;xs:element ref="PremiseNumberPrefix" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;xs:element ref="PremiseNumber" maxOccurs="unbounded"/>
 *          &lt;xs:element ref="PremiseNumberSuffix" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;/xs:sequence>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface PremiseNumberRangeTo {

    /**
     *
     * @return
     */
    public List<GenericTypedGrPostal> getAddressLines();

    /**
     *
     * @return
     */
    public List<PremiseNumberPrefix> getPremiseNumberPrefixes();

    /**
     *
     * @return
     */
    public List<PremiseNumber> getPremiseNumbers();

    /**
     * 
     * @return
     */
    public List<PremiseNumberSuffix> getPremiseNumberSuffixes();
}
