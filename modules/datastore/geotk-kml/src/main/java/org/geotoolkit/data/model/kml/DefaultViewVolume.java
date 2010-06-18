package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.kml.KmlUtilities;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultViewVolume extends DefaultAbstractObject implements ViewVolume {

    private final double leftFov;
    private final double rightFov;
    private final double bottomFov;
    private final double topFov;
    private final double near;
    private final List<SimpleType> viewVolumeSimpleExtensions;
    private final List<AbstractObject> viewVolumeObjectExtensions;

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param leftFov
     * @param rightFov
     * @param bottomFov
     * @param topFov
     * @param viewVolumeSimpleExtensions
     * @param viewVolumeObjectExtensions
     */
    public DefaultViewVolume(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            double leftFov, double rightFov, double bottomFov, double topFov, double near,
            List<SimpleType> viewVolumeSimpleExtensions, List<AbstractObject> viewVolumeObjectExtensions){
        super(objectSimpleExtensions, idAttributes);
        this.leftFov = KmlUtilities.checkAngle180(leftFov);
        this.rightFov = KmlUtilities.checkAngle180(rightFov);
        this.bottomFov = bottomFov;
        this.topFov = topFov;
        this.near = near;
        this.viewVolumeSimpleExtensions = (viewVolumeSimpleExtensions == null) ? EMPTY_LIST : viewVolumeSimpleExtensions;
        this.viewVolumeObjectExtensions = (viewVolumeObjectExtensions == null) ? EMPTY_LIST : viewVolumeObjectExtensions;
    }

    /**
     * 
     * @{@inheritDoc }
     */
    @Override
    public double getLeftFov() {return this.leftFov;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getRightFov() {return this.rightFov;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getBottomFov() {return this.bottomFov;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getTopFov() {return this.topFov;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getNear() {return this.near;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getViewVolumeSimpleExtensions() {return this.viewVolumeSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getViewVolumeObjectExtensions() {return this.viewVolumeObjectExtensions;}

}
