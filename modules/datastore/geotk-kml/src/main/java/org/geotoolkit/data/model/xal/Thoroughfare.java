package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 * <p></p>
 *
 * <p>Specification of a thoroughfare. A thoroughfare could be a rd, street, canal, river, etc.
 * Note dependentlocality in a street. For example, in some countries, a large street will
 * have many subdivisions with numbers. Normally the subdivision name is the same as the road name,
 * but with a number to identifiy it. Eg. SOI SUKUMVIT 3, SUKUMVIT RD, BANGKOK.</p>
 *
 * <br />&lt;xs:element name="Thoroughfare">
 * <br />&lt;xs:complexType>
 * <br />&lt;xs:sequence>
 * <br />&lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;xs:choice minOccurs="0" maxOccurs="unbounded">
 * <br />&lt;xs:element ref="ThoroughfareNumber"/>
 * <br />&lt;xs:element name="ThoroughfareNumberRange">...
 * <br />&lt;/xs:element>
 * <br />&lt;/xs:choice>
 * <br />&lt;xs:element ref="ThoroughfareNumberPrefix" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;xs:element ref="ThoroughfareNumberSuffix" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;xs:element name="ThoroughfarePreDirection" type="ThoroughfarePreDirectionType" minOccurs="0">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:element name="ThoroughfareLeadingType" type="ThoroughfareLeadingTypeType" minOccurs="0">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:element name="ThoroughfareName" type="ThoroughfareNameType" minOccurs="0" maxOccurs="unbounded">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:element name="ThoroughfareTrailingType" type="ThoroughfareTrailingTypeType" minOccurs="0">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:element name="ThoroughfarePostDirection" type="ThoroughfarePostDirectionType" minOccurs="0">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:element name="DependentThoroughfare" minOccurs="0">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:choice minOccurs="0">
 * <br />&lt;xs:element name="DependentLocality" type="DependentLocalityType">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:element ref="Premise"/>
 * <br />&lt;xs:element name="Firm" type="FirmType">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:element ref="PostalCode"/>
 * <br />&lt;/xs:choice>
 * <br />&lt;xs:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/xs:sequence>
 * <br />&lt;xs:attribute name="Type"/>
 * <br />&lt;xs:attribute name="DependentThoroughfares">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attribute name="DependentThoroughfaresIndicator">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attribute name="DependentThoroughfaresConnector">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attribute name="DependentThoroughfaresType">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
 * <br />&lt;/xs:element>
 *
 * @author Samuel Andrés
 */
public interface Thoroughfare {

    /**
     *
     * @return
     */
    public List<GenericTypedGrPostal> getAddressLine();

    /**
     *
     * @return
     */
    public List<ThoroughfareNumber> getThoroughfareNumbers();

    /**
     *
     * @return
     */
    public List<ThoroughfareNumberRange> getThoroughfareNumberRanges();

    /**
     *
     * @return
     */
    public List<ThoroughfareNumberPrefix> getThoroughfareNumberPrefixes();

    /**
     *
     * @return
     */
    public List<ThoroughfareNumberSuffix> getThoroughfareNumberSuffixes();

    /**
     *
     * @return
     */
    public GenericTypedGrPostal getThoroughfarePreDirection();

    /**
     *
     * @return
     */
    public GenericTypedGrPostal getThoroughfareLeadingType();

    /**
     * <p>Specification of the name of a Thoroughfare (also dependant street name): street name, canal name, etc.</p>
     * 
     * @return
     */
    public List<GenericTypedGrPostal> getThoroughfareNames();

    /**
     *
     * @return
     */
    public GenericTypedGrPostal getfPostThoroughfareTrailingType();

    /**
     *
     * @return
     */
    public GenericTypedGrPostal getThoroughfarePostDirection();

    /**
     *
     * @return
     */
    public DependentThoroughfare getDependentThoroughfare();

    /**
     * === CHOICE: ===
     */

    /**
     *
     * @return
     */
    public DependentLocality getDependentLocality();

    /**
     *
     * @return
     */
    public Premise getPremises();

    /**
     *
     * @return
     */
    public Firm getFirm();

    /**
     * 
     * @return
     */
    public PostalCode getPostalCode();

    /*
     * === END OF CHOICE ===
     *
     * === ATTRIBUTES: ===
     */

    /**
     *
     * @return
     */
    public String getType();

    /**
     * <p>Does this thoroughfare have a a dependent thoroughfare? Corner of street X, etc.</p>
     *
     * @return
     */
    public DependentThoroughfares getDependentThoroughfares();

    /**
     * <p>Corner of, Intersection of.</p>
     *
     * @return
     */
    public String getDependentThoroughfaresIndicator();

    /**
     * <p>Corner of Street1 AND Street 2 where AND is the Connector.</p>
     *
     * @return
     */
    public String getDependentThoroughfaresConnector();
		
    /**
     * <p>TS in GEORGE and ADELAIDE STS, RDS IN A and B RDS, etc. Use only when both the street types are the same.</p>
     * 
     * @return
     */
    public String getDependentThoroughfaresType();
			
}
