package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps PostTownSuffix type.</p>
 *
 * <p>GENERAL PO in MIAMI GENERAL PO.</p>
 *
 * <pre>
 * &lt;xs:complexType mixed="true">
 *  &lt;xs:attributeGroup ref="grPostal"/>
 *  &lt;xs:anyAttribute namespace="##other"/>
 * &lt;/xs:complexType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface PostTownSuffix {

    /**
     * 
     * @return
     */
    public String getContent();

    /**
     *
     * @return
     */
    public GrPostal getGrPostal();
}
