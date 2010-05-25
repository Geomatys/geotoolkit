package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class LatLonAltBoxDefault extends AbstractLatLonBoxDefault implements LatLonAltBox {

    private double minAltitude;
    private double maxAltitude;
    private AltitudeMode altitudeMode;
    private List<SimpleType> latLonAltBoxSimpleExtensions;
    private List<AbstractObject> latLonAltBoxObjectExtensions;

    public LatLonAltBoxDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            Angle180 north, Angle180 south, Angle180 east, Angle180 west,
            List<SimpleType> abstractLatLonBoxSimpleExtensions, List<AbstractObject> abstractLatLonBoxObjectExtensions,
            double minAltitude, double maxAltitude, AltitudeMode altitudeMode,
            List<SimpleType> latLonAltBoxSimpleExtensions, List<AbstractObject> latLonAltBoxObjectExtensions){
        super(objectSimpleExtensions, idAttributes, north, south, east, west, abstractLatLonBoxSimpleExtensions, abstractLatLonBoxObjectExtensions);
        this.minAltitude = minAltitude;
        this.maxAltitude = maxAltitude;
        this.altitudeMode = altitudeMode;
        this.latLonAltBoxSimpleExtensions = latLonAltBoxSimpleExtensions;
        this.latLonAltBoxObjectExtensions = latLonAltBoxObjectExtensions;
    }

    @Override
    public double getMinAltitude() {return this.minAltitude;}

    @Override
    public double getMaxAltitude() {return this.maxAltitude;}

    @Override
    public AltitudeMode getAltitudeMode() {return this.altitudeMode;}

    @Override
    public List<SimpleType> getLatLonAltBoxSimpleExtensions() {return this.latLonAltBoxSimpleExtensions;}

    @Override
    public List<AbstractObject> getLatLonAltBoxObjectExtensions() {return this.latLonAltBoxObjectExtensions;}

}
