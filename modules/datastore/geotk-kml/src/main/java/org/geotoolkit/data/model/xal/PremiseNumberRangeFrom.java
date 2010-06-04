package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 * <p>Start number details of the premise number range.</p>;
 *
 * <br />&lt;xs:element name="PremiseNumberRangeFrom">
 * <br />&lt;xs:complexType>
 * <br />&lt;xs:sequence>
 * <br />&lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;xs:element ref="PremiseNumberPrefix" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;xs:element ref="PremiseNumber" maxOccurs="unbounded"/>
 * <br />&lt;xs:element ref="PremiseNumberSuffix" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/xs:sequence>
 * <br />&lt;/xs:complexType>
 * <br />&lt;/xs:element>
 *
 * @author Samuel Andrés
 */
public interface PremiseNumberRangeFrom {

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
    public List<PremiseNumber> getPremiseNUmber();

    /**
     * 
     * @return
     */
    public List<PremiseNumberSuffix> getPremiseNumberSuffixes();
}
