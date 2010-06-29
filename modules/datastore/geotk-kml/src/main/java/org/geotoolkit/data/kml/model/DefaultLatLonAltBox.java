package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static java.util.Collections.*;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultLatLonAltBox extends DefaultAbstractLatLonBox implements LatLonAltBox {

    private double minAltitude;
    private double maxAltitude;
    private AltitudeMode altitudeMode;
    private List<SimpleType> latLonAltBoxSimpleExtensions;
    private List<AbstractObject> latLonAltBoxObjectExtensions;

    /**
     * 
     */
    public DefaultLatLonAltBox(){
        this.minAltitude = DEF_MIN_ALTITUDE;
        this.maxAltitude = DEF_MAX_ALTITUDE;
        this.altitudeMode = DEF_ALTITUDE_MODE;
        this.latLonAltBoxSimpleExtensions = EMPTY_LIST;
        this.latLonAltBoxObjectExtensions = EMPTY_LIST;
    }
    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param north
     * @param south
     * @param east
     * @param west
     * @param abstractLatLonBoxSimpleExtensions
     * @param abstractLatLonBoxObjectExtensions
     * @param minAltitude
     * @param maxAltitude
     * @param altitudeMode
     * @param latLonAltBoxSimpleExtensions
     * @param latLonAltBoxObjectExtensions
     */
    public DefaultLatLonAltBox(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            double north, double south, double east, double west,
            List<SimpleType> abstractLatLonBoxSimpleExtensions, List<AbstractObject> abstractLatLonBoxObjectExtensions,
            double minAltitude, double maxAltitude, AltitudeMode altitudeMode,
            List<SimpleType> latLonAltBoxSimpleExtensions, List<AbstractObject> latLonAltBoxObjectExtensions){
        super(objectSimpleExtensions, idAttributes, north, south, east, west, abstractLatLonBoxSimpleExtensions, abstractLatLonBoxObjectExtensions);
        this.minAltitude = minAltitude;
        this.maxAltitude = maxAltitude;
        this.altitudeMode = altitudeMode;
        this.latLonAltBoxSimpleExtensions = (latLonAltBoxSimpleExtensions == null) ? EMPTY_LIST : latLonAltBoxSimpleExtensions;
        this.latLonAltBoxObjectExtensions = (latLonAltBoxObjectExtensions == null) ? EMPTY_LIST : latLonAltBoxObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getMinAltitude() {return this.minAltitude;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getMaxAltitude() {return this.maxAltitude;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AltitudeMode getAltitudeMode() {return this.altitudeMode;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getLatLonAltBoxSimpleExtensions() {return this.latLonAltBoxSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getLatLonAltBoxObjectExtensions() {return this.latLonAltBoxObjectExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setMinAltitude(double minAltitude) {
        this.minAltitude = minAltitude;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setMaxAltitude(double maxAltitude) {
        this.maxAltitude = maxAltitude;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAltitudeMode(AltitudeMode altitudeMode) {
        this.altitudeMode = altitudeMode;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLatLonAltBoxSimpleExtensions(List<SimpleType> latLonAltBoxSimpleExtensions) {
        this.latLonAltBoxSimpleExtensions = latLonAltBoxSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLatLonAltBoxObjectExtensions(List<AbstractObject> latLonAltBoxObjectExtensions) {
        this.latLonAltBoxObjectExtensions = latLonAltBoxObjectExtensions;
    }

}
