package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.KmlUtilities;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static java.util.Collections.*;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultViewVolume extends DefaultAbstractObject implements ViewVolume {

    private double leftFov;
    private double rightFov;
    private double bottomFov;
    private double topFov;
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
        this.bottomFov = KmlUtilities.checkAngle90(bottomFov);
        this.topFov = KmlUtilities.checkAngle90(topFov);
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
    public void setLeftFov(double leftFov) {this.leftFov = KmlUtilities.checkAngle180(leftFov);}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setRightFov(double rightFov) {this.rightFov = KmlUtilities.checkAngle180(rightFov);}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setBottomFov(double bottomFov) {this.bottomFov = KmlUtilities.checkAngle90(bottomFov);}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setTopFov(double topFov) {this.topFov = KmlUtilities.checkAngle90(topFov);}

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
