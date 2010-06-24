package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultStyleMap extends DefaultAbstractStyleSelector implements StyleMap {

    private List<Pair> pairs;
    private List<SimpleType> styleMapSimpleExtensions;
    private List<AbstractObject> styleMapObjectExtensions;

    /**
     * 
     */
    public DefaultStyleMap() {
        this.pairs = EMPTY_LIST;
        this.styleMapSimpleExtensions = EMPTY_LIST;
        this.styleMapObjectExtensions = EMPTY_LIST;
    }

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractStyleSelectorSimpleExtensions
     * @param abstractStyleSelectorObjectExtensions
     * @param pairs
     * @param styleMapSimpleExtensions
     * @param styleMapObjectExtensions
     */
    public DefaultStyleMap(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractStyleSelectorSimpleExtensions,
            List<AbstractObject> abstractStyleSelectorObjectExtensions,
            List<Pair> pairs, List<SimpleType> styleMapSimpleExtensions,
            List<AbstractObject> styleMapObjectExtensions) {
        super(objectSimpleExtensions, idAttributes,
                abstractStyleSelectorSimpleExtensions,
                abstractStyleSelectorObjectExtensions);
        this.pairs = pairs;
        this.styleMapSimpleExtensions = (styleMapSimpleExtensions == null) ? EMPTY_LIST : styleMapSimpleExtensions;
        this.styleMapObjectExtensions = (styleMapObjectExtensions == null) ? EMPTY_LIST : styleMapObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Pair> getPairs() {
        return this.pairs;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getStyleMapSimpleExtensions() {
        return this.styleMapSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getStyleMapObjectExtensions() {
        return this.styleMapObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPairs(List<Pair> pairs) {
        this.pairs = pairs;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setStyleMapSimpleExtensions(List<SimpleType> styleMapSimpleExtensions) {
        this.styleMapSimpleExtensions = styleMapSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setStyleMapObjectExtensions(List<AbstractObject> styleMapObjectExtensions) {
        this.styleMapObjectExtensions = styleMapObjectExtensions;
    }
}
