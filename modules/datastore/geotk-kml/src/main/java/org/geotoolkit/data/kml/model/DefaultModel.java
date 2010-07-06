package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultModel extends DefaultAbstractGeometry implements Model {

    private final Extensions exts = new Extensions();
    private AltitudeMode altitudeMode;
    private Location location;
    private Orientation orientation;
    private Scale scale;
    private Link link;
    private ResourceMap resourceMap;

    public DefaultModel() {
        this.altitudeMode = DEF_ALTITUDE_MODE;
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractGeometrySimpleExtensions
     * @param abstractGeometryObjectExtensions
     * @param altitudeMode
     * @param location
     * @param orientation
     * @param scale
     * @param link
     * @param resourceMap
     * @param modelSimpleExtensions
     * @param modelObjectExtensions
     */
    public DefaultModel(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions,
            AltitudeMode altitudeMode, Location location, Orientation orientation,
            Scale scale, Link link, ResourceMap resourceMap,
            List<SimpleType> modelSimpleExtensions,
            List<AbstractObject> modelObjectExtensions) {
        super(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions,
                abstractGeometryObjectExtensions);
        this.altitudeMode = altitudeMode;
        this.location = location;
        this.orientation = orientation;
        this.scale = scale;
        this.link = link;
        this.resourceMap = resourceMap;
        if (modelSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.MODEL).addAll(modelSimpleExtensions);
        }
        if (modelObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.MODEL).addAll(modelObjectExtensions);
        }
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
    public Location getLocation() {
        return this.location;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Orientation getOrientation() {
        return this.orientation;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Scale getScale() {
        return this.scale;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Link getLink() {
        return this.link;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ResourceMap getRessourceMap() {
        return this.resourceMap;
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
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setScale(Scale scale) {
        this.scale = scale;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLink(Link link) {
        this.link = link;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setRessourceMap(ResourceMap resourceMap) {
        this.resourceMap = resourceMap;
    }
}
