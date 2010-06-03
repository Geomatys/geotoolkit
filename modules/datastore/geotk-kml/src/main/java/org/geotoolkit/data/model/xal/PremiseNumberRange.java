package org.geotoolkit.data.model.xal;

/**
 * <p></p>
 *
 * <p>Specification for defining the premise number range. Some premises have number as Building C1-C7.</p>
 *
 * <br />&lt;xs:element name="PremiseNumberRange">
 * <br />&lt;xs:complexType>
 * <br />&lt;xs:sequence>
 * <br />&lt;xs:element name="PremiseNumberRangeFrom">...
 * <br />&lt;/xs:element>
 * <br />&lt;xs:element name="PremiseNumberRangeTo">...
 * <br />&lt;/xs:element>
 * <br />&lt;/xs:sequence>
 * <br />&lt;xs:attribute name="RangeType">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attribute name="Indicator">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attribute name="Separator">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attribute name="Type"/>
 * <br />&lt;xs:attribute name="IndicatorOccurence">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attribute name="NumberRangeOccurence">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;/xs:complexType>
 * <br />&lt;/xs:element>
 *
 * @author Samuel Andrés
 */
public interface PremiseNumberRange {

    /**
     *
     * @return
     */
    public PremiseNumberRangeFrom getPremiseNumberRangeFrom();
    
    /**
     * 
     * @return
     */
    public PremiseNumberRangeTo getPremiseNumberRangeTo();
    
    /**
     * <p>Eg. Odd or even number range.</p>
     * 
     * @return
     */
    public String getRangeType();
    
    /**
     * <p>Eg. No. in Building No:C1-C5</p>
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
    public String getType();
    
    /**
     * <p>No.12-14 where "No." is before actual street number.</p>
     * 
     * @return
     */
    public AfterBeforeEnum getIndicatorOccurrence();
    
    /**
     * <p>Building 23-25 where the number occurs after building name.</p>
     * 
     * @return
     */
    public AfterBeforeTypeNameEnum getNumberRangeOccurrence();

}
