package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultAlias extends DefaultAbstractObject implements Alias {

    private String targetHref;
    private String sourceHref;
    private List<SimpleType> aliasSimpleExtensions;
    private List<AbstractObject> aliasObjectExtensions;

    public DefaultAlias(){
        this.aliasSimpleExtensions = EMPTY_LIST;
        this.aliasObjectExtensions = EMPTY_LIST;
    }

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

    /**
     *
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

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAliasSimpleExtensions(List<SimpleType> aliasSimpleExtensions) {
        this.aliasSimpleExtensions = aliasSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAliasObjectExtensions(List<AbstractObject> aliasObjectExtensions) {
        this.aliasObjectExtensions = aliasObjectExtensions;
    }

}
