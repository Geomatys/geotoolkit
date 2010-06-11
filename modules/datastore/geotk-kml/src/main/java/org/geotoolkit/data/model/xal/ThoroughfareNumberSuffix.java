package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps ThoroughfareNumberSuffix element.</p>
 *
 * <p>Suffix after the number. A in 12A Archer Street.</p>
 *
 * <pre>
 * &ltxs:element name="ThoroughfareNumberSuffix">
 *  &ltxs:complexType mixed="true">
 *      &ltxs:attribute name="NumberSuffixSeparator">
 *      &lt/xs:attribute>
 *      &ltxs:attribute name="Type"/>
 *      &ltxs:attributeGroup ref="grPostal"/>
 *      &ltxs:anyAttribute namespace="##other"/>
 *  &lt/xs:complexType>
 * &lt/xs:element>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface ThoroughfareNumberSuffix extends GenericTypedGrPostal{

    /**
     * <p>NEAR, ADJACENT TO, etc</p>
     * <p>12-A where 12 is number and A is suffix and "-" is the separator</p>
     * 
     * @return
     */
    public String getNumberSuffixSeparator();
}
