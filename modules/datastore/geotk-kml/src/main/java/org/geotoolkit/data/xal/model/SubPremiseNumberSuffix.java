package org.geotoolkit.data.xal.model;

/**
 * <p>This interface maps SubPremiseNumberSuffix type.</p>
 *
 * <pre>
 * &lt;xs:complexType mixed="true">
 *  &lt;xs:attribute name="NumberSuffixSeparator">...
 *  &lt;/xs:attribute>
 *  &lt;xs:attribute name="Type"/>
 *  &lt;xs:attributeGroup ref="grPostal"/>
 *  &lt;xs:anyAttribute namespace="##other"/>
 * &lt;/xs:complexType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface SubPremiseNumberSuffix extends GenericTypedGrPostal {

    /**
     * <p>12-A where 12 is number and A is suffix and "-" is the separator.</p>
     *
     * @return
     */
    public String getNumberSuffixSeparator();
}
