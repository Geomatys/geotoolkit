/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps PremiseNumber element.</p>
 *
 * <p>Specification of the identifier of the premise (house, building, etc).
 * Premises in a street are often uniquely identified by means of consecutive identifiers.
 * The identifier can be a number, a letter or any combination of the two.</p>
 *
 * <br />&lt;xs:element name="PremiseNumber">
 * <br />&lt;xs:complexType mixed="true">
 * <br />&lt;xs:attribute name="NumberType">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attribute name="Type"/>
 * <br />&lt;xs:attribute name="Indicator">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attribute name="IndicatorOccurrence">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attribute name="NumberTypeOccurrence">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attributeGroup ref="grPostal"/>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
 * <br />&lt;/xs:element>
 * 
 * @author Samuel Andrés
 */
public interface PremiseNumber extends GenericTypedGrPostal{

    /**
     * <p>Building 12-14 is "Range" and Building 12 is "Single".</p>
     *
     * @return
     */
    public SingleRangeEnum getNumberType();

    /**
     * <p>No. in House No.12, # in #12, etc.</p>
     *
     * @return
     */
    public String getIndicator();

    /**
     * <p>No. occurs before 12 No.12</p>
     *
     * @return
     */
    public AfterBeforeEnum getIndicatorOccurrence();

    /**
     * <p>12 in BUILDING 12 occurs "after" premise type BUILDING.</p>
     * @return
     */
    public AfterBeforeEnum getNumberTypeOccurrence();
}
