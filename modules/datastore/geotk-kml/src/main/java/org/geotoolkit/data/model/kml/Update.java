package org.geotoolkit.data.model.kml;

import java.util.List;

/**
 *
 * @author Samuel Andrés
 */
public interface Update {

    public List<Create> getCreate();
    public List<Delete> getDelete();
    public List<Change> getChange();
    public List<Object> getUpdateOpExtensions();
    public List<Object> getUpdateExtensions();

}
