package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 * <p></p>
 *
 * <p>Specification of a country.</p>
 *
 * <br />&lt;xs:element name="Country">
 * <br />&lt;xs:complexType>
 * <br />&lt;xs:sequence>
 * <br />&lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;xs:element name="CountryNameCode" minOccurs="0" maxOccurs="unbounded">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:element ref="CountryName" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;xs:choice minOccurs="0">
 * <br />&lt;xs:element ref="AdministrativeArea"/>
 * <br />&lt;xs:element ref="Locality"/>
 * <br />&lt;xs:element ref="Thoroughfare"/>
 * <br />&lt;/xs:choice>
 * <br />&lt;xs:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/xs:sequence>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
 * <br />&lt;/xs:element>
 *
 * @author Samuel Andrés
 */
public interface Country {

    /**
     *
     * @return
     */
    public List<GenericTypedGrPostal> getAddressLines();

    /**
     *
     * @return
     */
    public List<CountryNameCode> getCountryNameCodes();

    /**
     * <p>Specification of the name of a country.</p>
     *
     * @return
     */
    public List<GenericTypedGrPostal> getCountryNames();

    /*
     * === CHOICE ===
     */

    /**
     *
     * @return
     */
    public AdministrativeArea getAdministrativeArea();

    /**
     *
     * @return
     */
    public Locality getLocality();

    /**
     * 
     * @return
     */
    public Thoroughfare getThoroughfare();

    /*
     * === END OF CHOICE ===
     */
}
