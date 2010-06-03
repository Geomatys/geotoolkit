package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps PostalRouteNumber element.</p>
 *
 * <br />&lt;xs:element name="PostalRouteNumber">
 * <br />&lt;xs:complexType mixed="true">
 * <br />&lt;xs:attributeGroup ref="grPostal"/>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
 * <br />&lt;/xs:element>
 *
 * @author Samuel Andrés
 */
public interface PostalRouteNumber {

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
