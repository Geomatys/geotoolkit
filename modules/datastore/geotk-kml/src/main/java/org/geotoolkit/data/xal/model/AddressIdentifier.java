package org.geotoolkit.data.xal.model;

/**
 * <p>This interface maps addressIdentifier type.</p>
 *
 * <pre>
 * &lt;xs:complexType mixed="true">
 *  &lt;xs:attribute name="IdentifierType">
 *  &lt;/xs:attribute>
 *  &lt;xs:attribute name="Type"/>
 *  &lt;xs:attributeGroup ref="grPostal"/>
 *  &lt;xs:anyAttribute namespace="##other"/>
 * &lt;/xs:complexType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface AddressIdentifier {

    /**
     *
     * @return
     */
    public String getContent();

    /**
     * <p>Type of identifier. eg. DPID as in Australia.</p>
     *
     * @return
     */
    public String getIdentifierType();

    /**
     *
     * @return
     */
    public String getType();
    
    /**
     *
     * @return
     */
    public GrPostal getGrPostal();

}
