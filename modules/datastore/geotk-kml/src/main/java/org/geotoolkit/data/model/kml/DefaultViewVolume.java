package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.kml.KmlUtilities;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;
import static org.geotoolkit.data.model.KmlModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultViewVolume extends DefaultAbstractObject implements ViewVolume {

    /**
     * Angle, in decimal degrees, from the left side of the view volume to the camera's view vector.
     * A negative value of the angle corresponds to a field of view that is ‘left’ of the view vector.
     */
    private double leftFov;

    /**
     * Angle, in decimal degrees, from the camera's view vector to the right side of the view
     * volume. A positive value of the angle corresponds to a field of view that is ‘right’ of the view
     * vector.
     */
    private double rightFov;

    /**
     * Angle, in decimal degrees, from the the bottom side of the view volume to camera's view
     * vector.
     */
    private double bottomFov;

    /**
     * Angle, in decimal degrees, from the camera's view vector to the top side of the view volume.
     */
    private double topFov;

    /**
     * Length in meters of the view vector, which starts from the camera viewpoint and ends at the
     * kml:PhotoOverlay shape. The value shall be positive.
     */
    private double near;
    private List<SimpleType> viewVolumeSimpleExtensions;
    private List<AbstractObject> viewVolumeObjectExtensions;

    /**
     * 
     */
    public DefaultViewVolume(){
        this.leftFov = DEF_LEFT_FOV;
        this.rightFov = DEF_RIGHT_FOV;
        this.bottomFov = DEF_BOTTOM_FOV;
        this.topFov = DEF_TOP_FOV;
        this.near = DEF_NEAR;
        this.viewVolumeSimpleExtensions = EMPTY_LIST;
        this.viewVolumeObjectExtensions = EMPTY_LIST;
    }

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

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLeftFov(double leftFov) {this.leftFov = leftFov;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setRightFov(double rightFov) {this.rightFov = rightFov;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setBottomFov(double bottomFov) {this.bottomFov = bottomFov;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setTopFov(double topFov) {this.topFov = topFov;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setNear(double near) {this.near = near;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setViewVolumeSimpleExtensions(List<SimpleType> viewVolumeSimpleExtensions) {
        this.viewVolumeSimpleExtensions = viewVolumeSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setViewVolumeObjectExtensions(List<AbstractObject> viewVolumeObjectExtensions) {
        this.viewVolumeObjectExtensions = viewVolumeObjectExtensions;
    }

}
