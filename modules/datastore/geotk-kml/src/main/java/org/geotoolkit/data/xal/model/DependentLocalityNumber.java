package org.geotoolkit.data.xal.model;

/**
 * <p>This interface maps DependentLocalityNumber element.</p>
 *
 * <p>Number of the dependent locality. Some areas are numbered.
 * Eg. SECTOR 5 in a Suburb as in India or SOI SUKUMVIT 10 as in Thailand.</p>
 *
 * <pre>
 * &lt;xs:element name="DependentLocalityNumber" minOccurs="0">
 *  &lt;xs:complexType mixed="true">
 *      &lt;xs:attribute name="NameNumberOccurrence">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attributeGroup ref="grPostal"/>
 *      &lt;xs:anyAttribute namespace="##other"/>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface DependentLocalityNumber {

    /**
     * 
     * @return
     */
    public String getContent();

    /**
     * <p>Eg. SECTOR occurs before 5 in SECTOR 5.</p>
     *
     * @return
     */
    public AfterBeforeEnum getNameNumberOccurrence();

    /**
     * 
     * @return
     */
    public GrPostal getGrPostal();
}
