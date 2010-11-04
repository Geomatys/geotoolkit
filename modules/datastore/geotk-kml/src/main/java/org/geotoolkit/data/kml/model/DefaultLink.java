/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleTypeContainer;
import static org.geotoolkit.data.kml.xml.KmlConstants.*;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class DefaultLink extends DefaultAbstractObject implements Link {

    private String href;
    private RefreshMode refreshMode;
    private double refreshInterval;
    private ViewRefreshMode viewRefreshMode;
    private double viewRefreshTime;
    private double viewBoundScale;
    private String viewFormat;
    private String httpQuery;

    /**
     * 
     */
    public DefaultLink() {
        this.refreshMode = DEF_REFRESH_MODE;
        this.refreshInterval = DEF_REFRESH_INTERVAL;
        this.viewRefreshMode = DEF_VIEW_REFRESH_MODE;
        this.viewRefreshTime = DEF_VIEW_REFRESH_TIME;
        this.viewBoundScale = DEF_VIEW_BOUND_SCALE;
    }

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param href
     * @param basicLinkSimpleExtensions
     * @param basicLinkObjectExtension
     * @param refreshMode
     * @param refreshInterval
     * @param viewRefreshMode
     * @param viewRefreshTime
     * @param viewBoundScale
     * @param viewFormat
     * @param httpQuery
     * @param linkSimpleExtensions
     * @param linkObjectExtension
     */
    public DefaultLink(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            String href,
            List<SimpleTypeContainer> basicLinkSimpleExtensions,
            List<Object> basicLinkObjectExtension,
            RefreshMode refreshMode, double refreshInterval,
            ViewRefreshMode viewRefreshMode, double viewRefreshTime,
            double viewBoundScale, String viewFormat, String httpQuery,
            List<SimpleTypeContainer> linkSimpleExtensions,
            List<Object> linkObjectExtension) {
        super(objectSimpleExtensions, idAttributes);
        this.href = href;
        if (basicLinkSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.BASIC_LINK).addAll(basicLinkSimpleExtensions);
        }
        if (basicLinkObjectExtension != null) {
            this.extensions().complexes(Extensions.Names.BASIC_LINK).addAll(basicLinkObjectExtension);
        }

        this.refreshMode = refreshMode;
        this.refreshInterval = refreshInterval;
        this.viewRefreshMode = viewRefreshMode;
        this.viewRefreshTime = viewRefreshTime;
        this.viewBoundScale = viewBoundScale;
        this.viewFormat = viewFormat;
        this.httpQuery = httpQuery;
        if (linkSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.LINK).addAll(linkSimpleExtensions);
        }
        if (linkObjectExtension != null) {
            this.extensions().complexes(Extensions.Names.LINK).addAll(linkObjectExtension);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getHref() {
        return this.href;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public RefreshMode getRefreshMode() {
        return this.refreshMode;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getRefreshInterval() {
        return this.refreshInterval;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ViewRefreshMode getViewRefreshMode() {
        return this.viewRefreshMode;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getViewRefreshTime() {
        return this.viewRefreshTime;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getViewBoundScale() {
        return this.viewBoundScale;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getViewFormat() {
        return this.viewFormat;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getHttpQuery() {
        return this.httpQuery;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public IdAttributes getIdAttributes() {
        return this.idAttributes;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setHref(String href) {
        this.href = href;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setRefreshMode(RefreshMode refreshMode) {
        this.refreshMode = refreshMode;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setRefreshInterval(double refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setViewRefreshMode(ViewRefreshMode viewRefreshMode) {
        this.viewRefreshMode = viewRefreshMode;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setViewRefreshTime(double viewRefreshTime) {
        this.viewRefreshTime = viewRefreshTime;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setViewBoundScale(double viewBoundScale) {
        this.viewBoundScale = viewBoundScale;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setViewFormat(String viewFormat) {
        this.viewFormat = viewFormat;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setHttpQuery(String httpQuery) {
        this.httpQuery = httpQuery;
    }
}
