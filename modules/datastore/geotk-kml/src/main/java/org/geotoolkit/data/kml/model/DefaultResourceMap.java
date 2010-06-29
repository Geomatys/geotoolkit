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
    private List<SimpleType> resourceMapSimpleExtensions;
    private List<AbstractObject> resourceMapObjectExtensions;

    public DefaultResourceMap() {
        this.aliases = EMPTY_LIST;
        this.resourceMapSimpleExtensions = EMPTY_LIST;
        this.resourceMapObjectExtensions = EMPTY_LIST;
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
            List<SimpleType> resourceMapSimpleExtensions, List<AbstractObject> resourceMapObjectExtensions) {
        super(objectSimpleExtensions, idAttributes);
        this.aliases = aliases;
        this.resourceMapSimpleExtensions = (resourceMapSimpleExtensions == null) ? EMPTY_LIST : resourceMapSimpleExtensions;
        this.resourceMapObjectExtensions = (resourceMapObjectExtensions == null) ? EMPTY_LIST : resourceMapObjectExtensions;
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
    public List<SimpleType> getResourceMapSimpleExtensions() {
        return this.resourceMapSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getResourceMapObjectExtensions() {
        return this.resourceMapObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAliases(List<Alias> aliases) {
        this.aliases = aliases;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setResourceMapSimpleExtensions(List<SimpleType> resourceMapSimpleExtensions) {
        this.resourceMapSimpleExtensions = resourceMapSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setResourceMapObjectExtensions(List<AbstractObject> resourceMapObjectExtensions) {
        this.resourceMapObjectExtensions = resourceMapObjectExtensions;
    }
}
