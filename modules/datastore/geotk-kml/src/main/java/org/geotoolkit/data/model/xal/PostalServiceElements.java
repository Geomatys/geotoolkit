package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 * <p>This interface maps PostalServiceElements element.</p>
 *
 * <p>Postal authorities use specific postal service data to expedient delivery of mail.</p>
 *
 * <pre>
 * &lt;xs:element name="PostalServiceElements" minOccurs="0">
 *  &lt;xs:complexType>
 *      &lt;xs:sequence>
 *          &lt;xs:element name="AddressIdentifier" minOccurs="0" maxOccurs="unbounded">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="EndorsementLineCode" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="KeyLineCode" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="Barcode" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="SortingCode" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="AddressLatitude" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="AddressLatitudeDirection" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="AddressLongitude" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="AddressLongitudeDirection" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="SupplementaryPostalServiceData" minOccurs="0" maxOccurs="unbounded">...
 *          &lt;/xs:element>
 *          &lt;xs:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;/xs:sequence>
 *      &lt;xs:attribute name="Type">...
 *      &lt;/xs:attribute>
 *      &lt;xs:anyAttribute namespace="##other"/>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface PostalServiceElements {

    /**
     *
     * @return
     */
    public List<AddressIdentifier> getAddressIdentifiers();

    /**
     * <p>Directly affects postal service distribution.</p>
     *
     * @return
     */
    public GenericTypedGrPostal getEndorsementLineCode();

    /**
     * <p>Required for some postal services</p>
     *
     * @return
     */
    public GenericTypedGrPostal getKeyLineCode();

    /**
     * <p>Required for some postal services.</p>
     *
     * @return
     */
    public GenericTypedGrPostal getBarcode();

    /**
     *
     * @return
     */
    public SortingCode getSortingCode();

    /**
     * <p>Latitude of delivery address.</p>
     *
     * @return
     */
    public GenericTypedGrPostal getAddressLatitude();

    /**
     * <p>Latitude direction of delivery address;N = North and S = South.</p>
     *
     * @return
     */
    public GenericTypedGrPostal getAddressLatitudeDirection();

    /**
     * <p>Longtitude of delivery address.</p>
     *
     * @return
     */
    public GenericTypedGrPostal getAddressLongitude();

    /**
     * <p>Longtitude direction of delivery address;N=North and S=South.</p>
     *
     * @return
     */
    public GenericTypedGrPostal getAddressLongitudeDirection();

    /**
     *
     * @return
     */
    public List<GenericTypedGrPostal> getSupplementaryPostalServiceData();

    /**
     * <p>Specific to postal service.</p>
     *
     * @return
     */
    public String getType();
}
