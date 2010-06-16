package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.atom.AtomLink;
import org.geotoolkit.data.model.atom.AtomPersonConstruct;
import org.geotoolkit.data.model.xal.AddressDetails;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultScreenOverlay extends DefaultAbstractOverlay implements ScreenOverlay {

    private final Vec2 overlayXY;
    private final Vec2 screenXY;
    private final Vec2 rotationXY;
    private final Vec2 size;
    private final Angle180 rotation;
    private final List<SimpleType> screenOverlaySimpleExtensions;
    private final List<AbstractObject> screenOverlayObjectExtensions;

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
            Vec2 overlayXY, Vec2 screenXY, Vec2 rotationXY, Vec2 size, Angle180 rotation,
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
        this.rotation = rotation;
        this.screenOverlaySimpleExtensions = (screenOverlaySimpleExtensions == null) ? EMPTY_LIST : screenOverlaySimpleExtensions;
        this.screenOverlayObjectExtensions = (screenOverlayObjectExtensions == null) ? EMPTY_LIST : screenOverlayObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Vec2 getOverlayXY() {return this.overlayXY;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Vec2 getScreenXY() {return this.screenXY;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Vec2 getRotationXY() {return this.rotationXY;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Vec2 getSize() {return this.size;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Angle180 getRotation() {return this.rotation;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getScreenOverlaySimpleExtensions() {return this.screenOverlaySimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getScreenOverlayObjectExtensions() {return this.screenOverlayObjectExtensions;}

}
