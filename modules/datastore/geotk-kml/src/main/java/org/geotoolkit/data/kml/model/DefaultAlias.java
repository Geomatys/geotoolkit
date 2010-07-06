package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultAlias extends DefaultAbstractObject implements Alias {

    private String targetHref;
    private String sourceHref;

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
            String targetHref, String sourceHref,
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
    public String getTargetHref() {
        return this.targetHref;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getSourceHref() {
        return this.sourceHref;
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public void setTargetHref(String targetHref) {
        this.targetHref = targetHref;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setSourceHref(String sourceHref) {
        this.sourceHref = sourceHref;
    }
}
