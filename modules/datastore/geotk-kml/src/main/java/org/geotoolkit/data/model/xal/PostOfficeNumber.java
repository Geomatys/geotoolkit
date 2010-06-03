package org.geotoolkit.data.model.xal;

/**
 *
 * <p>This interface maps PostOfficeNumber type.</p>
 *
 * <br />&lt;xs:complexType mixed="true">
 * <br />&lt;xs:attribute name="Indicator">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attribute name="IndicatorOccurrence">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attributeGroup ref="grPostal"/>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
 *
 * @author Samuel Andrés
 */
public interface PostOfficeNumber {

    /**
     *
     * @return
     */
    public String getContent();

    /**
     * <p>MS in MS 62, # in MS # 12, etc.</p>
     * @return
     */
    public String getIndicator();

    /**
     * <p>MS occurs before 62 in MS 62.</p>
     * @return
     */
    public AfterBeforeEnum getIndicatorOccurence();

    /**
     * 
     * @return
     */
    public GrPostal gerGrPostal();
}
