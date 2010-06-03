package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps LocalityNAme type.</p>
 *
 * <p>Name of the locality.</p>
 *
 * <br />&lt;xs:complexType mixed="true">
 * <br />&lt;xs:attribute name="Type"/>
 * <br />&lt;xs:attributeGroup ref="grPostal"/>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
 * <br />&lt;/xs:element>
 *
 * @author Samuel Andrés
 */
public interface LocalityName extends GenericTypedGrPostal {

}
