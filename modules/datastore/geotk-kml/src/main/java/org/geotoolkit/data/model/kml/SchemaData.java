package org.geotoolkit.data.model.kml;

import java.util.List;

/**
 *
 * @author Samuel Andrés
 */
public interface SchemaData extends AbstractObject {

    public List<SimpleData> getSimpleDatas();
    public List<Object> getSchemaDataExtensions();

}
