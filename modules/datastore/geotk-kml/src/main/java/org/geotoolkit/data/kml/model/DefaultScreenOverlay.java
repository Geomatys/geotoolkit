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
import org.geotoolkit.data.kml.KmlUtilities;
import org.geotoolkit.data.atom.model.AtomLink;
import org.geotoolkit.data.atom.model.AtomPersonConstruct;
import org.geotoolkit.data.kml.xsd.SimpleType;
import org.geotoolkit.data.xal.model.AddressDetails;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultScreenOverlay extends DefaultAbstractOverlay implements ScreenOverlay {

    private Vec2 overlayXY;
    private Vec2 screenXY;
    private Vec2 rotationXY;
    private Vec2 size;
    private double rotation;

    /**
     *
     */
    public DefaultScreenOverlay() {
        this.rotation = DEF_ROTATION;
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
     * @param overlayXY
     * @param screenXY
     * @param rotationXY
     * @param size
     * @param rotation
     * @param screenOverlaySimpleExtensions
     * @param screendOverlayObjectExtensions
     */
    public DefaultScreenOverlay(List<SimpleType> objectSimpleExtensions,
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
            List<AbstractObject> abstractOverlayObjectExtensions,
            Vec2 overlayXY, Vec2 screenXY, Vec2 rotationXY, Vec2 size, double rotation,
            List<SimpleType> screenOverlaySimpleExtensions,
            List<AbstractObject> screendOverlayObjectExtensions) {
        super(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, atomLink,
                address, addressDetails, phoneNumber, snippet,
                description, view, timePrimitive, styleUrl, styleSelector,
                region, extendedData,
                abstractFeatureSimpleExtensions,
                abstractFeatureObjectExtensions,
                color, drawOrder, icon,
                abstractOverlaySimpleExtensions,
                abstractOverlayObjectExtensions);
        this.overlayXY = overlayXY;
        this.screenXY = screenXY;
        this.rotationXY = rotationXY;
        this.size = size;
        this.rotation = KmlUtilities.checkAngle180(rotation);
        if (screenOverlaySimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.SCREEN_OVERLAY).addAll(screenOverlaySimpleExtensions);
        }
        if (screendOverlayObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.SCREEN_OVERLAY).addAll(screendOverlayObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Vec2 getOverlayXY() {
        return this.overlayXY;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Vec2 getScreenXY() {
        return this.screenXY;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Vec2 getRotationXY() {
        return this.rotationXY;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Vec2 getSize() {
        return this.size;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getRotation() {
        return this.rotation;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setOverlayXY(Vec2 overlayXY) {
        this.overlayXY = overlayXY;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setScreenXY(Vec2 screenXY) {
        this.screenXY = screenXY;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setRotationXY(Vec2 rotationXY) {
        this.rotationXY = rotationXY;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setSize(Vec2 size) {
        this.size = size;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setRotation(double rotation) {
        this.rotation = KmlUtilities.checkAngle180(rotation);
    }
}
