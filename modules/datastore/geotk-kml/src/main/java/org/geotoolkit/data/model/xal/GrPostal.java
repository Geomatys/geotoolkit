package org.geotoolkit.data.model.xal;

/**
 * <p>This interface maps grPostal attributeGroup.</p>
 *
 * <br />&lt;xs:attributeGroup name="grPostal">
 * <br />&lt;xs:attribute name="Code">...
 * <br />&lt;/xs:attribute>
 * <br />&lt;/xs:attributeGroup>
 *
 * @author Samuel Andrés
 */
public interface GrPostal {

    /**
     * 
     * @return
     */
    public String getName();

    /**
     * 
     * <p>Used by postal services to encode the name of the element.</p>
     * 
     * @return
     */
    public String getValue();
}
