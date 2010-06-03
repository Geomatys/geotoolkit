package org.geotoolkit.data.model.xal;

/**
 *
 * <p>This interface maps LargeMailUserIdentifer.</p>
 *
 * <br />&lt;xs:complexType mixed="true">
 * <br />&lt;xs:attribute name="Type" type="xs:string">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attribute name="Indicator">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attributeGroup ref="grPostal"/>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
 *
 * @author Samuel Andrés
 */
public interface LargeMailUserIdentifier extends GenericTypedGrPostal {

    /**
     * <p>eg. Building 429 in which Building is the Indicator.</p>
     * 
     * @return
     */
    public String getIndicator();
}
