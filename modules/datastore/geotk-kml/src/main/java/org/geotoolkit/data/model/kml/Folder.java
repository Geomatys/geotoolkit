package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface Folder extends AbstractContainer {

    public List<AbstractFeature> getAbstractFeatures();
    public List<SimpleType> getFolderSimpleExtensions();
    public List<AbstractObject> getFolderObjectExtensions();

}
