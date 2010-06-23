package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultBoundary implements Boundary {

    private LinearRing linearRing;
    private List<SimpleType> boundarySimpleExtensions;
    private List<AbstractObject> boundaryObjectExtensions;

    /**
     * 
     */
    public DefaultBoundary() {
        this.boundarySimpleExtensions = EMPTY_LIST;
        this.boundaryObjectExtensions = EMPTY_LIST;
    }

    /**
     *
     * @param linearRing
     * @param boundarySimpleExtensions
     * @param boundaryObjectExtensions
     */
    public DefaultBoundary(LinearRing linearRing, List<SimpleType> boundarySimpleExtensions, List<AbstractObject> boundaryObjectExtensions) {
        this.linearRing = linearRing;
        this.boundarySimpleExtensions = (boundarySimpleExtensions == null) ? EMPTY_LIST : boundarySimpleExtensions;
        this.boundaryObjectExtensions = (boundaryObjectExtensions == null) ? EMPTY_LIST : boundaryObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public LinearRing getLinearRing() {
        return this.linearRing;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getBoundarySimpleExtensions() {
        return this.boundarySimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getBoundaryObjectExtensions() {
        return this.boundaryObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLinearRing(LinearRing linearRing) {
        this.linearRing = linearRing;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setBoundarySimpleExtensions(List<SimpleType> boundarySimpleExtensions) {
        this.boundarySimpleExtensions = boundarySimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setBoundaryObjectExtensions(List<AbstractObject> boundaryObjectExtensions) {
        this.boundaryObjectExtensions = boundaryObjectExtensions;
    }
}
