package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class StyleMapDefault extends AbstractStyleSelectorDefault implements StyleMap {

    private List<Pair> pairs;
    private List<SimpleType> styleMapSimpleExtensions;
    private List<AbstractObject> styleMapObjectExtensions;

    public StyleMapDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractStyleSelectorSimpleExtensions,
            List<AbstractObject> abstractStyleSelectorObjectExtensions,
            List<Pair> pairs, List<SimpleType> styleMapSimpleExtensions, List<AbstractObject> styleMapObjectExtensions){
        super(objectSimpleExtensions, idAttributes,
            abstractStyleSelectorSimpleExtensions,
            abstractStyleSelectorObjectExtensions);
        this.pairs = pairs;
        this.styleMapSimpleExtensions = styleMapSimpleExtensions;
        this.styleMapObjectExtensions = styleMapObjectExtensions;
    }

    @Override
    public List<Pair> getPairs() {return this.pairs;}

    @Override
    public List<SimpleType> getStyleMapSimpleExtensions() {return this.styleMapSimpleExtensions;}

    @Override
    public List<AbstractObject> getStyleMapObjectExtensions() {return this.styleMapObjectExtensions;}

}
