package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultViewVolume extends DefaultAbstractObject implements ViewVolume {

    private final Angle180 leftFov;
    private final Angle180 rightFov;
    private final Angle90 bottomFov;
    private final Angle90 topFov;
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
            Angle180 leftFov, Angle180 rightFov, Angle90 bottomFov, Angle90 topFov, double near,
            List<SimpleType> viewVolumeSimpleExtensions, List<AbstractObject> viewVolumeObjectExtensions){
        super(objectSimpleExtensions, idAttributes);
        this.leftFov = leftFov;
        this.rightFov = rightFov;
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
    public Angle180 getLeftFov() {return this.leftFov;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Angle180 getRightFov() {return this.rightFov;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Angle90 getBottomFov() {return this.bottomFov;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Angle90 getTopFov() {return this.topFov;}

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
