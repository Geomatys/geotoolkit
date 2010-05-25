package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface Kml {

    public NetworkLinkControl getNetworkLinkControl();
    public AbstractFeature getAbstractFeature();
    public List<SimpleType> getKmlSimpleExtensions();
    public List<AbstractObject> getKmlObjectExtensions();

}
