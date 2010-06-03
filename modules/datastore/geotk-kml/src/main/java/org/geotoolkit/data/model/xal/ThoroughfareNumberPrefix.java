package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps ThoroughfareNumberPrefix element.</p>
 *
 * <p>Prefix before the number. A in A12 Archer Street.</p>
 *
 * <br />&lt;xs:element name="ThoroughfareNumberPrefix">
 * <br />&lt;xs:complexType mixed="true">
 * <br />&lt;xs:attribute name="NumberPrefixSeparator"/>
 * <br />&lt;xs:attribute name="Type"/>
 * <br />&lt;xs:attributeGroup ref="grPostal"/>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
 * <br />&lt;/xs:element>
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
