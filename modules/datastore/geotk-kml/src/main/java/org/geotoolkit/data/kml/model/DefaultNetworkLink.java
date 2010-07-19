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

import java.net.URI;
import java.util.List;
import org.geotoolkit.atom.model.AtomLink;
import org.geotoolkit.atom.model.AtomPersonConstruct;
import org.geotoolkit.data.kml.xsd.SimpleType;
import org.geotoolkit.xal.model.AddressDetails;
import static org.geotoolkit.data.kml.xml.KmlConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultNetworkLink extends DefaultAbstractFeature implements NetworkLink {

    private boolean refreshVisibility;
    private boolean flyToView;
    private Link link;

    /**
     * 
     */
    public DefaultNetworkLink() {
        super(KmlModelConstants.TYPE_KML_ENTITY);
        this.refreshVisibility = DEF_REFRESH_VISIBILITY;
        this.flyToView = DEF_FLY_TO_VIEW;
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param name
     * @param visibility
     * @param open
     * @param author
     * @param atomLink
     * @param address
     * @param addressDetails
     * @param phoneNumber
     * @param snippet
     * @param description
     * @param view
     * @param timePrimitive
     * @param styleUrl
     * @param styleSelector
     * @param region
     * @param extendedData
     * @param abstractFeatureSimpleExtensions
     * @param abstractFeatureObjectExtensions
     * @param refreshVisibility
     * @param flyToView
     * @param link
     * @param networkLinkSimpleExtensions
     * @param networkLinkObjectExtensions
     */
    public DefaultNetworkLink(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            String name, boolean visibility, boolean open,
            AtomPersonConstruct author, AtomLink atomLink,
            String address, AddressDetails addressDetails,
            String phoneNumber, Object snippet,
            Object description, AbstractView view,
            AbstractTimePrimitive timePrimitive,
            URI styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, Object extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            boolean refreshVisibility, boolean flyToView, Link link,
            List<SimpleType> networkLinkSimpleExtensions,
            List<AbstractObject> networkLinkObjectExtensions) {
        super(KmlModelConstants.TYPE_KML_ENTITY,
                objectSimpleExtensions, idAttributes,
                name, visibility, open,
                author, atomLink,
                address, addressDetails,
                phoneNumber, snippet, description, view, timePrimitive,
                styleUrl, styleSelector,
                region, extendedData,
                abstractFeatureSimpleExtensions,
                abstractFeatureObjectExtensions);
        this.refreshVisibility = refreshVisibility;
        this.flyToView = flyToView;
        this.link = link;
        if (networkLinkSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.NETWORK_LINK).addAll(networkLinkSimpleExtensions);
        }
        if (networkLinkObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.NETWORK_LINK).addAll(networkLinkObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public boolean getRefreshVisibility() {
        return this.refreshVisibility;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public boolean getFlyToView() {
        return this.flyToView;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Link getLink() {
        return this.link;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setRefreshVisibility(boolean refreshVisibility) {
        this.refreshVisibility = refreshVisibility;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setFlyToView(boolean flyToView) {
        this.flyToView = flyToView;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLink(Link link) {
        this.link = link;
    }
}
