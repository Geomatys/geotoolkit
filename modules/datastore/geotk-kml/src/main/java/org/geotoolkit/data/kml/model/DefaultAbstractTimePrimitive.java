package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultAbstractTimePrimitive extends DefaultAbstractObject implements AbstractTimePrimitive {

    /**
     * 
     */
    protected DefaultAbstractTimePrimitive() {
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractTimePrimitiveSimpleExtensions
     * @param abstractTimePrimitiveObjectExtensions
     */
    protected DefaultAbstractTimePrimitive(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractTimePrimitiveSimpleExtensions,
            List<AbstractObject> abstractTimePrimitiveObjectExtensions) {
        super(objectSimpleExtensions, idAttributes);
        if (abstractTimePrimitiveSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.TIME_PRIMITIVE).addAll(abstractTimePrimitiveSimpleExtensions);
        }
        if (abstractTimePrimitiveObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.TIME_PRIMITIVE).addAll(abstractTimePrimitiveObjectExtensions);
        }
    }
}
