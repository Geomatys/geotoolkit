package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public abstract class AbstractViewDefault extends AbstractObjectDefault implements AbstractView {

    protected List<SimpleType> abstractViewSimpleExtensions;
    protected List<AbstractObject> abstractViewObjectExtensions;

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractViewSimpleExtensions
     * @param abstractViewObjectExtensions
     */
    protected AbstractViewDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractViewSimpleExtensions, List<AbstractObject> abstractViewObjectExtensions){
        super(objectSimpleExtensions, idAttributes);
        this.abstractViewSimpleExtensions = abstractViewSimpleExtensions;
        this.abstractViewObjectExtensions = abstractViewObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getAbstractViewSimpleExtensions() {return this.abstractViewSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getAbstractViewObjectExtensions() {return this.abstractViewObjectExtensions;}

}
