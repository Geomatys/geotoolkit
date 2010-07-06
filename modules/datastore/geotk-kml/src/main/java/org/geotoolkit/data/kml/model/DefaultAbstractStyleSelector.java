package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public abstract class DefaultAbstractStyleSelector extends DefaultAbstractObject implements AbstractStyleSelector {

    protected DefaultAbstractStyleSelector() {
    }

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractStyleSelectorSimpleExtensions
     * @param abstractStyleSelectorObjectExtensions
     */
    protected DefaultAbstractStyleSelector(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractStyleSelectorSimpleExtensions,
            List<AbstractObject> abstractStyleSelectorObjectExtensions) {
        super(objectSimpleExtensions, idAttributes);
        if (abstractStyleSelectorSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.STYLE_SELECTOR).addAll(abstractStyleSelectorSimpleExtensions);
        }
        if (abstractStyleSelectorObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.STYLE_SELECTOR).addAll(abstractStyleSelectorObjectExtensions);
        }
    }

    @Override
    public String toString() {
        String resultat = super.toString()
                + "\n\tAbstractStyleSelectorDefault : ";
        return resultat;
    }
}
