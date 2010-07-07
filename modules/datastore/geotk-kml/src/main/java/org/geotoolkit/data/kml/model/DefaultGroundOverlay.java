package org.geotoolkit.data.kml.model;

import java.awt.Color;
import java.util.List;
import org.geotoolkit.data.atom.model.AtomLink;
import org.geotoolkit.data.atom.model.AtomPersonConstruct;
import org.geotoolkit.data.kml.xsd.SimpleType;
import org.geotoolkit.data.xal.model.AddressDetails;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultGroundOverlay extends DefaultAbstractOverlay implements GroundOverlay {

    private double altitude;
    private AltitudeMode altitudeMode;
    private LatLonBox latLonBox;

    /**
     * 
     */
    public DefaultGroundOverlay() {
        this.altitude = DEF_ALTITUDE;
        this.altitudeMode = DEF_ALTITUDE_MODE;
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param name
     * @param visibility
     * @param open
     * @param author
     * @param atomLink
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
     * @param abstractOverlaySimpleExtensions
     * @param abstractOverlayObjectExtensions
     * @param altitude
     * @param altitudeMode
     * @param latLonBox
     * @param groundOverlaySimpleExtensions
     * @param groundOverlayObjectExtensions
     */
    public DefaultGroundOverlay(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            String name, boolean visibility, boolean open,
            AtomPersonConstruct author, AtomLink atomLink,
            String address, AddressDetails addressDetails,
            String phoneNumber, Object snippet,
            Object description, AbstractView view,
            AbstractTimePrimitive timePrimitive,
            String styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, Object extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            Color color, int drawOrder, Icon icon,
            List<SimpleType> abstractOverlaySimpleExtensions,
            List<AbstractObject> abstractOverlayObjectExtensions,
            double altitude, AltitudeMode altitudeMode, LatLonBox latLonBox,
            List<SimpleType> groundOverlaySimpleExtensions,
            List<AbstractObject> groundOverlayObjectExtensions) {
        super(objectSimpleExtensions, idAttributes,
                name, visibility, open,
                author, atomLink, address, addressDetails,
                phoneNumber, snippet, description, view,
                timePrimitive, styleUrl, styleSelector,
                region, extendedData,
                abstractFeatureSimpleExtensions,
                abstractFeatureObjectExtensions,
                color, drawOrder, icon,
                abstractOverlaySimpleExtensions,
                abstractOverlayObjectExtensions);
        this.altitude = altitude;
        this.altitudeMode = altitudeMode;
        this.latLonBox = latLonBox;
        if (groundOverlaySimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.GROUND_OVERLAY).addAll(groundOverlaySimpleExtensions);
        }
        if (groundOverlayObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.GROUND_OVERLAY).addAll(groundOverlayObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getAltitude() {
        return this.altitude;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AltitudeMode getAltitudeMode() {
        return this.altitudeMode;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public LatLonBox getLatLonBox() {
        return this.latLonBox;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAltitude(double altitude) {
        this.altitude = altitude;
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
    public void setLatLonBox(LatLonBox latLonBox) {
        this.latLonBox = latLonBox;
    }
}
