package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class ResourceMapDefault extends AbstractObjectDefault implements ResourceMap {

    private List<Alias> aliases;
    private List<SimpleType> resourceMapSimpleExtensions;
    private List<AbstractObject> resourceMapObjectExtensions;

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
        this.resourceMapSimpleExtensions = resourceMapSimpleExtensions;
        this.resourceMapObjectExtensions = resourceMapObjectExtensions;
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
