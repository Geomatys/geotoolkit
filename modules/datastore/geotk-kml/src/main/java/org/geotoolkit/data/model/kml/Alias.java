package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface Alias extends AbstractObject {

    public String getTargetHref();
    public String getSourceHref();
    public List<SimpleType> getAliasSimpleExtensions();
    public List<AbstractObject> getAliasObjectExtensions();

}
