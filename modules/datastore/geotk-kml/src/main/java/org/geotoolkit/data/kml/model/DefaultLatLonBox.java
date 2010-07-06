package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.KmlUtilities;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultLatLonBox extends DefaultAbstractLatLonBox implements LatLonBox {

    private double rotation;

    /**
     * 
     */
    public DefaultLatLonBox() {
        this.rotation = DEF_ROTATION;
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
     * @param rotation
     * @param latLonBoxSimpleExtensions
     * @param latLonBoxObjectExtensions
     */
    public DefaultLatLonBox(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            double north, double south, double east, double west,
            List<SimpleType> abstractLatLonBoxSimpleExtensions,
            List<AbstractObject> abstractLatLonBoxObjectExtensions,
            double rotation,
            List<SimpleType> latLonBoxSimpleExtensions,
            List<AbstractObject> latLonBoxObjectExtensions) {
        super(objectSimpleExtensions, idAttributes,
                north, south, east, west,
                abstractLatLonBoxSimpleExtensions,
                abstractLatLonBoxObjectExtensions);
        this.rotation = KmlUtilities.checkAngle180(rotation);
        if (latLonBoxSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.LAT_LON_BOX).addAll(latLonBoxSimpleExtensions);
        }
        if (latLonBoxObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.LAT_LON_BOX).addAll(latLonBoxObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getRotation() {
        return this.rotation;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setRotation(double rotation) {
        this.rotation = KmlUtilities.checkAngle180(rotation);
    }
}
