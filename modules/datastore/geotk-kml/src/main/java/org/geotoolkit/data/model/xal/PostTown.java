package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 * <p>This interface maps PostTown element.</p>
 *
 * <p>A post town is not the same as a locality.
 * A post town can encompass a collection of (small) localities.
 * It can also be a subpart of a locality.
 * An actual post town in Norway is "Bergen".</p>
 *
 * <br />&lt;xs:element name="PostTown" minOccurs="0">
 * <br />&lt;xs:complexType>
 * <br />&lt;xs:sequence>
 * <br />&lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;xs:element name="PostTownName" minOccurs="0" maxOccurs="unbounded">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:element name="PostTownSuffix" minOccurs="0">...
 * <br />&lt;/xs:element>
 * <br />&lt;/xs:sequence>
 * <br />&lt;xs:attribute name="Type">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
 * <br />&lt;/xs:element>
 *
 * @author Samuel Andrés
 */
public interface PostTown {

    /**
     * 
     * @return
     */
    public List<GenericTypedGrPostal> getAddressLines();

    /**
     * <p>Name of the post town.</p>
     * 
     * @return
     */
    public List<GenericTypedGrPostal> getPostTownNames();

    /**
     * 
     * @return
     */
    public String getType();
}
