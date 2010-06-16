package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultImagePyramid extends DefaultAbstractObject implements ImagePyramid {

    private final int titleSize;
    private final int maxWidth;
    private final int maxHeight;
    private final GridOrigin gridOrigin;
    private final List<SimpleType> imagePyramidSimpleExtensions;
    private final List<AbstractObject> imagePyramidObjectExtensions;

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
    public DefaultImagePyramid(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            int titleSize, int maxWidth, int maxHeight, GridOrigin gridOrigin,
            List<SimpleType> imagePyramidSimpleExtensions, List<AbstractObject> imagePyramidObjectExtensions){
        super(objectSimpleExtensions, idAttributes);
        this.titleSize = titleSize;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.gridOrigin = gridOrigin;
        this.imagePyramidSimpleExtensions = (imagePyramidSimpleExtensions == null) ? EMPTY_LIST : imagePyramidSimpleExtensions;
        this.imagePyramidObjectExtensions = (imagePyramidObjectExtensions == null) ? EMPTY_LIST : imagePyramidObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public int getTitleSize() {return this.titleSize;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public int getMaxWidth() {return this.maxWidth;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public int getMaxHeight() {return this.maxHeight;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public GridOrigin getGridOrigin() {return this.gridOrigin;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getImagePyramidSimpleExtensions() {return this.imagePyramidSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getImagePyramidObjectExtensions() {return this.imagePyramidObjectExtensions;}

}
