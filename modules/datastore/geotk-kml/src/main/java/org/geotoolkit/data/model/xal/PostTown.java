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
 * <pre>
 * &lt;xs:element name="PostTown" minOccurs="0">
 *  &lt;xs:complexType>
 *      &lt;xs:sequence>
 *          &lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;xs:element name="PostTownName" minOccurs="0" maxOccurs="unbounded">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="PostTownSuffix" minOccurs="0">...
 *          &lt;/xs:element>
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
    public PostTownSuffix getPostTownSuffix();

    /**
     * 
     * @return
     */
    public String getType();
}
