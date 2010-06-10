package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps PremiseNae element.</p>
 *
 * <p>Specification of the name of the premise (house, building, park, farm, etc).
 * A premise name is specified when the premise cannot be addressed
 * using a street name plus premise (house) number.</p>
 *
 * <pre>
 * &lt;xs:complexType mixed="true">
 *  &lt;xs:attribute name="Type"/>
 *  &lt;xs:attribute name="TypeOccurrence">
 *  &lt;xs:simpleType>
 *      &lt;xs:restriction base="xs:NMTOKEN">
 *          &lt;xs:enumeration value="Before"/>
 *          &lt;xs:enumeration value="After"/>
 *      &lt;/xs:restriction>
 *  &lt;/xs:simpleType>
 *  &lt;/xs:attribute>
 *  &lt;xs:attributeGroup ref="grPostal"/>
 *  &lt;xs:anyAttribute namespace="##other"/>
 * &lt;/xs:complexType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface PremiseName extends GenericTypedGrPostal {

    /**
     * <p>EGIS Building where EGIS occurs before Building, DES JARDINS occurs after COMPLEXE DES JARDINS.</p>
     * 
     * @return
     */
    public AfterBeforeEnum getTypeOccurrence();

}
