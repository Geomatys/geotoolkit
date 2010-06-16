package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultRegion extends DefaultAbstractObject implements Region {

    private final LatLonAltBox latLonAltBox;
    private final Lod lod;
    private final List<SimpleType> regionSimpleExtensions;
    private final List<AbstractObject> regionObjectExtentions;

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

}
