package org.geotoolkit.data.xal.model;

/**
 * <p>This interface maps PremiseLocation element.</p>
 *
 * <p>LOBBY, BASEMENT, GROUND FLOOR, etc...</p>
 *
 * <pre>
 * &lt;xs:element name="PremiseLocation">
 *  &lt;xs:complexType mixed="true">
 *      &lt;xs:attributeGroup ref="grPostal"/>
 *      &lt;xs:anyAttribute namespace="##other"/>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
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
