package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps PremiseNae element.</p>
 *
 * <p>Specification of the name of the premise (house, building, park, farm, etc).
 * A premise name is specified when the premise cannot be addressed
 * using a street name plus premise (house) number.</p>
 *
 * <br />&lt;xs:complexType mixed="true">
 * <br />&lt;xs:attribute name="Type"/>
 * <br />&lt;xs:attribute name="TypeOccurrence">
 * <br />&lt;xs:simpleType>
 * <br />&lt;xs:restriction base="xs:NMTOKEN">
 * <br />&lt;xs:enumeration value="Before"/>
 * <br />&lt;xs:enumeration value="After"/>
 * <br />&lt;/xs:restriction>
 * <br />&lt;/xs:simpleType>
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attributeGroup ref="grPostal"/>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
 *
 * @author Samuel Andrés
 */
public interface PremiseName extends GenericTypedGrPostal {

    /**
     * <p>EGIS Building where EGIS occurs before Building, DES JARDINS occurs after COMPLEXE DES JARDINS.</p>
     * 
     * @return
     */
    public AfterBeforeEnum getTypeOccurence();

}
