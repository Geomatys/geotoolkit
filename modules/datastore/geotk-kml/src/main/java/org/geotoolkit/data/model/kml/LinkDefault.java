package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class LinkDefault extends AbstractObjectDefault implements Link {

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

    public LinkDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String href, List<SimpleType> basicLinkSimpleExtensions, List<AbstractObject> basicLinkObjectExtensions,
            RefreshMode refreshMode, double refreshInterval, ViewRefreshMode viewRefreshMode, double viewRefreshTime,
            double viewBoundScale, String viewFormat, String httpQuery,
            List<SimpleType> linkSimpleExtensions, List<AbstractObject> linkObjectExtensions){
        super(objectSimpleExtensions, idAttributes);
        this.href = href;
        this.refreshMode = refreshMode;
        this.refreshInterval = refreshInterval;
        this.viewRefreshMode = viewRefreshMode;
        this.viewBoundScale = viewBoundScale;
        this.viewFormat = viewFormat;
        this.httpQuery = httpQuery;
        this.linkSimpleExtensions = linkSimpleExtensions;
        this.linkObjectExtensions = linkObjectExtensions;

    }

    @Override
    public String getHref() {return this.href;}

    @Override
    public List<SimpleType> getBasicLinkSimpleExtensions() {return this.basicLinkSimpleExtensions;}

    @Override
    public List<AbstractObject> getBasicLinkObjectExtensions() {return this.basicLinkObjectExtensions;}

    @Override
    public RefreshMode getRefreshMode() {return this.refreshMode;}

    @Override
    public double getRefreshInterval() {return this.refreshInterval;}

    @Override
    public ViewRefreshMode getViewRefreshMode() {return this.viewRefreshMode;}

    @Override
    public double getViewRefreshTime() {return this.viewRefreshTime;}

    @Override
    public double getViewBoundScale() {return this.viewBoundScale;}

    @Override
    public String getViewFormat() {return this.viewFormat;}

    @Override
    public String getHttpQuery() {return this.httpQuery;}

    @Override
    public List<SimpleType> getLinkSimpleExtensions() {return this.linkSimpleExtensions;}

    @Override
    public List<AbstractObject> getLinkObjectExtensions() {return this.linkObjectExtensions;}

    @Override
    public List<SimpleType> getObjectSimpleExtensions() {return this.objectSimpleExtensions;}

    @Override
    public IdAttributes getIdAttributes() {return this.idAttributes;}

}
