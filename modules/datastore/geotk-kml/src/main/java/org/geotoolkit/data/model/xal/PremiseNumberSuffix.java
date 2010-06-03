package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps premiseNumberSuffix element.</p>
 *
 * <p>A in 12A</p>
 *
 * <br />&lt;xs:element name="PremiseNumberSuffix">
 * <br />&lt;xs:complexType mixed="true">
 * <br />&lt;xs:attribute name="NumberSuffixSeparator">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attribute name="Type"/>
 * <br />&lt;xs:attributeGroup ref="grPostal"/>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
 * <br />&lt;/xs:element>
 * 
 * @author Samuel Andrés
 */
public interface PremiseNumberSuffix extends GenericTypedGrPostal {

    /**
     * <p>12-A where 12 is number and A is suffix and "-" is the separator.</p>
     * 
     * @return
     */
    public String getNumberSuffixSeparator();
}
