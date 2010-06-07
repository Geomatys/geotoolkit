package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps BuildingNameType type.</p>
 *
 * <pre>
 * &lt;xs:complexType name="BuildingNameType" mixed="true">
 *  &lt;xs:attribute name="Type"/>
 *  &lt;xs:attribute name="TypeOccurrence">...
 *  &lt;/xs:attribute>
 *  &lt;xs:attributeGroup ref="grPostal"/>
 *  &lt;xs:anyAttribute namespace="##other"/>
 * &lt;/xs:complexType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface BuildingName extends GenericTypedGrPostal {

    /**
     * <p>Occurrence of the building name before/after the type.
     * eg. EGIS BUILDING where name appears before type.</p>
     * 
     * @return
     */
    public AfterBeforeEnum getTypeOccurrence();
}
