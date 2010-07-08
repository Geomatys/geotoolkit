package org.geotoolkit.data.kml.model;

import java.net.URI;
import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultAlias extends DefaultAbstractObject implements Alias {

    private URI targetHref;
    private URI sourceHref;

    public DefaultAlias() {
    }

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param targetHref
     * @param sourceHref
     * @param abstractAliasSimpleExtensions
     * @param abstractAliasObjectExtensions
     */
    public DefaultAlias(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            URI targetHref, URI sourceHref,
            List<SimpleType> abstractAliasSimpleExtensions,
            List<AbstractObject> abstractAliasObjectExtensions) {
        super(objectSimpleExtensions, idAttributes);
        this.targetHref = targetHref;
        this.sourceHref = sourceHref;
        if (abstractAliasSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.ALIAS).addAll(abstractAliasSimpleExtensions);
        }
        if (abstractAliasObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.ALIAS).addAll(abstractAliasObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public URI getTargetHref() {
        return this.targetHref;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public URI getSourceHref() {
        return this.sourceHref;
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public void setTargetHref(URI targetHref) {
        this.targetHref = targetHref;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setSourceHref(URI sourceHref) {
        this.sourceHref = sourceHref;
    }
}
