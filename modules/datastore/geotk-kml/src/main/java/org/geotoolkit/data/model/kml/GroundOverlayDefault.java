package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.atom.AtomLink;
import org.geotoolkit.data.model.atom.AtomPersonConstruct;
import org.geotoolkit.data.model.xal.AddressDetails;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class GroundOverlayDefault extends AbstractOverlayDefault implements GroundOverlay {

    private double altitude;
    private AltitudeMode altitudeMode;
    private LatLonBox latLonBox;
    private List<SimpleType> groundOverlaySimpleExtensions;
    private List<AbstractObject> groundOverlayObjectExtensions;

    public GroundOverlayDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String name, boolean visibility, boolean open, AtomPersonConstruct author, AtomLink link,
            String address, AddressDetails addressDetails, String phoneNumber, String snippet,
            String description, AbstractView view, AbstractTimePrimitive timePrimitive,
            String styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, ExtendedData extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            Color color, int drawOrder, Link icon,
            List<SimpleType> abstractOveraySimpleExtensions, List<AbstractObject> abstractOverlayObjectExtensions,
            double altitude, AltitudeMode altitudeMode, LatLonBox latLonBox,
            List<SimpleType> groundOverlaySimpleExtensions, List<AbstractObject> groundOverlayObjectExtensions){
        super(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, link,
                address, addressDetails, phoneNumber, snippet,
                description, view, timePrimitive,
                styleUrl, styleSelector, region, extendedData,
                abstractFeatureSimpleExtensions, abstractFeatureObjectExtensions,
                color, drawOrder, icon, abstractOveraySimpleExtensions, abstractOverlayObjectExtensions);
        this.altitude = altitude;
        this.altitudeMode = altitudeMode;
        this.latLonBox = latLonBox;
        this.groundOverlaySimpleExtensions = groundOverlaySimpleExtensions;
        this.groundOverlayObjectExtensions = groundOverlayObjectExtensions;
    }

    @Override
    public double getAltitude() {return this.altitude;}

    @Override
    public AltitudeMode getAltitudeMode() {return this.altitudeMode;}

    @Override
    public LatLonBox getLatLonBox() {return this.latLonBox;}

    @Override
    public List<SimpleType> getGroundOverlaySimpleExtensions() {return this.groundOverlaySimpleExtensions;}

    @Override
    public List<AbstractObject> getGroundOverlayObjectExtensions() {return this.groundOverlayObjectExtensions;}

}
