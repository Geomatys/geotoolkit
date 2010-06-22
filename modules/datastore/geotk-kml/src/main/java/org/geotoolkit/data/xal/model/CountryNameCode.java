package org.geotoolkit.data.xal.model;

/**
 * <p>This interface maps CountryNameCode type.</p>
 *
 * <p>A country code according to the specified scheme.</p>
 *
 * <pre>
 * &lt;xs:complexType mixed="true">
 *  &lt;xs:attribute name="Scheme">...
 *  &lt;/xs:attribute>
 *  &lt;xs:attributeGroup ref="grPostal"/>
 *  &lt;xs:anyAttribute namespace="##other"/>
 * &lt;/xs:complexType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface CountryNameCode {

    /**
     * 
     * @return
     */
    public String getContent();

    /**
     * <p>Country code scheme possible values, but not limited to:
     * iso.3166-2, iso.3166-3 for two and three character country codes.</p>
     *
     * @return
     */
    public String getScheme();

    /**
     * 
     * @return
     */
    public GrPostal getGrPostal();

}
