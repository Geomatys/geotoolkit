package org.geotoolkit.data.xal.model;

import java.util.List;

/**
 * <p>This interface maps ThoroughfareNumberRange element.</p>
 *
 * <p>A container to represent a range of numbers (from x thru y)for a thoroughfare. eg. 1-2 Albert Av.</p>
 *
 * <pre>
 * &lt;xs:element name="ThoroughfareNumberRange">
 *  &lt;xs:complexType>
 *      &lt;xs:sequence>
 *          &lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;xs:element name="ThoroughfareNumberFrom">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="ThoroughfareNumberTo">...
 *          &lt;/xs:element>
 *      &lt;/xs:sequence>
 *      &lt;xs:attribute name="OddEvenEnum">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="Indicator">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="Separator">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="AfterBeforeEnum">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="AfterBeforeTypeNameEnum">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="Type"/>
 *      &lt;xs:attributeGroup ref="grPostal"/>
 *      &lt;xs:anyAttribute namespace="##other"/>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface ThoroughfareNumberRange {

    /**
     * 
     * @return
     */
    public List<GenericTypedGrPostal> getAddressLines();

    /**
     *
     * @return
     */
    public ThoroughfareNumberFrom getThoroughfareNumberFrom();

    /**
     *
     * @return
     */
    public ThoroughfareNumberTo getThoroughfareNumberTo();

    /**
     * <p>Thoroughfare number ranges are odd or even.</p>
     * 
     * @return
     */
    public OddEvenEnum getRangeType();

    /**
     * <p>"No." No.12-13</p>
     *
     * @return
     */
    public String getIndicator();

    /**
     * <p>"-" in 12-14  or "Thru" in 12 Thru 14 etc.</p>
     *
     * @return
     */
    public String getSeparator();

    /**
     *
     * @return
     */
    public AfterBeforeEnum getIndicatorOccurence();

    /**
     * 
     * @return
     */
    public AfterBeforeTypeNameEnum getNumberRangeOccurence();

    /**
     *
     * @return
     */
    public String getType();
}
