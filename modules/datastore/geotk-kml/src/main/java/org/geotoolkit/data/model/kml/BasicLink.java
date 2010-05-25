package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface BasicLink {
    public List<SimpleType> getObjectSimpleExtensions();
    public IdAttributes getIdAttributes();
    public String getHref();
    public List<SimpleType> getBasicLinkSimpleExtensions();
    public List<AbstractObject> getBasicLinkObjectExtensions();
}
