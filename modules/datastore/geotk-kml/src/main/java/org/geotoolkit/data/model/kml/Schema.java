package org.geotoolkit.data.model.kml;

import java.util.List;

/**
 *
 * @author Samuel Andrés
 */
public interface Schema {
 
    public List<SimpleField> getSimpleFields();
    //public List<SchemaExtension> getSchemaExtensions();
    public String getName();
    public String getId();
}
