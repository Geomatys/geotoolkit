package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface PhotoOverlay extends AbstractOverlay {

    public Angle180 getRotation();
    public ViewVolume getViewVolume();
    public ImagePyramid getImagePyramid();
    public Point getPoint();
    public Shape getShape();
    public List<SimpleType> getPhotoOverlaySimpleExtensions();
    public List<AbstractObject> getPhotoOverlayObjectExtensions();

}
