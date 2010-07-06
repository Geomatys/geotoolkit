package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public abstract class DefaultAbstractView extends DefaultAbstractObject implements AbstractView {

    protected DefaultAbstractView() {
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractViewSimpleExtensions
     * @param abstractViewObjectExtensions
     */
    protected DefaultAbstractView(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractViewSimpleExtensions,
            List<AbstractObject> abstractViewObjectExtensions) {
        super(objectSimpleExtensions, idAttributes);
        if (abstractViewSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.VIEW).addAll(abstractViewSimpleExtensions);
        }
        if (abstractViewObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.VIEW).addAll(abstractViewObjectExtensions);
        }
    }
}
