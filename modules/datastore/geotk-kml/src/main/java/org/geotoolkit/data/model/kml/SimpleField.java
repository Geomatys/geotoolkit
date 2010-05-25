package org.geotoolkit.data.model.kml;

import java.util.List;

/**
 *
 * @author Samuel Andrés
 */
public interface SimpleField {

    public String getDisplayName();
    //public List<SimpleFieldExtension> getSimpleFieldExtensions();
    public String getType();
    public String getName();
}
