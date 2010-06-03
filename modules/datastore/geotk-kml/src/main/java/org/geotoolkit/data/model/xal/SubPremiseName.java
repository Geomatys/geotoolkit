package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps SubPremiseName type.</p>
 *
 * <br />&lt;xs:complexType mixed="true">
 * <br />&lt;xs:attribute name="Type"/>
 * <br />&lt;xs:attribute name="TypeOccurrence">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attributeGroup ref="grPostal"/>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
 *
 * @author Samuel Andrés
 */
public interface SubPremiseName extends GenericTypedGrPostal {

    /**
     * <p>EGIS Building where EGIS occurs before Building.</p>
     * 
     * @return
     */
    public AfterBeforeEnum getTypeOccurrence();
}
