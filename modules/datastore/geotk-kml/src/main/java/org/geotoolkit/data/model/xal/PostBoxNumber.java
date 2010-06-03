package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps PostBoxNumber element.</p>
 *
 * <p>Specification of the number of a postbox.</p>
 *
 * <br />&lt;xs:element name="PostBoxNumber">
 * <br />&lt;xs:complexType mixed="true">
 * <br />&lt;xs:attributeGroup ref="grPostal"/>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
 * <br />&lt;/xs:element>
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
