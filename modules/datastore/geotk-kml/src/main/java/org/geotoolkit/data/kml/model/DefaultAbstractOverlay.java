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

import java.awt.Color;
import java.net.URI;
import java.util.List;
import org.geotoolkit.atom.model.AtomLink;
import org.geotoolkit.atom.model.AtomPersonConstruct;
import org.geotoolkit.data.kml.xsd.SimpleType;
import org.geotoolkit.xal.model.AddressDetails;
import org.opengis.feature.type.FeatureType;
import static org.geotoolkit.data.kml.xml.KmlConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public abstract class DefaultAbstractOverlay extends DefaultAbstractFeature implements AbstractOverlay {

    protected Color color;
    protected int drawOrder = DEF_DRAW_ORDER;
    protected Icon icon;

    /**
     * 
     */
    protected DefaultAbstractOverlay(FeatureType type) {
        super(type);
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
     * @param color
     * @param drawOrder
     * @param icon
     * @param abstractOverlaySimpleExtensions
     * @param abstractOverlayObjectExtensions
     */
    protected DefaultAbstractOverlay(FeatureType type,
            List<SimpleType> objectSimpleExtensions,
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
            Color color, int drawOrder, Icon icon,
            List<SimpleType> abstractOverlaySimpleExtensions,
            List<AbstractObject> abstractOverlayObjectExtensions) {
        super(type, objectSimpleExtensions, idAttributes,
                name, visibility, open,
                author, atomLink, address, addressDetails,
                phoneNumber, snippet, description, view,
                timePrimitive, styleUrl, styleSelector,
                region, extendedData,
                abstractFeatureSimpleExtensions,
                abstractFeatureObjectExtensions);
        this.color = color;
        this.drawOrder = drawOrder;
        this.icon = icon;
        if (abstractOverlaySimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.OVERLAY).addAll(abstractOverlaySimpleExtensions);
        }
        if (abstractOverlayObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.OVERLAY).addAll(abstractOverlayObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Color getColor() {
        return this.color;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public int getDrawOrder() {
        return this.drawOrder;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Icon getIcon() {
        return this.icon;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setDrawOrder(int drawOrder) {
        this.drawOrder = drawOrder;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setIcon(Icon icon) {
        this.icon = icon;
    }
}
