package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public abstract class DefaultAbstractView extends DefaultAbstractObject implements AbstractView {

    protected final List<SimpleType> abstractViewSimpleExtensions;
    protected final List<AbstractObject> abstractViewObjectExtensions;

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractViewSimpleExtensions
     * @param abstractViewObjectExtensions
     */
    protected DefaultAbstractView(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractViewSimpleExtensions, List<AbstractObject> abstractViewObjectExtensions){
        super(objectSimpleExtensions, idAttributes);
        this.abstractViewSimpleExtensions = (abstractViewSimpleExtensions == null) ? EMPTY_LIST : abstractViewSimpleExtensions;
        this.abstractViewObjectExtensions = (abstractViewObjectExtensions == null) ? EMPTY_LIST : abstractViewObjectExtensions;
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
