package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 * <p>This interface maps ThoroughfareNumberRange element.</p>
 *
 * <p>A container to represent a range of numbers (from x thru y)for a thoroughfare. eg. 1-2 Albert Av.</p>
 *
 * <br />&lt;xs:element name="ThoroughfareNumberRange">
 * <br />&lt;xs:complexType>
 * <br />&lt;xs:sequence>
 * <br />&lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;xs:element name="ThoroughfareNumberFrom">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:element name="ThoroughfareNumberTo">...
 * <br />&lt;/xs:element>
 * <br />&lt;/xs:sequence>
 * <br />&lt;xs:attribute name="OddEvenEnum">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attribute name="Indicator">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attribute name="Separator">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attribute name="AfterBeforeEnum">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attribute name="AfterBeforeTypeNameEnum">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attribute name="Type"/>
 * <br />&lt;xs:attributeGroup ref="grPostal"/>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
 * <br />&lt;/xs:element>
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

    /**
     * 
     * @return
     */
    public GrPostal getGrPostal();
}
