package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps SubPremiseName type.</p>
 *
 * <pre>
 * &lt;xs:complexType mixed="true">
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
public interface SubPremiseName extends GenericTypedGrPostal {

    /**
     * <p>EGIS Building where EGIS occurs before Building.</p>
     * 
     * @return
     */
    public AfterBeforeEnum getTypeOccurrence();
}
