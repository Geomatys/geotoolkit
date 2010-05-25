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

    protected AbstractViewDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractViewSimpleExtensions, List<AbstractObject> abstractViewObjectExtensions){
        super(objectSimpleExtensions, idAttributes);
        this.abstractViewSimpleExtensions = abstractViewSimpleExtensions;
        this.abstractViewObjectExtensions = abstractViewObjectExtensions;
    }

    @Override
    public List<SimpleType> getAbstractViewSimpleExtensions() {return this.abstractViewSimpleExtensions;}

    @Override
    public List<AbstractObject> getAbstractViewObjectExtensions() {return this.abstractViewObjectExtensions;}

}
