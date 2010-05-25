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
public class ScreenOverlayDefault extends AbstractOverlayDefault implements ScreenOverlay {

    private Vec2 overlayXY;
    private Vec2 screenXY;
    private Vec2 rotationXY;
    private Vec2 size;
    private Angle180 rotation;
    private List<SimpleType> screenOverlaySimpleExtensions;
    private List<AbstractObject> screenOverlayObjectExtensions;

    public ScreenOverlayDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String name, boolean visibility, boolean open, AtomPersonConstruct author, AtomLink link,
            String address, AddressDetails addressDetails, String phoneNumber, String snippet,
            String description, AbstractView view, AbstractTimePrimitive timePrimitive,
            String styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, ExtendedData extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            Color color, int drawOrder, Link icon,
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
        this.screenOverlaySimpleExtensions = screenOverlaySimpleExtensions;
        this.screenOverlayObjectExtensions = screenOverlayObjectExtensions;
    }

    @Override
    public Vec2 getOverlayXY() {return this.overlayXY;}

    @Override
    public Vec2 getScreenXY() {return this.screenXY;}

    @Override
    public Vec2 getRotationXY() {return this.rotationXY;}

    @Override
    public Vec2 getSize() {return this.size;}

    @Override
    public Angle180 getRotation() {return this.rotation;}

    @Override
    public List<SimpleType> getScreenOverlaySimpleExtensions() {return this.screenOverlaySimpleExtensions;}

    @Override
    public List<AbstractObject> getScreenOverlayObjectExtensions() {return this.screenOverlayObjectExtensions;}

}
