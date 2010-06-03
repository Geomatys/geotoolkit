package org.geotoolkit.data.model.xal;

/**
 * <p>THis interface maps ThoroughfareNumber element.</p>
 *
 * <p>Eg.: 23 Archer street or 25/15 Zero Avenue, etc.</p>
 *
 * <br />&lt;xs:element name="ThoroughfareNumber">
 * <br />&lt;xs:complexType mixed="true">
 * <br />&lt;xs:attribute name="SingleRangeEnum">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attribute name="Type"/>
 * <br />&lt;xs:attribute name="Indicator">
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attribute name="AfterBeforeEnum">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attribute name="AfterBeforeTypeNameEnum">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attributeGroup ref="grPostal"/>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
 * <br />&lt;/xs:element>
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
