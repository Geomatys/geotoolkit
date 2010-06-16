package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultLink extends DefaultAbstractObject implements Link {

    private final  String href;
    private final List<SimpleType> basicLinkSimpleExtensions;
    private final List<AbstractObject> basicLinkObjectExtensions;
    
    private final RefreshMode refreshMode;
    private final double refreshInterval;
    private final ViewRefreshMode viewRefreshMode;
    private final double viewRefreshTime;
    private final double viewBoundScale;
    private final String viewFormat;
    private final String httpQuery;
    private final List<SimpleType> linkSimpleExtensions;
    private final List<AbstractObject> linkObjectExtensions;

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param href
     * @param basicLinkSimpleExtensions
     * @param basicLinkObjectExtensions
     * @param refreshMode
     * @param refreshInterval
     * @param viewRefreshMode
     * @param viewRefreshTime
     * @param viewBoundScale
     * @param viewFormat
     * @param httpQuery
     * @param linkSimpleExtensions
     * @param linkObjectExtensions
     */
    public DefaultLink(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String href, List<SimpleType> basicLinkSimpleExtensions, List<AbstractObject> basicLinkObjectExtensions,
            RefreshMode refreshMode, double refreshInterval, ViewRefreshMode viewRefreshMode, double viewRefreshTime,
            double viewBoundScale, String viewFormat, String httpQuery,
            List<SimpleType> linkSimpleExtensions, List<AbstractObject> linkObjectExtensions){
        super(objectSimpleExtensions, idAttributes);
        this.href = href;
        this.basicLinkSimpleExtensions = (basicLinkSimpleExtensions == null) ? EMPTY_LIST : basicLinkSimpleExtensions;
        this.basicLinkObjectExtensions = (basicLinkObjectExtensions == null) ? EMPTY_LIST : basicLinkObjectExtensions;

        this.refreshMode = refreshMode;
        this.refreshInterval = refreshInterval;
        this.viewRefreshMode = viewRefreshMode;
        this.viewRefreshTime = viewRefreshTime;
        this.viewBoundScale = viewBoundScale;
        this.viewFormat = viewFormat;
        this.httpQuery = httpQuery;
        this.linkSimpleExtensions = (linkSimpleExtensions == null) ? EMPTY_LIST : linkSimpleExtensions;
        this.linkObjectExtensions = (linkObjectExtensions == null) ? EMPTY_LIST : linkObjectExtensions;

    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getHref() {return this.href;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getBasicLinkSimpleExtensions() {return this.basicLinkSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getBasicLinkObjectExtensions() {return this.basicLinkObjectExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public RefreshMode getRefreshMode() {return this.refreshMode;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getRefreshInterval() {return this.refreshInterval;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ViewRefreshMode getViewRefreshMode() {return this.viewRefreshMode;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getViewRefreshTime() {return this.viewRefreshTime;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getViewBoundScale() {return this.viewBoundScale;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getViewFormat() {return this.viewFormat;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getHttpQuery() {return this.httpQuery;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getLinkSimpleExtensions() {return this.linkSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getLinkObjectExtensions() {return this.linkObjectExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getObjectSimpleExtensions() {return this.objectSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public IdAttributes getIdAttributes() {return this.idAttributes;}

}
