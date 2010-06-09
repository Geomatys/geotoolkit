package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps PostalRouteNumber element.</p>
 *
 * <pre>
 * &lt;xs:element name="PostalRouteNumber">
 *  &lt;xs:complexType mixed="true">
 *      &lt;xs:attributeGroup ref="grPostal"/>
 *      &lt;xs:anyAttribute namespace="##other"/>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
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
