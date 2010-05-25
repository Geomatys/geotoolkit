package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class AbstractContainerStructure {

    public List<SimpleType> containerSimpleExtensions;
    public List<AbstractObject> containerObjectExtensions;
}
