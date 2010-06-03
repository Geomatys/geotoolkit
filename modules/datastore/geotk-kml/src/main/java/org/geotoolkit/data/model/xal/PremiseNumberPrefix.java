package org.geotoolkit.data.model.xal;

/**
 *
 * <p>This interface maps PremiseNumberPrefix element.</p>
 *
 * <p>A in A12</p>
 *
 * <br />&lt;xs:element name="PremiseNumberPrefix">
 * <br />&lt;xs:complexType>
 * <br />&lt;xs:simpleContent>
 * <br />&lt;xs:extension base="xs:string">
 * <br />&lt;xs:attribute name="NumberPrefixSeparator">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attribute name="Type"/>
 * <br />&lt;xs:attributeGroup ref="grPostal"/>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:extension>
 * <br />&lt;/xs:simpleContent>
 * <br />&lt;/xs:complexType>
 * <br />&lt;/xs:element>
 *
 * @author Samuel Andrés
 */
public interface PremiseNumberPrefix extends GenericTypedGrPostal {

    /**
     * <p>A-12 where 12 is number and A is prefix and "-" is the separator.</p>
     *
     * @return
     */
    public String getNumberPrefixSeparator();

}
