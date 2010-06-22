package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultAbstractTimePrimitive extends DefaultAbstractObject implements AbstractTimePrimitive {

    protected final List<SimpleType> abstractTimePrimitiveSimpleExtensions;
    protected final List<AbstractObject> abstractTimePrimitiveObjectExtensions;

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractTimePrimitiveSimpleExtensions
     * @param abstractTimePrimitiveObjectExtensions
     */
    protected DefaultAbstractTimePrimitive(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractTimePrimitiveSimpleExtensions, List<AbstractObject> abstractTimePrimitiveObjectExtensions){
            super(objectSimpleExtensions, idAttributes);
            this.abstractTimePrimitiveSimpleExtensions = (abstractTimePrimitiveSimpleExtensions == null) ? EMPTY_LIST : abstractTimePrimitiveSimpleExtensions;
            this.abstractTimePrimitiveObjectExtensions = (abstractTimePrimitiveObjectExtensions == null) ? EMPTY_LIST : abstractTimePrimitiveObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getAbstractTimePrimitiveSimpleExtensions() {return this.abstractTimePrimitiveSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getAbstractTimePrimitiveObjectExtensions() {return this.abstractTimePrimitiveObjectExtensions;}

}
