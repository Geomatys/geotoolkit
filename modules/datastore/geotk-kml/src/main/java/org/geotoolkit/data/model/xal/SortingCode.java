package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps SortingCode element.</p>
 *
 * <p>Used for sorting addresses. Values may for example be CEDEX 16 (France).</p>
 *
 * <br />&lt;xs:element name="SortingCode" minOccurs="0">
 * <br />&lt;xs:complexType>
 * <br />&lt;xs:attribute name="Type">
 * <br />&lt;xs:annotation>
 * <br />&lt;xs:xs:attributeGroup ref="grPostal"/>
 * <br />&lt;xs:/xs:complexType>
 * <br />&lt;xs:/xs:element>
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
