package org.geotoolkit.data.model.xal;

/**
 *
 * <p>This interface maps PostOfficeNumber type.</p>
 *
 * <pre>
 * &lt;xs:complexType mixed="true">
 *  &lt;xs:attribute name="Indicator">...
 *  &lt;/xs:attribute>
 *  &lt;xs:attribute name="IndicatorOccurrence">...
 *  &lt;/xs:attribute>
 *  &lt;xs:attributeGroup ref="grPostal"/>
 *  &lt;xs:anyAttribute namespace="##other"/>
 * &lt;/xs:complexType>
 * </pre>
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
    public AfterBeforeEnum getIndicatorOccurrence();

    /**
     * 
     * @return
     */
    public GrPostal getGrPostal();
}
