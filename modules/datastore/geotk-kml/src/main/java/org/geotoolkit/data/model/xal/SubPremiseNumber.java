package org.geotoolkit.data.model.xal;

/**
 * <p></p>
 *
 * <p>Specification of the identifier of a sub-premise.
 * Examples of sub-premises are apartments and suites.
 * sub-premises in a building are often uniquely identified
 * by means of consecutive identifiers.</p>
 *
 * <p>The identifier can be a number, a letter or any combination of the two.
 * In the latter case, the identifier includes exactly one variable (range) part,
 * which is either a number or a single letter that is surrounded
 * by fixed parts at the left (prefix) or the right (postfix).</p>
 *
 * <pre>
 * &lt;xs:complexType mixed="true">
 *  &lt;xs:attribute name="Indicator">...
 *  &lt;/xs:attribute>
 *  &lt;xs:attribute name="IndicatorOccurrence">...
 *  &lt;/xs:attribute>
 *  &lt;xs:attribute name="NumberTypeOccurrence">...
 *  &lt;/xs:attribute>
 *  &lt;xs:attribute name="PremiseNumberSeparator">...
 *  &lt;/xs:attribute>
 *  &lt;xs:attribute name="Type"/>
 *  &lt;xs:attributeGroup ref="grPostal"/>
 *  &lt;xs:anyAttribute namespace="##other"/>
 * &lt;/xs:complexType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface SubPremiseNumber extends GenericTypedGrPostal {

    /**
     * <p>"TH" in 12TH which is a floor number, "NO." in NO.1, "#" in APT #12, etc.</p>
     * 
     * @return
     */
    public String getIndicator();

    /**
     * <p>"No." occurs before 1 in No.1, or TH occurs after 12 in 12TH.</p>
     *
     * @return
     */
    public AfterBeforeEnum getIndicatorOccurrence();

    /**
     * <p>12TH occurs "before" FLOOR (a type of subpremise) in 12TH FLOOR.</p>
     *
     * @return
     */
    public AfterBeforeEnum getNumberTypeOccurrence();

    /**
     * <p>"/" in 12/14 Archer Street where 12 is sub-premise number and 14 is premise number.</p>
     * 
     * @return
     */
    public String getPremiseNumberSeparator();

}
