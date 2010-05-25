package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class ModelDefault extends AbstractGeometryDefault implements Model {

    private AltitudeMode altitudeMode;
    private Location location;
    private Orientation orientation;
    private Scale scale;
    private Link link;
    private ResourceMap resourceMap;
    private List<SimpleType> modelSimpleExtensions;
    private List<AbstractObject> modelObjectExtensions;

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
        this.modelSimpleExtensions = modelSimpleExtensions;
        this.modelObjectExtensions = modelObjectExtensions;
    }

    @Override
    public AltitudeMode getAltitudeMode() {return this.altitudeMode;}

    @Override
    public Location getLocation() {return this.location;}

    @Override
    public Orientation getOrientation() {return this.orientation;}

    @Override
    public Scale getScale() {return this.scale;}

    @Override
    public Link getLink() {return this.link;}

    @Override
    public ResourceMap getRessourceMap() {return this.resourceMap;}

    @Override
    public List<SimpleType> getModelSimpleExtensions() {return this.modelSimpleExtensions;}

    @Override
    public List<AbstractObject> getModelObjectExtensions() {return this.modelObjectExtensions;}
}
