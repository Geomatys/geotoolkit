package org.geotoolkit.data.model.kml;

/**
 * <p>This interface maps idAttributes attributeGroup.</p>
 *
 * <pre>
 * &lt;attributeGroup name="idAttributes">
 *  &lt;attribute name="id" type="ID" use="optional"/> <!-- NOT AN OBJETC : type String -->
 *  &lt;attribute name="targetId" type="NCName" use="optional"/> <!-- NOT AN OBJETC : type String -->
 * &lt;/attributeGroup>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface IdAttributes {

    /**
     *
     * @return The id value.
     */
    public String getId();

    /**
     *
     * @return The targetId value.
     */
    public String getTargetId();
}
