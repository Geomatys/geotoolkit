package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class PairDefault extends AbstractObjectDefault implements Pair{

    private StyleState key;
    private String styleUrl;
    private AbstractStyleSelector styleSelector;
    private List<SimpleType> pairSimpleExtensions;
    private List<AbstractObject> pairObjectExtensions;

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param key
     * @param styleUrl
     * @param styleSelector
     * @param pairSimpleExtensions
     * @param pairObjectExtensions
     */
    public PairDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            StyleState key, String styleUrl, AbstractStyleSelector styleSelector,
            List<SimpleType> pairSimpleExtensions,
            List<AbstractObject> pairObjectExtensions){
        super(objectSimpleExtensions, idAttributes);
        this.key = key;
        this.styleUrl = styleUrl;
        this.styleSelector = styleSelector;
        this.pairSimpleExtensions = pairSimpleExtensions;
        this.pairObjectExtensions = pairObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public StyleState getKey() {return this.key;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getStyleUrl() {return this.styleUrl;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AbstractStyleSelector getAbstractStyleSelector() {return this.styleSelector;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getPairSimpleExtensions() {return this.pairSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getPairObjectExtensions() {return this.pairObjectExtensions;}

}
