package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps PostBoxNumberPrefix element.</p>
 *
 * <p>Specification of the prefix of the post box number. eg. A in POBox:A-123</p>
 *
 * <pre>
 * &lt;xs:complexType mixed="true">
 *  &lt;xs:attribute name="NumberPrefixSeparator">...
 *  &lt;/xs:attribute>
 *  &lt;xs:attributeGroup ref="grPostal"/>
 *  &lt;xs:anyAttribute namespace="##other"/>
 * &lt;/xs:complexType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface PostBoxNumberPrefix {

    /**
     * 
     * @return
     */
    public String getContent();

    /**
     * <p>A-12 where 12 is number and A is prefix and "-" is the separator.</p>
     *
     * @return
     */
    public String getNumberPrefixSeparator();

    /**
     *
     * @return
     */
    public GrPostal getGrPostal();
}
