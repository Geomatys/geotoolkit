package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultAlias extends DefaultAbstractObject implements Alias {

    private final String targetHref;
    private final String sourceHref;
    private final List<SimpleType> aliasSimpleExtensions;
    private final List<AbstractObject> aliasObjectExtensions;

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param targetHref
     * @param sourceHref
     * @param aliasSimpleExtensions
     * @param aliasObjectExtensions
     */
    public DefaultAlias(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            String targetHref, String sourceHref,
            List<SimpleType> aliasSimpleExtensions, List<AbstractObject> aliasObjectExtensions){
        super(objectSimpleExtensions, idAttributes);
        this.targetHref = targetHref;
        this.sourceHref = sourceHref;
        this.aliasSimpleExtensions = (aliasSimpleExtensions == null) ? EMPTY_LIST : aliasSimpleExtensions;
        this.aliasObjectExtensions = (aliasObjectExtensions == null) ? EMPTY_LIST : aliasObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getTargetHref() {return this.targetHref;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getSourceHref() {return this.sourceHref;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getAliasSimpleExtensions() {return this.aliasSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getAliasObjectExtensions() {return this.aliasObjectExtensions;}

}
