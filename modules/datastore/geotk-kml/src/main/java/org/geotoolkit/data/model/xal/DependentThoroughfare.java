/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 * <p>This interface maps Dependent Thoroughfare element.</p>
 *
 * <p>DependentThroughfare is related to a street; occurs in GB, IE, ES, PT.</p>
 *
 * <br />&lt;xs:element name="DependentThoroughfare" minOccurs="0">
 * <br />&lt;xs:complexType>
 * <br />&lt;xs:sequence>
 * <br />&lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
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
 * <br />&lt;xs:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/xs:sequence>
 * <br />&lt;xs:attribute name="Type"/>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
 * <br />&lt;/xs:element>
 * 
 * @author Samuel Andrés
 */
public interface DependentThoroughfare {

    /**
     *
     * @return
     */
    public List<AddressLine> getAddressLines();

    /**
     * <p>North Baker Street, where North is the pre-direction.
     * The direction appears before the name.</p>
     *
     * @return
     */
    public GenericTypedGrPostal getThoroughfarePreDirection();

    /**
     *
     * <p>Appears before the thoroughfare name.
     * Ed. Spanish: Avenida Aurora, where Avenida is the leading type /
     * French: Rue Moliere, where Rue is the leading type.</p>
     * 
     * @return
     */
    public GenericTypedGrPostal getThoroughfareLeadingType();

    /**
     * <p>Specification of the name of a Thoroughfare (also dependant street name):
     * street name, canal name, etc.</p>
     *
     * @return
     */
    public List<GenericTypedGrPostal> getThoroughfareNames();

    /**
     * <p>Appears after the thoroughfare name.
     * Ed. British: Baker Lane, where Lane is the trailing type.</p>
     *
     * @return
     */
    public GenericTypedGrPostal getfPostThoroughfareTrailingType();

    /**
     * <p>221-bis Baker Street North, where North is the post-direction.
     * The post-direction appears after the name.</p>
     * 
     * @return
     */
    public GenericTypedGrPostal getThoroughfarePostDirection();

    /**
     *
     * @return
     */
    public String getType();
}
