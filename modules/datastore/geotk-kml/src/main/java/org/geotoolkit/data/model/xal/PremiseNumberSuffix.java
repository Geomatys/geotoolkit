package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps premiseNumberSuffix element.</p>
 *
 * <p>A in 12A</p>
 *
 * <pre>
 * &lt;xs:element name="PremiseNumberSuffix">
 *  &lt;xs:complexType mixed="true">
 *      &lt;xs:attribute name="NumberSuffixSeparator">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="Type"/>
 *      &lt;xs:attributeGroup ref="grPostal"/>
 *      &lt;xs:anyAttribute namespace="##other"/>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
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
