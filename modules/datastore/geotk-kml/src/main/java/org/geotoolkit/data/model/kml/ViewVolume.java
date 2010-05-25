package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface ViewVolume extends AbstractObject {

    public Angle180 getLeftFov();
    public Angle180 getRightFov();
    public Angle90 getBottomFov();
    public Angle90 getTopFov();
    public double getNear();
    public List<SimpleType> getViewVolumeSimpleExtensions();
    public List<AbstractObject> getViewVolumeObjectExtensions();

}
