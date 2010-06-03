package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps ThoroughfareNumberSuffix element.</p>
 *
 * <p>Suffix after the number. A in 12A Archer Street.</p>
 *
 * <br />&ltxs:element name="ThoroughfareNumberSuffix">
 * <br />&ltxs:complexType mixed="true">
 * <br />&ltxs:attribute name="NumberSuffixSeparator">
 * <br />&lt/xs:attribute>
 * <br />&ltxs:attribute name="Type"/>
 * <br />&ltxs:attributeGroup ref="grPostal"/>
 * <br />&ltxs:anyAttribute namespace="##other"/>
 * <br />&lt/xs:complexType>
 * <br />&lt/xs:element>
 *
 * @author Samuel Andrés
 */
public interface ThoroughfareNumberSuffix {

    /**
     * <p>NEAR, ADJACENT TO, etc</p>
     * <p>12-A where 12 is number and A is suffix and "-" is the separator</p>
     * 
     * @return
     */
    public String getNumberSuffixSeparator();
}
