package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class ResourceMapDefault extends AbstractObjectDefault implements ResourceMap {

    private final List<Alias> aliases;
    private final List<SimpleType> resourceMapSimpleExtensions;
    private final List<AbstractObject> resourceMapObjectExtensions;

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param aliases
     * @param resourceMapSimpleExtensions
     * @param resourceMapObjectExtensions
     */
    public ResourceMapDefault(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<Alias> aliases,
            List<SimpleType> resourceMapSimpleExtensions, List<AbstractObject> resourceMapObjectExtensions){
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
    public List<Alias> getAliases() {return this.aliases;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getResourceMapSimpleExtensions() {return this.resourceMapSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getResourceMapObjectExtensions() {return this.resourceMapObjectExtensions;}

}
