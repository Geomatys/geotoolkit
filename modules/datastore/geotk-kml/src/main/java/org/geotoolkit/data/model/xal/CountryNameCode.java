package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps CountryNameCode type.</p>
 *
 * <p>A country code according to the specified scheme.</p>
 *
 * <br />&lt;xs:complexType mixed="true">
 * <br />&lt;xs:attribute name="Scheme">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;xs:attributeGroup ref="grPostal"/>
 * <br />&lt;xs:anyAttribute namespace="##other"/>
 * <br />&lt;/xs:complexType>
 *
 * @author Samuel Andrés
 */
public interface CountryNameCode {

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
