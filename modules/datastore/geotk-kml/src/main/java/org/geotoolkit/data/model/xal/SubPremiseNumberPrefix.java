package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps SubPremiseNumberPrefix type.</p>
 *
 * <br />&lt;xs:complexType mixed="true">
 * <br />&lt;xs:attribute name="NumberPrefixSeparator">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attribute name="Type"/>
 * <br />&lt;xs:attributeGroup ref="grPostal"/>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
 *
 * @author Samuel Andrés
 */
public interface SubPremiseNumberPrefix extends GenericTypedGrPostal {

    /**
     * <p>A-12 where 12 is number and A is prefix and "-" is the separator</p>
     * 
     * @return
     */
    public String getNumberPrefixSeparator();
}
