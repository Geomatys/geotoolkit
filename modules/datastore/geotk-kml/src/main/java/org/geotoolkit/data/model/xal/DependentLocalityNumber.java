package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps DependentLocalityNumber element.</p>
 *
 * <p>Number of the dependent locality. Some areas are numbered.
 * Eg. SECTOR 5 in a Suburb as in India or SOI SUKUMVIT 10 as in Thailand.</p>
 *
 * <br />&lt;xs:element name="DependentLocalityNumber" minOccurs="0">
 * <br />&lt;xs:complexType mixed="true">
 * <br />&lt;xs:attribute name="NameNumberOccurrence">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attributeGroup ref="grPostal"/>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
 * <br />&lt;/xs:element>
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
