package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultMultiGeometry extends DefaultAbstractGeometry implements MultiGeometry {

    private final List<AbstractGeometry> geometries;
    private final List<SimpleType> multiGeometrySimpleExtensions;
    private final List<AbstractObject> multiGeometryObjectExtensions;

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
    public DefaultMultiGeometry(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions,
            List<AbstractGeometry> geometries,
            List<SimpleType> multiGeometrySimpleExtensions,
            List<AbstractObject> multiGeometryObjectExtensions){
        super(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions);
        this.geometries = (geometries == null) ? EMPTY_LIST : geometries;
        this.multiGeometrySimpleExtensions = (multiGeometrySimpleExtensions == null) ? EMPTY_LIST : multiGeometrySimpleExtensions;
        this.multiGeometryObjectExtensions = (multiGeometryObjectExtensions == null) ? EMPTY_LIST : multiGeometryObjectExtensions;
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
