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
public class DefaultModel extends DefaultAbstractGeometry implements Model {

    private AltitudeMode altitudeMode;
    private Location location;
    private Orientation orientation;
    private Scale scale;
    private Link link;
    private ResourceMap resourceMap;

    public DefaultModel() {
        this.altitudeMode = DEF_ALTITUDE_MODE;
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractGeometrySimpleExtensions
     * @param abstractGeometryObjectExtensions
     * @param altitudeMode
     * @param location
     * @param orientation
     * @param scale
     * @param link
     * @param resourceMap
     * @param modelSimpleExtensions
     * @param modelObjectExtensions
     */
    public DefaultModel(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleTypeContainer> abstractGeometrySimpleExtensions,
            List<Object> abstractGeometryObjectExtensions,
            AltitudeMode altitudeMode, Location location, Orientation orientation,
            Scale scale, Link link, ResourceMap resourceMap,
            List<SimpleTypeContainer> modelSimpleExtensions,
            List<Object> modelObjectExtensions) {
        super(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions,
                abstractGeometryObjectExtensions);
        this.altitudeMode = altitudeMode;
        this.location = location;
        this.orientation = orientation;
        this.scale = scale;
        this.link = link;
        this.resourceMap = resourceMap;
        if (modelSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.MODEL).addAll(modelSimpleExtensions);
        }
        if (modelObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.MODEL).addAll(modelObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AltitudeMode getAltitudeMode() {
        return this.altitudeMode;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Location getLocation() {
        return this.location;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Orientation getOrientation() {
        return this.orientation;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Scale getScale() {
        return this.scale;
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
    public ResourceMap getRessourceMap() {
        return this.resourceMap;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAltitudeMode(AltitudeMode altitudeMode) {
        this.altitudeMode = altitudeMode;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setScale(Scale scale) {
        this.scale = scale;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLink(Link link) {
        this.link = link;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setRessourceMap(ResourceMap resourceMap) {
        this.resourceMap = resourceMap;
    }
}
