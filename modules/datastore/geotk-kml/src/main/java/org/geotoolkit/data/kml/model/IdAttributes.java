package org.geotoolkit.data.kml.model;

/**
 * <p>This interface maps idAttributes attributeGroup.</p>
 *
 * <pre>
 * &lt;attributeGroup name="idAttributes">
 *  &lt;attribute name="id" type="ID" use="optional"/>
 *  &lt;attribute name="targetId" type="NCName" use="optional"/>
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
