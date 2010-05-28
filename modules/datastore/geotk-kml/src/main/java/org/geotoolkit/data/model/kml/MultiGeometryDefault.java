package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class MultiGeometryDefault extends AbstractGeometryDefault implements MultiGeometry {

    private List<AbstractGeometry> geometries;
    private List<SimpleType> multiGeometrySimpleExtensions;
    private List<AbstractObject> multiGeometryObjectExtensions;

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractGeometrySimpleExtensions
     * @param abstractGeometryObjectExtensions
     * @param geometries
     * @param multiGeometrySimpleExtensions
     * @param multiGeometryObjectExtensions
     */
    public MultiGeometryDefault(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions,
            List<AbstractGeometry> geometries,
            List<SimpleType> multiGeometrySimpleExtensions,
            List<AbstractObject> multiGeometryObjectExtensions){
        super(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions);
        this.geometries = geometries;
        this.multiGeometrySimpleExtensions = multiGeometrySimpleExtensions;
        this.multiGeometryObjectExtensions = multiGeometryObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractGeometry> getGeometries() {return this.geometries;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getMultiGeometrySimpleExtensions() {return this.multiGeometrySimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getMultiGeometryObjectExtensions() {return this.multiGeometryObjectExtensions;}

}
