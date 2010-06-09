package org.geotoolkit.data.model.xal;

/**
 *
 * <p>This interface maps LargeMailUserIdentifer.</p>
 *
 * <pre>
 * &lt;xs:complexType mixed="true">
 *  &lt;xs:attribute name="Type" type="xs:string">...
 *  &lt;/xs:attribute>
 *  &lt;xs:attribute name="Indicator">...
 *  &lt;/xs:attribute>
 *  &lt;xs:attributeGroup ref="grPostal"/>
 *  &lt;xs:anyAttribute namespace="##other"/>
 * &lt;/xs:complexType>
 * </pre>
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
