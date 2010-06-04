package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps SortingCode element.</p>
 *
 * <p>Used for sorting addresses. Values may for example be CEDEX 16 (France).</p>
 *
 * <pre>
 * &lt;xs:element name="SortingCode" minOccurs="0">
 *  &lt;xs:complexType>
 *      &lt;xs:attribute name="Type">
 *      &lt;xs:xs:attributeGroup ref="grPostal"/>
 *  &lt;xs:/xs:complexType>
 * &lt;xs:/xs:element>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface SortingCode {

    /**
     * <p>Specific to postal service.</p>
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
