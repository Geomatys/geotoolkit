package org.geotoolkit.data.xal.model;

/**
 * <p>This interface maps grPostal attributeGroup.</p>
 *
 * <pre>
 *  &lt;xs:attributeGroup name="grPostal">
 *  &lt;xs:attribute name="Code">...
 *  &lt;/xs:attribute>
 *  &lt;/xs:attributeGroup>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface GrPostal {

    /**
     * 
     * <p>Used by postal services to encode the name of the element.</p>
     * 
     * @return
     */
    public String getCode();
}
