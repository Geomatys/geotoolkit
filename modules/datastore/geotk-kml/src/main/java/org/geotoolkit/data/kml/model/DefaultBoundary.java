package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultBoundary implements Boundary {

    private Extensions extensions = new Extensions();
    private LinearRing linearRing;

    /**
     * 
     */
    public DefaultBoundary() {
    }

    /**
     *
     * @param linearRing
     * @param boundarySimpleExtensions
     * @param boundaryObjectExtensions
     */
    public DefaultBoundary(LinearRing linearRing,
            List<SimpleType> boundarySimpleExtensions,
            List<AbstractObject> boundaryObjectExtensions) {
        this.linearRing = linearRing;
        if (boundarySimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.BOUNDARY).addAll(boundarySimpleExtensions);
        }
        if (boundaryObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.BOUNDARY).addAll(boundaryObjectExtensions);
        }
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
    public void setLinearRing(LinearRing linearRing) {
        this.linearRing = linearRing;
    }

    @Override
    public Extensions extensions() {
        return this.extensions;
    }
}
