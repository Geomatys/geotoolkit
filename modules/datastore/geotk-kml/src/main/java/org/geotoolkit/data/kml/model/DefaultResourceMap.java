package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultResourceMap extends DefaultAbstractObject implements ResourceMap {

    private List<Alias> aliases;

    /**
     * 
     */
    public DefaultResourceMap() {
        this.aliases = EMPTY_LIST;
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param aliases
     * @param resourceMapSimpleExtensions
     * @param resourceMapObjectExtensions
     */
    public DefaultResourceMap(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<Alias> aliases,
            List<SimpleType> resourceMapSimpleExtensions,
            List<AbstractObject> resourceMapObjectExtensions) {
        super(objectSimpleExtensions, idAttributes);
        this.aliases = aliases;
        if (resourceMapSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.RESOURCE_MAP).addAll(resourceMapSimpleExtensions);
        }
        if (resourceMapObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.RESOURCE_MAP).addAll(resourceMapObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Alias> getAliases() {
        return this.aliases;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAliases(List<Alias> aliases) {
        this.aliases = aliases;
    }
}
