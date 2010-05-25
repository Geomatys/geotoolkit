package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface Document extends AbstractContainer {

    public List<Schema> getSchemas();
    public List<AbstractFeature> getAbstractFeatures();
    public List<SimpleType> getDocumentSimpleExtensions();
    public List<AbstractObject> getDocumentObjectExtensions();

}
