package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public abstract class DefaultAbstractSubStyle extends DefaultAbstractObject implements AbstractSubStyle {

    protected final List<SimpleType> subStyleSimpleExtensions;
    protected final List<AbstractObject> subStyleObjectExtensions;

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param subStyleSimpleExtensions
     * @param subStyleObjectExtensions
     */
    protected DefaultAbstractSubStyle(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions){
        super(objectSimpleExtensions, idAttributes);
        this.subStyleSimpleExtensions = (subStyleSimpleExtensions == null) ? EMPTY_LIST : subStyleSimpleExtensions;
        this.subStyleObjectExtensions = (subStyleObjectExtensions == null) ? EMPTY_LIST : subStyleObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc}
     */
    @Override
    public List<SimpleType> getSubStyleSimpleExtensions() {return this.subStyleSimpleExtensions;}

    /**
     *
     * @{@inheritDoc}
     */
    @Override
    public List<AbstractObject> getSubStyleObjectExtensions() {return this.subStyleObjectExtensions;}

    @Override
    public String toString(){
        String resultat = super.toString()+
                "\n\tAbstractSubStyleDefault : ";
        return resultat;
    }
}
