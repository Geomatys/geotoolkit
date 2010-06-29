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
    private List<SimpleType> multiGeometrySimpleExtensions;
    private List<AbstractObject> multiGeometryObjectExtensions;

    /**
     * 
     */
    public DefaultMultiGeometry() {
        this.geometries = EMPTY_LIST;
        this.multiGeometrySimpleExtensions = EMPTY_LIST;
        this.multiGeometryObjectExtensions = EMPTY_LIST;
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
    public List<AbstractGeometry> getGeometries() {
        return this.geometries;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getMultiGeometrySimpleExtensions() {
        return this.multiGeometrySimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getMultiGeometryObjectExtensions() {
        return this.multiGeometryObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setGeometries(List<AbstractGeometry> geometries) {
        this.geometries = geometries;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setMultiGeometrySimpleExtensions(List<SimpleType> multiGeometrySimpleExtensions) {
        this.multiGeometrySimpleExtensions = multiGeometrySimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setMultiGeometryObjectExtensions(List<AbstractObject> multiGeometryObjectExtensions) {
        this.multiGeometryObjectExtensions = multiGeometryObjectExtensions;
    }
}
