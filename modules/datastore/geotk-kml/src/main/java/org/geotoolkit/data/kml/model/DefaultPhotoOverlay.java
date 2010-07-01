package org.geotoolkit.data.kml.model;

import java.awt.Color;
import java.util.List;
import org.geotoolkit.data.kml.KmlUtilities;
import org.geotoolkit.data.atom.model.AtomLink;
import org.geotoolkit.data.atom.model.AtomPersonConstruct;
import org.geotoolkit.data.xal.model.AddressDetails;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultPhotoOverlay extends DefaultAbstractOverlay implements PhotoOverlay {

    private double rotation;
    private ViewVolume viewVolume;
    private ImagePyramid imagePyramid;
    private Point point;
    private Shape shape;
    private List<SimpleType> photoOverlaySimpleExtensions;
    private List<AbstractObject> photoOverlayObjectExtensions;

    /**
     * 
     */
    public DefaultPhotoOverlay() {
        super();
        this.photoOverlaySimpleExtensions = EMPTY_LIST;
        this.photoOverlayObjectExtensions = EMPTY_LIST;
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
     * @param rotation
     * @param viewVolume
     * @param imagePyramid
     * @param point
     * @param shape
     * @param photoOverlaySimpleExtensions
     * @param photoOverlayObjectExtensions
     */
    public DefaultPhotoOverlay(final List<SimpleType> objectSimpleExtensions, final IdAttributes idAttributes,
            final String name, final boolean visibility, final boolean open, final AtomPersonConstruct author, final AtomLink link,
            final String address, final AddressDetails addressDetails, final String phoneNumber, final String snippet,
            final String description, final AbstractView view, final AbstractTimePrimitive timePrimitive,
            final String styleUrl, final List<AbstractStyleSelector> styleSelector,
            final Region region, final ExtendedData extendedData,
            final List<SimpleType> abstractFeatureSimpleExtensions,
            final List<AbstractObject> abstractFeatureObjectExtensions,
            final Color color, final int drawOrder, final Icon icon,
            final List<SimpleType> abstractOveraySimpleExtensions, final List<AbstractObject> abstractOverlayObjectExtensions,
            final double rotation, final ViewVolume viewVolume, final ImagePyramid imagePyramid, final Point point, final Shape shape,
            final List<SimpleType> photoOverlaySimpleExtensions, final List<AbstractObject> photoOverlayObjectExtensions) {
        super(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, link, address, addressDetails, phoneNumber, snippet,
                description, view, timePrimitive, styleUrl, styleSelector, region, extendedData,
                abstractFeatureSimpleExtensions, abstractFeatureObjectExtensions,
                color, drawOrder, icon, abstractOveraySimpleExtensions, abstractOverlayObjectExtensions);
        this.rotation = KmlUtilities.checkAngle180(rotation);
        this.viewVolume = viewVolume;
        this.imagePyramid = imagePyramid;
        this.point = point;
        this.shape = shape;
        this.photoOverlaySimpleExtensions = (photoOverlaySimpleExtensions == null) ? EMPTY_LIST : photoOverlaySimpleExtensions;
        this.photoOverlayObjectExtensions = (photoOverlayObjectExtensions == null) ? EMPTY_LIST : photoOverlayObjectExtensions;
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
    public ViewVolume getViewVolume() {
        return this.viewVolume;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ImagePyramid getImagePyramid() {
        return this.imagePyramid;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Point getPoint() {
        return this.point;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Shape getShape() {
        return this.shape;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getPhotoOverlaySimpleExtensions() {
        return this.photoOverlaySimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getPhotoOverlayObjectExtensions() {
        return this.photoOverlayObjectExtensions;
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
    public void setViewVolume(ViewVolume viewVolume) {
        this.viewVolume = viewVolume;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setImagePyramid(ImagePyramid imagePyramid) {
        this.imagePyramid = imagePyramid;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPoint(Point point) {
        this.point = point;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setShape(Shape shape) {
        this.shape = shape;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPhotoOverlaySimpleExtensions(List<SimpleType> photoOverlaySimpleExtensions) {
        this.photoOverlaySimpleExtensions = photoOverlaySimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPhotoOverlayObjectExtensions(List<AbstractObject> photoOverlayObjectExtensions) {
        this.photoOverlayObjectExtensions = photoOverlayObjectExtensions;
    }
}
