package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps AddressLine element.</p>
 *
 * <p>Free format address representation. An address can have more than one line.
 * The order of the AddressLine elements must be preserved.</p>
 *
 * <br />&lt;xs:element name="AddressLine">
 * <br />&lt;xs:complexType mixed="true">
 * <br />&lt;xs:attribute name="Type">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attributeGroup ref="grPostal"/>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
 * <br />&lt;/xs:element>
 *
 * @author Samuel Andrés
 */
public interface AddressLine {

    /**
     * <p>Defines the type of address line. eg. Street, Address Line 1, etc.</p>
     *
     * @return
     */
    public String getType();

    /**
     *
     * @return
     */
    public GrPostal getGrPostal();

}
