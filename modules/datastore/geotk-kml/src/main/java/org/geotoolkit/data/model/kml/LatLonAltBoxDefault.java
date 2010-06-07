package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class LatLonAltBoxDefault extends AbstractLatLonBoxDefault implements LatLonAltBox {

    private final double minAltitude;
    private final double maxAltitude;
    private final AltitudeMode altitudeMode;
    private final List<SimpleType> latLonAltBoxSimpleExtensions;
    private final List<AbstractObject> latLonAltBoxObjectExtensions;

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
    public LatLonAltBoxDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            Angle180 north, Angle180 south, Angle180 east, Angle180 west,
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

}
