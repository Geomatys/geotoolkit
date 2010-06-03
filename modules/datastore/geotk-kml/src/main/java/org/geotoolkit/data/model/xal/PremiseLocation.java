package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps PremiseLocation element.</p>
 *
 * <p>LOBBY, BASEMENT, GROUND FLOOR, etc...</p>
 *
 * <br />&lt;xs:element name="PremiseLocation">
 * <br />&lt;xs:complexType mixed="true">
 * <br />&lt;xs:attributeGroup ref="grPostal"/>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
 * <br />&lt;/xs:element>
 *
 * @author Samuel Andrés
 */
public interface PremiseLocation {

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
