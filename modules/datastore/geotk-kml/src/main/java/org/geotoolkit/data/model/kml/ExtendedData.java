package org.geotoolkit.data.model.kml;

import java.util.List;

/**
 *
 * @author Samuel Andrés
 */
public interface ExtendedData {

    public List<Data> getDatas();
    public List<SchemaData> getSchemaData();
    public List<Object> getAnyOtherElements();
}
