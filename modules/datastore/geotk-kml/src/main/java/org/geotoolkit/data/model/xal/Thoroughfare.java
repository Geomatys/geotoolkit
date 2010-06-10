package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 * <p>This interface maps Thoroughfare element.</p>
 *
 * <p>Specification of a thoroughfare. A thoroughfare could be a rd, street, canal, river, etc.
 * Note dependentlocality in a street. For example, in some countries, a large street will
 * have many subdivisions with numbers. Normally the subdivision name is the same as the road name,
 * but with a number to identifiy it. Eg. SOI SUKUMVIT 3, SUKUMVIT RD, BANGKOK.</p>
 *
 * <pre>
 * &lt;xs:element name="Thoroughfare">
 *  &lt;xs:complexType>
 *      &lt;xs:sequence>
 *          &lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;xs:choice minOccurs="0" maxOccurs="unbounded">
 *                  &lt;xs:element ref="ThoroughfareNumber"/>
 *                  &lt;xs:element name="ThoroughfareNumberRange">...
 *                  &lt;/xs:element>
 *              &lt;/xs:choice>
 *          &lt;xs:element ref="ThoroughfareNumberPrefix" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;xs:element ref="ThoroughfareNumberSuffix" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;xs:element name="ThoroughfarePreDirection" type="ThoroughfarePreDirectionType" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="ThoroughfareLeadingType" type="ThoroughfareLeadingTypeType" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="ThoroughfareName" type="ThoroughfareNameType" minOccurs="0" maxOccurs="unbounded">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="ThoroughfareTrailingType" type="ThoroughfareTrailingTypeType" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="ThoroughfarePostDirection" type="ThoroughfarePostDirectionType" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="DependentThoroughfare" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:choice minOccurs="0">
 *              &lt;xs:element name="DependentLocality" type="DependentLocalityType">...
 *              &lt;/xs:element>
 *              &lt;xs:element ref="Premise"/>
 *              &lt;xs:element name="Firm" type="FirmType">...
 *              &lt;/xs:element>
 *              &lt;xs:element ref="PostalCode"/>
 *          &lt;/xs:choice>
 *          &lt;xs:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;/xs:sequence>
 *      &lt;xs:attribute name="Type"/>
 *      &lt;xs:attribute name="DependentThoroughfares">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="DependentThoroughfaresIndicator">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="DependentThoroughfaresConnector">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="DependentThoroughfaresType">...
 *      &lt;/xs:attribute>
 *      &lt;xs:anyAttribute namespace="##other"/>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
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
    public List<Object> getThoroughfareNumbers();

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
    public GenericTypedGrPostal getThoroughfareTrailingType();

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
