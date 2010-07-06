package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;

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
