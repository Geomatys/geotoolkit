package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultMultiGeometry extends DefaultAbstractGeometry implements MultiGeometry {

    private List<AbstractGeometry> geometries;

    /**
     * 
     */
    public DefaultMultiGeometry() {
        this.geometries = EMPTY_LIST;
    }

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
            List<AbstractObject> multiGeometryObjectExtensions) {
        super(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions,
                abstractGeometryObjectExtensions);
        this.geometries = (geometries == null) ? EMPTY_LIST : geometries;
        if (multiGeometrySimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.MULTI_GEOMETRY).addAll(multiGeometrySimpleExtensions);
        }
        if (multiGeometryObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.MULTI_GEOMETRY).addAll(multiGeometryObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractGeometry> getGeometries() {
        return this.geometries;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setGeometries(List<AbstractGeometry> geometries) {
        this.geometries = geometries;
    }
}
