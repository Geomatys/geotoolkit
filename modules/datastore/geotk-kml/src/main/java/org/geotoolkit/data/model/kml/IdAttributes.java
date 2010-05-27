package org.geotoolkit.data.model.kml;

/**
 * <p>This interface maps idAttributes attributeGroup.</p>
 *
 * <br />&lt;attributeGroup name="idAttributes">
 * <br />&lt;attribute name="id" type="ID" use="optional"/> <!-- NOT AN OBJETC : type String -->
 * <br />&lt;attribute name="targetId" type="NCName" use="optional"/> <!-- NOT AN OBJETC : type String -->
 * <br />&lt;/attributeGroup>
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
