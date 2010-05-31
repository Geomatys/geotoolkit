package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class ViewVolumeDefault extends AbstractObjectDefault implements ViewVolume {

    private Angle180 leftFov;
    private Angle180 rightFov;
    private Angle90 bottomFov;
    private Angle90 topFov;
    private double near;
    private List<SimpleType> viewVolumeSimpleExtensions;
    private List<AbstractObject> viewVolumeObjectExtensions;

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
    public ViewVolumeDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            Angle180 leftFov, Angle180 rightFov, Angle90 bottomFov, Angle90 topFov,
            List<SimpleType> viewVolumeSimpleExtensions, List<AbstractObject> viewVolumeObjectExtensions){
        super(objectSimpleExtensions, idAttributes);
        this.leftFov = leftFov;
        this.rightFov = rightFov;
        this.bottomFov = bottomFov;
        this.topFov = topFov;
        this.viewVolumeSimpleExtensions = viewVolumeSimpleExtensions;
        this.viewVolumeObjectExtensions = viewVolumeObjectExtensions;
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
