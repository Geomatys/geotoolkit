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

    /**
     * 
     */
    public DefaultStyleMap() {
        this.pairs = EMPTY_LIST;
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
    public DefaultStyleMap(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractStyleSelectorSimpleExtensions,
            List<AbstractObject> abstractStyleSelectorObjectExtensions,
            List<Pair> pairs,
            List<SimpleType> styleMapSimpleExtensions,
            List<AbstractObject> styleMapObjectExtensions) {
        super(objectSimpleExtensions, idAttributes,
                abstractStyleSelectorSimpleExtensions,
                abstractStyleSelectorObjectExtensions);
        this.pairs = pairs;
        if (styleMapSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.STYLE_MAP).addAll(styleMapSimpleExtensions);
        }
        if (styleMapObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.STYLE_MAP).addAll(styleMapObjectExtensions);
        }
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
    public void setPairs(List<Pair> pairs) {
        this.pairs = pairs;
    }
}
