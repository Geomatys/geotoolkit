package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps BuildingNameType type.</p>
 *
 * <br />&lt;xs:complexType name="BuildingNameType" mixed="true">
 * <br />&lt;xs:attribute name="Type"/>
 * <br />&lt;xs:attribute name="TypeOccurrence">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attributeGroup ref="grPostal"/>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
 *
 * @author Samuel Andrés
 */
public interface BuildingName extends GenericTypedGrPostal {

    /**
     * <p>Occurrence of the building name before/after the type. eg. EGIS BUILDING where name appears before type.</p>
     * 
     * @return
     */
    public AfterBeforeEnum getTypeOccurrence();
}
