package org.geotoolkit.data.kml.model;

import java.awt.Color;
import java.util.List;
import org.geotoolkit.data.kml.KmlUtilities;
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
public class DefaultScreenOverlay extends DefaultAbstractOverlay implements ScreenOverlay {

    private Vec2 overlayXY;
    private Vec2 screenXY;
    private Vec2 rotationXY;
    private Vec2 size;
    private double rotation;
    private List<SimpleType> screenOverlaySimpleExtensions;
    private List<AbstractObject> screenOverlayObjectExtensions;

    /**
     *
     */
    public DefaultScreenOverlay(){
        this.rotation = DEF_ROTATION;
        this.screenOverlaySimpleExtensions = EMPTY_LIST;
        this.screenOverlayObjectExtensions = EMPTY_LIST;
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
     * @param overlayXY
     * @param screenXY
     * @param rotationXY
     * @param size
     * @param rotation
     * @param screenOverlaySimpleExtensions
     * @param screenOverlayObjectExtensions
     */
    public DefaultScreenOverlay(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String name, boolean visibility, boolean open, AtomPersonConstruct author, AtomLink link,
            String address, AddressDetails addressDetails, String phoneNumber, String snippet,
            String description, AbstractView view, AbstractTimePrimitive timePrimitive,
            String styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, ExtendedData extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            Color color, int drawOrder, Icon icon,
            List<SimpleType> abstractOveraySimpleExtensions, List<AbstractObject> abstractOverlayObjectExtensions,
            Vec2 overlayXY, Vec2 screenXY, Vec2 rotationXY, Vec2 size, double rotation,
            List<SimpleType> screenOverlaySimpleExtensions, List<AbstractObject> screenOverlayObjectExtensions){
        super(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, link,
                address, addressDetails, phoneNumber, snippet,
                description, view, timePrimitive,
                styleUrl, styleSelector, region, extendedData,
                abstractFeatureSimpleExtensions, abstractFeatureObjectExtensions,
                color, drawOrder, icon, abstractOveraySimpleExtensions, abstractOverlayObjectExtensions);
        this.overlayXY = overlayXY;
        this.screenXY = screenXY;
        this.rotationXY = rotationXY;
        this.size = size;
        this.rotation = KmlUtilities.checkAngle180(rotation);
        this.screenOverlaySimpleExtensions = (screenOverlaySimpleExtensions == null) ? EMPTY_LIST : screenOverlaySimpleExtensions;
        this.screenOverlayObjectExtensions = (screenOverlayObjectExtensions == null) ? EMPTY_LIST : screenOverlayObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Vec2 getOverlayXY() {
        return this.overlayXY;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Vec2 getScreenXY() {
        return this.screenXY;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Vec2 getRotationXY() {
        return this.rotationXY;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Vec2 getSize() {
        return this.size;
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
    public List<SimpleType> getScreenOverlaySimpleExtensions() {
        return this.screenOverlaySimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getScreenOverlayObjectExtensions() {
        return this.screenOverlayObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setOverlayXY(Vec2 overlayXY){
        this.overlayXY = overlayXY;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setScreenXY(Vec2 screenXY) {
        this.screenXY = screenXY;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setRotationXY(Vec2 rotationXY) {
        this.rotationXY = rotationXY;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setSize(Vec2 size) {
        this.size = size;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setRotation(double rotation) {
        this.rotation = KmlUtilities.checkAngle180(rotation);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setScreenOverlaySimpleExtensions(List<SimpleType> screenOverlaySimpleExtentions) {
        this.screenOverlaySimpleExtensions = screenOverlaySimpleExtentions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setScreenOverlayObjectExtensions(List<AbstractObject> screenOverlayObjectExtensions) {
        this.screenOverlayObjectExtensions = screenOverlayObjectExtensions;
    }

}
