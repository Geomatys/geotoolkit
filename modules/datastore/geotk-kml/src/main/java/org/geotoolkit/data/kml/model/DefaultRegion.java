package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultRegion extends DefaultAbstractObject implements Region {

    private LatLonAltBox latLonAltBox;
    private Lod lod;

    /**
     *
     */
    public DefaultRegion() {
    }

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param latLonAltBox
     * @param lod
     * @param regionSimpleExtensions
     * @param regionObjectExtensions
     */
    public DefaultRegion(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            LatLonAltBox latLonAltBox, Lod lod,
            List<SimpleType> regionSimpleExtensions,
            List<AbstractObject> regionObjectExtensions) {
        super(objectSimpleExtensions, idAttributes);
        this.latLonAltBox = latLonAltBox;
        this.lod = lod;
        if (regionSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.REGION).addAll(regionSimpleExtensions);
        }
        if (regionObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.REGION).addAll(regionObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public LatLonAltBox getLatLonAltBox() {
        return this.latLonAltBox;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Lod getLod() {
        return this.lod;
    }

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
}
