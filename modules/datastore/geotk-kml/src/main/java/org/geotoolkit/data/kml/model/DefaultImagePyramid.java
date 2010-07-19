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
import org.geotoolkit.data.kml.xsd.SimpleType;
import static org.geotoolkit.data.kml.xml.KmlConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultImagePyramid extends DefaultAbstractObject implements ImagePyramid {

    private int titleSize;
    private int maxWidth;
    private int maxHeight;
    private GridOrigin gridOrigin;

    /**
     *
     */
    public DefaultImagePyramid() {
        this.titleSize = DEF_TITLE_SIZE;
        this.maxWidth = DEF_MAX_WIDTH;
        this.maxHeight = DEF_MAX_HEIGHT;
        this.gridOrigin = DEF_GRID_ORIGIN;
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param titleSize
     * @param maxWidth
     * @param maxHeight
     * @param gridOrigin
     * @param imagePyramidSimpleExtensions
     * @param imagePyramidObjectExtensions
     */
    public DefaultImagePyramid(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            int titleSize, int maxWidth, int maxHeight, GridOrigin gridOrigin,
            List<SimpleType> imagePyramidSimpleExtensions,
            List<AbstractObject> imagePyramidObjectExtensions) {
        super(objectSimpleExtensions, idAttributes);
        this.titleSize = titleSize;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.gridOrigin = gridOrigin;
        if (imagePyramidSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.IMAGE_PYRAMID).addAll(imagePyramidSimpleExtensions);
        }
        if (imagePyramidObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.IMAGE_PYRAMID).addAll(imagePyramidObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public int getTitleSize() {
        return this.titleSize;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public int getMaxWidth() {
        return this.maxWidth;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public int getMaxHeight() {
        return this.maxHeight;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public GridOrigin getGridOrigin() {
        return this.gridOrigin;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setTitleSize(int titleSize) {
        this.titleSize = titleSize;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setGridOrigin(GridOrigin gridOrigin) {
        this.gridOrigin = gridOrigin;
    }
}
