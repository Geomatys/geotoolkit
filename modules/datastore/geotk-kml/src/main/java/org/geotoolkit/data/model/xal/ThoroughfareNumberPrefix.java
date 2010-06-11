package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps ThoroughfareNumberPrefix element.</p>
 *
 * <p>Prefix before the number. A in A12 Archer Street.</p>
 *
 * <pre>
 * &lt;xs:element name="ThoroughfareNumberPrefix">
 *  &lt;xs:complexType mixed="true">
 *      &lt;xs:attribute name="NumberPrefixSeparator"/>
 *      &lt;xs:attribute name="Type"/>
 *      &lt;xs:attributeGroup ref="grPostal"/>
 *      &lt;xs:anyAttribute namespace="##other"/>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface ThoroughfareNumberPrefix extends GenericTypedGrPostal {

    /**
     * <p>A-12 where 12 is number and A is prefix and "-" is the separator.</p>
     * 
     * @return
     */
    public String getNumberPrefixSeparator();
}
