package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;
import static org.geotoolkit.data.model.KmlModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultLink extends DefaultAbstractObject implements Link {

    private String href;
    private List<SimpleType> basicLinkSimpleExtensions;
    private List<AbstractObject> basicLinkObjectExtensions;
    
    private RefreshMode refreshMode;
    private double refreshInterval;
    private ViewRefreshMode viewRefreshMode;
    private double viewRefreshTime;
    private double viewBoundScale;
    private String viewFormat;
    private String httpQuery;
    private List<SimpleType> linkSimpleExtensions;
    private List<AbstractObject> linkObjectExtensions;

    /**
     * 
     */
    public DefaultLink(){
        this.basicLinkSimpleExtensions = EMPTY_LIST;
        this.basicLinkObjectExtensions = EMPTY_LIST;

        this.refreshMode = DEF_REFRESH_MODE;
        this.refreshInterval = DEF_REFRESH_INTERVAL;
        this.viewRefreshMode = DEF_VIEW_REFRESH_MODE;
        this.viewRefreshTime = DEF_VIEW_REFRESH_TIME;
        this.viewBoundScale = DEF_VIEW_BOUND_SCALE;
        this.linkSimpleExtensions = EMPTY_LIST;
        this.linkObjectExtensions = EMPTY_LIST;
    }

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

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setHref(String href) {this.href = href;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setBasicLinkSimpleExtensions(List<SimpleType> basicLinkSimpleExtensions) {
        this.basicLinkSimpleExtensions = basicLinkSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setBasicLinkObjectExtensions(List<AbstractObject> basicLinkObjectExtensions) {
        this.basicLinkObjectExtensions = basicLinkObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setRefreshMode(RefreshMode refreshMode) {this.refreshMode = refreshMode;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setRefreshInterval(double refreshInterval) {this.refreshInterval = refreshInterval;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setViewRefreshMode(ViewRefreshMode viewRefreshMode) {this.viewRefreshMode = viewRefreshMode;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setViewRefreshTime(double viewRefreshTime) {this.viewRefreshTime = viewRefreshTime;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setViewBoundScale(double viewBoundScale) {this.viewBoundScale = viewBoundScale;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setViewFormat(String viewFormat) {this.viewFormat = viewFormat;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setHttpQuery(String httpQuery) {this.httpQuery = httpQuery;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLinkSimpleExtensions(List<SimpleType> linkSimpleExtensions) {
        this.linkSimpleExtensions = linkSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLinkObjectExtensions(List<AbstractObject> linkObjectExtensions) {
        this.linkObjectExtensions = linkObjectExtensions;
    }

}
