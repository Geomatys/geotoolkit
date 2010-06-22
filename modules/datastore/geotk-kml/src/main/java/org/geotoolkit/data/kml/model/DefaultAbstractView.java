package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public abstract class DefaultAbstractView extends DefaultAbstractObject implements AbstractView {

    protected List<SimpleType> abstractViewSimpleExtensions;
    protected List<AbstractObject> abstractViewObjectExtensions;

    protected DefaultAbstractView(){
        this.abstractViewSimpleExtensions = EMPTY_LIST;
        this.abstractViewObjectExtensions = EMPTY_LIST;
    }

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

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAbstractViewSimpleExtensions(List<SimpleType> abstractViewSimpleExtensions){
        this.abstractViewSimpleExtensions = abstractViewSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAbstractViewObjectExtensions(List<AbstractObject> abstractViewObjectExtensions){
        this.abstractViewObjectExtensions = abstractViewObjectExtensions;
    }

}
