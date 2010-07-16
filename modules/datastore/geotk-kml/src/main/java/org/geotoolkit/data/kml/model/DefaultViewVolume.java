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
import org.geotoolkit.data.kml.KmlUtilities;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultViewVolume extends DefaultAbstractObject implements ViewVolume {

    private final Extensions exts = new Extensions();
    private double leftFov;
    private double rightFov;
    private double bottomFov;
    private double topFov;
    private double near;

    /**
     * 
     */
    public DefaultViewVolume() {
        this.leftFov = DEF_LEFT_FOV;
        this.rightFov = DEF_RIGHT_FOV;
        this.bottomFov = DEF_BOTTOM_FOV;
        this.topFov = DEF_TOP_FOV;
        this.near = DEF_NEAR;
    }

    /**
     * 
     * @param idAttributes
     * @param leftFov
     * @param rightFov
     * @param bottomFov
     * @param topFov
     * @param near
     */
    public DefaultViewVolume(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            double leftFov, double rightFov,
            double bottomFov, double topFov, double near,
            List<SimpleType> viewVolumeSimpleExtensions,
            List<AbstractObject> viewVolumeObjectExtensions) {
        super(objectSimpleExtensions, idAttributes);
        this.leftFov = KmlUtilities.checkAngle180(leftFov);
        this.rightFov = KmlUtilities.checkAngle180(rightFov);
        this.bottomFov = KmlUtilities.checkAngle90(bottomFov);
        this.topFov = KmlUtilities.checkAngle90(topFov);
        this.near = near;
        if (viewVolumeSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.VIEW_VOLUME).addAll(viewVolumeSimpleExtensions);
        }
        if (viewVolumeObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.VIEW_VOLUME).addAll(viewVolumeObjectExtensions);
        }
    }

    /**
     * 
     * @{@inheritDoc }
     */
    @Override
    public double getLeftFov() {
        return this.leftFov;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getRightFov() {
        return this.rightFov;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getBottomFov() {
        return this.bottomFov;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getTopFov() {
        return this.topFov;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getNear() {
        return this.near;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLeftFov(double leftFov) {
        this.leftFov = KmlUtilities.checkAngle180(leftFov);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setRightFov(double rightFov) {
        this.rightFov = KmlUtilities.checkAngle180(rightFov);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setBottomFov(double bottomFov) {
        this.bottomFov = KmlUtilities.checkAngle90(bottomFov);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setTopFov(double topFov) {
        this.topFov = KmlUtilities.checkAngle90(topFov);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setNear(double near) {
        this.near = near;
    }
}
