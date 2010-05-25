package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class RegionDefault extends AbstractObjectDefault implements Region {

    private LatLonAltBox latLonAltBox;
    private Lod lod;
    private List<SimpleType> regionSimpleExtensions;
    private List<AbstractObject> regionObjectExtentions;

    public RegionDefault(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            LatLonAltBox latLonAltBox, Lod lod, List<SimpleType> regionSimpleExtensions, List<AbstractObject> regionObjectExtentions){
        super(objectSimpleExtensions, idAttributes);
        this.latLonAltBox = latLonAltBox;
        this.lod = lod;
        this.regionSimpleExtensions = regionSimpleExtensions;
        this.regionObjectExtentions = regionObjectExtentions;
    }

    @Override
    public LatLonAltBox getLatLonAltBox() {return this.latLonAltBox;}

    @Override
    public Lod getLod() {return this.lod;}

    @Override
    public List<SimpleType> getRegionSimpleExtensions() {return this.regionSimpleExtensions;}

    @Override
    public List<AbstractObject> getRegionObjectExtensions() {return this.regionObjectExtentions;}

}
