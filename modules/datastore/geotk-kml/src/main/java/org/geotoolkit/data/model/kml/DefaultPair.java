package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultPair extends DefaultAbstractObject implements Pair{

    private final StyleState key;
    private final String styleUrl;
    private final AbstractStyleSelector styleSelector;
    private final List<SimpleType> pairSimpleExtensions;
    private final List<AbstractObject> pairObjectExtensions;

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
    public DefaultPair(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            StyleState key, String styleUrl, AbstractStyleSelector styleSelector,
            List<SimpleType> pairSimpleExtensions,
            List<AbstractObject> pairObjectExtensions){
        super(objectSimpleExtensions, idAttributes);
        this.key = key;
        this.styleUrl = styleUrl;
        this.styleSelector = styleSelector;
        this.pairSimpleExtensions = (pairSimpleExtensions == null) ? EMPTY_LIST : pairSimpleExtensions;
        this.pairObjectExtensions = (pairObjectExtensions == null) ? EMPTY_LIST : pairObjectExtensions;
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
