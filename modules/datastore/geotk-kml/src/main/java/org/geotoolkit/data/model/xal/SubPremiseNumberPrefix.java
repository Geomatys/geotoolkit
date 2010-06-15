package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps SubPremiseNumberPrefix type.</p>
 *
 * <pre>
 * &lt;xs:complexType mixed="true">
 *  &lt;xs:attribute name="NumberPrefixSeparator">...
 *  &lt;/xs:attribute>
 *  &lt;xs:attribute name="Type"/>
 *  &lt;xs:attributeGroup ref="grPostal"/>
 *  &lt;xs:anyAttribute namespace="##other"/>
 * &lt;/xs:complexType>
 * </pre>
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
