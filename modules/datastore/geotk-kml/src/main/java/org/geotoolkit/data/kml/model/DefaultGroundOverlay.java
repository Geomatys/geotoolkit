package org.geotoolkit.data.kml.model;

import java.awt.Color;
import java.util.List;
import org.geotoolkit.data.atom.model.AtomLink;
import org.geotoolkit.data.atom.model.AtomPersonConstruct;
import org.geotoolkit.data.xal.model.AddressDetails;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static java.util.Collections.*;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultGroundOverlay extends DefaultAbstractOverlay implements GroundOverlay {

    private double altitude;
    private AltitudeMode altitudeMode;
    private LatLonBox latLonBox;
    private List<SimpleType> groundOverlaySimpleExtensions;
    private List<AbstractObject> groundOverlayObjectExtensions;

    /**
     * 
     */
    public DefaultGroundOverlay(){
       this.altitude = DEF_ALTITUDE;
       this.altitudeMode = DEF_ALTITUDE_MODE;
       this.groundOverlaySimpleExtensions = EMPTY_LIST;
       this.groundOverlayObjectExtensions = EMPTY_LIST;
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param name
     * @param visibility
     * @param open
     * @param author
     * @param link
     * @param address
     * @param addressDetails
     * @param phoneNumber
     * @param snippet
     * @param description
     * @param view
     * @param timePrimitive
     * @param styleUrl
     * @param styleSelector
     * @param region
     * @param extendedData
     * @param abstractFeatureSimpleExtensions
     * @param abstractFeatureObjectExtensions
     * @param color
     * @param drawOrder
     * @param icon
     * @param abstractOveraySimpleExtensions
     * @param abstractOverlayObjectExtensions
     * @param altitude
     * @param altitudeMode
     * @param latLonBox
     * @param groundOverlaySimpleExtensions
     * @param groundOverlayObjectExtensions
     */
    public DefaultGroundOverlay(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String name, boolean visibility, boolean open, AtomPersonConstruct author, AtomLink link,
            String address, AddressDetails addressDetails, String phoneNumber, String snippet,
            String description, AbstractView view, AbstractTimePrimitive timePrimitive,
            String styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, ExtendedData extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            Color color, int drawOrder, Icon icon,
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
        this.groundOverlaySimpleExtensions = (groundOverlaySimpleExtensions == null) ? EMPTY_LIST : groundOverlaySimpleExtensions;
        this.groundOverlayObjectExtensions = (groundOverlayObjectExtensions == null) ? EMPTY_LIST : groundOverlayObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getAltitude() {return this.altitude;}

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
    public LatLonBox getLatLonBox() {return this.latLonBox;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getGroundOverlaySimpleExtensions() {return this.groundOverlaySimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getGroundOverlayObjectExtensions() {return this.groundOverlayObjectExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAltitude(double altitude) {this.altitude = altitude;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAltitudeMode(AltitudeMode altitudeMode) {this.altitudeMode = altitudeMode;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLatLonBox(LatLonBox latLonBox) {this.latLonBox= latLonBox;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setGroundOverlaySimpleExtensions(List<SimpleType> groundOverlaySimpleExtensions) {
        this.groundOverlaySimpleExtensions = groundOverlaySimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setGroundOverlayObjectExtensions(List<AbstractObject> groundOverlayObjectExtensions) {
        this.groundOverlayObjectExtensions = groundOverlayObjectExtensions;
    }

}
