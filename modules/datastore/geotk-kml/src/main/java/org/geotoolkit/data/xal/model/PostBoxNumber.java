package org.geotoolkit.data.xal.model;

/**
 * <p>This interface maps PostBoxNumber element.</p>
 *
 * <p>Specification of the number of a postbox.</p>
 *
 * <pre>
 * &lt;xs:element name="PostBoxNumber">
 *  &lt;xs:complexType mixed="true">
 *      &lt;xs:attributeGroup ref="grPostal"/>
 *      &lt;xs:anyAttribute namespace="##other"/>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface PostBoxNumber {

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
