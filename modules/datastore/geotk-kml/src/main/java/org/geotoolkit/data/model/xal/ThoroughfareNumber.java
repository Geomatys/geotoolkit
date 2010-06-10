package org.geotoolkit.data.model.xal;

/**
 * <p>THis interface maps ThoroughfareNumber element.</p>
 *
 * <p>Eg.: 23 Archer street or 25/15 Zero Avenue, etc.</p>
 *
 * <pre>
 * &lt;xs:element name="ThoroughfareNumber">
 *  &lt;xs:complexType mixed="true">
 *      &lt;xs:attribute name="SingleRangeEnum">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="Type"/>
 *      &lt;xs:attribute name="Indicator">
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="AfterBeforeEnum">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="AfterBeforeTypeNameEnum">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attributeGroup ref="grPostal"/>
 *      &lt;xs:anyAttribute namespace="##other"/>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface ThoroughfareNumber {

    /**
     *
     * @return
     */
    public String getContent();

    /**
     *
     * @return
     */
    public SingleRangeEnum getNumberType();

    /**
     *
     * @return
     */
    public String getType();

    /**
     * <p>No. in Street No.12 or "#" in Street # 12, etc.</p>
     *
     * @return
     */
    public String getIndicator();

    /**
     *
     * @return
     */
    public AfterBeforeEnum getIndicatorOccurence();

    /**
     *
     * @return
     */
    public AfterBeforeTypeNameEnum getNumberOccurence();

    /**
     *
     * @return
     */
    public GrPostal getGrPostal();

}
