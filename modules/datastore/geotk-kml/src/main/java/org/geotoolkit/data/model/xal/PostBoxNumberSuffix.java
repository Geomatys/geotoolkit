package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps PostNumberSuffix element.</p>
 *
 * <p>Specification of the suffix of the post box number. eg. A in POBox:123A.</p>
 *
 * <br />&lt;xs:complexType mixed="true">
 * <br />&lt;xs:attribute name="NumberSuffixSeparator">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attributeGroup ref="grPostal"/>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
 *
 * @author Samuel Andrés
 */
public interface PostBoxNumberSuffix {

    /**
     *
     * @return
     */
    public String getContent();

    /**
     * <p>12-A where 12 is number and A is suffix and "-" is the separator</p>
     *
     * @return
     */
    public String getNumberSuffixSeparator();

    /**
     *
     * @return
     */
    public GrPostal getGrPostal();
}
