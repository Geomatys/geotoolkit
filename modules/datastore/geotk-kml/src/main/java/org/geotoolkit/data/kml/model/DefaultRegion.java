package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultRegion extends DefaultAbstractObject implements Region {

    private LatLonAltBox latLonAltBox;
    private Lod lod;
    private List<SimpleType> regionSimpleExtensions;
    private List<AbstractObject> regionObjectExtentions;

    /**
     *
     */
    public DefaultRegion(){
        this.regionSimpleExtensions = EMPTY_LIST;
        this.regionObjectExtentions = EMPTY_LIST;
    }
    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param latLonAltBox
     * @param lod
     * @param regionSimpleExtensions
     * @param regionObjectExtentions
     */
    public DefaultRegion(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            LatLonAltBox latLonAltBox, Lod lod, List<SimpleType> regionSimpleExtensions, List<AbstractObject> regionObjectExtentions){
        super(objectSimpleExtensions, idAttributes);
        this.latLonAltBox = latLonAltBox;
        this.lod = lod;
        this.regionSimpleExtensions = (regionSimpleExtensions == null) ? EMPTY_LIST : regionSimpleExtensions;
        this.regionObjectExtentions = (regionObjectExtentions == null) ? EMPTY_LIST : regionObjectExtentions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public LatLonAltBox getLatLonAltBox() {return this.latLonAltBox;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Lod getLod() {return this.lod;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getRegionSimpleExtensions() {return this.regionSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getRegionObjectExtensions() {return this.regionObjectExtentions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLatLonAltBox(LatLonAltBox latLonAltBox) {
        this.latLonAltBox = latLonAltBox;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLod(Lod lod) {
        this.lod = lod;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setRegionSimpleExtensions(List<SimpleType> regionSimpleExtensions) {
        this.regionSimpleExtensions = regionSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setRegionObjectExtensions(List<AbstractObject> regionObjectExtensions) {
        this.regionObjectExtentions = regionObjectExtensions;
    }

}
