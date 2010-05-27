package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class AbstractTimePrimitiveDefault extends AbstractObjectDefault implements AbstractTimePrimitive {

    private List<SimpleType> abstractTimePrimitiveSimpleExtensions;
    private List<AbstractObject> abstractTimePrimitiveObjectExtensions;

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractTimePrimitiveSimpleExtensions
     * @param abstractTimePrimitiveObjectExtensions
     */
    protected AbstractTimePrimitiveDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractTimePrimitiveSimpleExtensions, List<AbstractObject> abstractTimePrimitiveObjectExtensions){
            super(objectSimpleExtensions, idAttributes);
            this.abstractTimePrimitiveSimpleExtensions = abstractTimePrimitiveSimpleExtensions;
            this.abstractTimePrimitiveObjectExtensions = abstractTimePrimitiveObjectExtensions;
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
