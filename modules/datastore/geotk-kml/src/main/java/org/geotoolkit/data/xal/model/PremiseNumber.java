/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.data.xal.model;

/**
 * <p>This interface maps PremiseNumber element.</p>
 *
 * <p>Specification of the identifier of the premise (house, building, etc).
 * Premises in a street are often uniquely identified by means of consecutive identifiers.
 * The identifier can be a number, a letter or any combination of the two.</p>
 *
 * <pre>
 * &lt;xs:element name="PremiseNumber">
 *  &lt;xs:complexType mixed="true">
 *      &lt;xs:attribute name="NumberType">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="Type"/>
 *      &lt;xs:attribute name="Indicator">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="IndicatorOccurrence">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="NumberTypeOccurrence">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attributeGroup ref="grPostal"/>
 *      &lt;xs:anyAttribute namespace="##other"/>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
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
