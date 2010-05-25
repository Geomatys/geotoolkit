package org.geotoolkit.data.model.kml;

import java.util.List;

/**
 *
 * @author Samuel Andrés
 */
public interface Data extends AbstractObject {

    public String getDisplayName();
    public String getValue();
    public List<Object> getDataExtensions();
}
