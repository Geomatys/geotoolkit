package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultAbstractTimePrimitive extends DefaultAbstractObject implements AbstractTimePrimitive {

    protected List<SimpleType> abstractTimePrimitiveSimpleExtensions;
    protected List<AbstractObject> abstractTimePrimitiveObjectExtensions;

    /**
     * 
     */
    protected DefaultAbstractTimePrimitive(){
        this.abstractTimePrimitiveSimpleExtensions = EMPTY_LIST;
        this.abstractTimePrimitiveObjectExtensions = EMPTY_LIST;
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractTimePrimitiveSimpleExtensions
     * @param abstractTimePrimitiveObjectExtensions
     */
    protected DefaultAbstractTimePrimitive(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractTimePrimitiveSimpleExtensions,
            List<AbstractObject> abstractTimePrimitiveObjectExtensions){
            super(objectSimpleExtensions, idAttributes);
            this.abstractTimePrimitiveSimpleExtensions = (abstractTimePrimitiveSimpleExtensions == null) ? EMPTY_LIST : abstractTimePrimitiveSimpleExtensions;
            this.abstractTimePrimitiveObjectExtensions = (abstractTimePrimitiveObjectExtensions == null) ? EMPTY_LIST : abstractTimePrimitiveObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getAbstractTimePrimitiveSimpleExtensions() {
        return this.abstractTimePrimitiveSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getAbstractTimePrimitiveObjectExtensions() {
        return this.abstractTimePrimitiveObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void getAbstractTimePrimitiveSimpleExtensions(List<SimpleType> abstractTimePrimitiveSimpleExtensions) {
        this.abstractTimePrimitiveSimpleExtensions = abstractTimePrimitiveSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void getAbstractTimePrimitiveObjectExtensions(List<AbstractObject> abstractTimePrimitiveObjectExtensions) {
        this.abstractTimePrimitiveObjectExtensions = abstractTimePrimitiveObjectExtensions;
    }

}
