package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps PremiseLocation element.</p>
 *
 * <p>Name of the SubPremise Location. eg. LOBBY, BASEMENT, GROUND FLOOR, etc...</p>
 *
 * <br />&lt;xs:element name="SubPremiseLocation">
 * <br />&lt;xs:complexType mixed="true">
 * <br />&lt;xs:attributeGroup ref="grPostal"/>
 * <br />&lt;/xs:complexType>
 * <br />&lt;/xs:element>
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
