package org.geotoolkit.data.model.kml;

/**
 *
 * @author Samuel Andrés
 */
public interface ImagePyramid extends AbstractObject {

    public int getTitleSize();
    public int getMaxWidth();
    public int getMaxHeight();
    public GridOrigin getGridOrigin();

}
