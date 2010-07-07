package org.geotoolkit.data.kml.model;

import java.awt.Color;
import java.util.List;
import org.geotoolkit.data.kml.KmlUtilities;
import org.geotoolkit.data.atom.model.AtomLink;
import org.geotoolkit.data.atom.model.AtomPersonConstruct;
import org.geotoolkit.data.kml.xsd.SimpleType;
import org.geotoolkit.data.xal.model.AddressDetails;

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

    /**
     * 
     */
    public DefaultPhotoOverlay() {
        super();
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
     * @param rotation
     * @param viewVolume
     * @param imagePyramid
     * @param point
     * @param shape
     * @param photoOverlaySimpleExtensions
     * @param photoOverlayObjectExtensions
     */
    public DefaultPhotoOverlay(List<SimpleType> objectSimpleExtensions,
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
            final double rotation, final ViewVolume viewVolume,
            final ImagePyramid imagePyramid,
            final Point point, final Shape shape,
            List<SimpleType> photoOverlaySimpleExtensions,
            List<AbstractObject> photoOverlayObjectExtensions) {
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
        this.rotation = KmlUtilities.checkAngle180(rotation);
        this.viewVolume = viewVolume;
        this.imagePyramid = imagePyramid;
        this.point = point;
        this.shape = shape;
        if (photoOverlaySimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.PHOTO_OVERLAY).addAll(photoOverlaySimpleExtensions);
        }
        if (photoOverlayObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.PHOTO_OVERLAY).addAll(photoOverlayObjectExtensions);
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
}
