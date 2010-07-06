package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public abstract class DefaultAbstractSubStyle extends DefaultAbstractObject implements AbstractSubStyle {

    /**
     * 
     */
    protected DefaultAbstractSubStyle() {
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractSubStyleSimpleExtensions
     * @param abstractSubStyleObjectExtensions
     */
    protected DefaultAbstractSubStyle(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractSubStyleSimpleExtensions,
            List<AbstractObject> abstractSubStyleObjectExtensions) {
        super(objectSimpleExtensions, idAttributes);
        if (abstractSubStyleSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.SUB_STYLE).addAll(abstractSubStyleSimpleExtensions);
        }
        if (abstractSubStyleObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.SUB_STYLE).addAll(abstractSubStyleObjectExtensions);
        }
    }

    @Override
    public String toString() {
        String resultat = super.toString()
                + "\n\tAbstractSubStyleDefault : ";
        return resultat;
    }
}
