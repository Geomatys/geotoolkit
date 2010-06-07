package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class ModelDefault extends AbstractGeometryDefault implements Model {

    private final AltitudeMode altitudeMode;
    private final Location location;
    private final Orientation orientation;
    private final Scale scale;
    private final Link link;
    private final ResourceMap resourceMap;
    private final List<SimpleType> modelSimpleExtensions;
    private final List<AbstractObject> modelObjectExtensions;

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
    public ModelDefault(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions,
            AltitudeMode altitudeMode, Location location, Orientation orientation, Scale scale, Link link, ResourceMap resourceMap,
            List<SimpleType> modelSimpleExtensions, List<AbstractObject> modelObjectExtensions){
        super(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions);
        this.altitudeMode = altitudeMode;
        this.location = location;
        this.orientation = orientation;
        this.scale = scale;
        this.link = link;
        this.resourceMap = resourceMap;
        this.modelSimpleExtensions = (modelSimpleExtensions == null) ? EMPTY_LIST : modelSimpleExtensions;
        this.modelObjectExtensions = (modelObjectExtensions == null) ? EMPTY_LIST : modelObjectExtensions;
    }

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
    public Location getLocation() {return this.location;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Orientation getOrientation() {return this.orientation;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Scale getScale() {return this.scale;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Link getLink() {return this.link;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ResourceMap getRessourceMap() {return this.resourceMap;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getModelSimpleExtensions() {return this.modelSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getModelObjectExtensions() {return this.modelObjectExtensions;}
}
