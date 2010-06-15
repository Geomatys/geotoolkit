package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps PremiseLocation element.</p>
 *
 * <p>Name of the SubPremise Location. eg. LOBBY, BASEMENT, GROUND FLOOR, etc...</p>
 *
 * <pre>
 * &lt;xs:element name="SubPremiseLocation">
 *  &lt;xs:complexType mixed="true">
 *      &lt;xs:attributeGroup ref="grPostal"/>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface SubPremiseLocation {

    /**
     * 
     * @return
     */
    public String getContent();

    /**
     *
     * @return
     */
    public GrPostal getGrPostal();
}
