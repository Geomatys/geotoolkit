package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public abstract class DefaultAbstractStyleSelector extends DefaultAbstractObject implements AbstractStyleSelector {

    protected final List<SimpleType> styleSelectorSimpleExtensions;
    protected final List<AbstractObject> styleSelectorObjectExtensions;

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractStyleSelectorSimpleExtensions
     * @param abstractStyleSelectorObjectExtensions
     */
    protected DefaultAbstractStyleSelector(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractStyleSelectorSimpleExtensions,
            List<AbstractObject> abstractStyleSelectorObjectExtensions){
        super(objectSimpleExtensions, idAttributes);
        this.styleSelectorSimpleExtensions = (abstractStyleSelectorSimpleExtensions == null) ? EMPTY_LIST : abstractStyleSelectorSimpleExtensions;
        this.styleSelectorObjectExtensions = (abstractStyleSelectorObjectExtensions == null) ? EMPTY_LIST : abstractStyleSelectorObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getAbstractStyleSelectorSimpleExtensions() {
        return this.styleSelectorSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getAbstractStyleSelectorObjectExtensions() {
        return this.styleSelectorObjectExtensions;
    }

    @Override
    public String toString(){
        String resultat = super.toString()+
                "\n\tAbstractStyleSelectorDefault : ";
        return resultat;
    }

}
