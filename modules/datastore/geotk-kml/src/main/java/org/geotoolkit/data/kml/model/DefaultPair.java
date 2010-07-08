package org.geotoolkit.data.kml.model;

import java.net.URI;
import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultPair extends DefaultAbstractObject implements Pair {

    private StyleState key;
    private URI styleUrl;
    private AbstractStyleSelector styleSelector;

    /**
     * 
     */
    public DefaultPair() {
        this.key = DEF_STYLE_STATE;
    }

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
    public DefaultPair(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            StyleState key, URI styleUrl, AbstractStyleSelector styleSelector,
            List<SimpleType> pairSimpleExtensions,
            List<AbstractObject> pairObjectExtensions) {
        super(objectSimpleExtensions, idAttributes);
        this.key = key;
        this.styleUrl = styleUrl;
        this.styleSelector = styleSelector;
        if (pairSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.PAIR).addAll(pairSimpleExtensions);
        }
        if (pairObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.PAIR).addAll(pairObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public StyleState getKey() {
        return this.key;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public URI getStyleUrl() {
        return this.styleUrl;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AbstractStyleSelector getAbstractStyleSelector() {
        return this.styleSelector;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setKey(StyleState key) {
        this.key = key;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setStyleUrl(URI styleUrl) {
        this.styleUrl = styleUrl;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAbstractStyleSelector(AbstractStyleSelector styleSelector) {
        this.styleSelector = styleSelector;
    }
}
