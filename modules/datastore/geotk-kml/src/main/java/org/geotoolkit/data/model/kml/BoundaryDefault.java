package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class BoundaryDefault implements Boundary {

    private LinearRing linearRing;
    private List<SimpleType> boundarySimpleExtensions;
    private List<AbstractObject> boundaryObjectExtensions;

    /**
     *
     * @param linearRing
     * @param boundarySimpleExtensions
     * @param boundaryObjectExtensions
     */
    public BoundaryDefault(LinearRing linearRing, List<SimpleType> boundarySimpleExtensions, List<AbstractObject> boundaryObjectExtensions){
        this.linearRing = linearRing;
        this.boundarySimpleExtensions = boundarySimpleExtensions;
        this.boundaryObjectExtensions = boundaryObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public LinearRing getLinearRing() {return this.linearRing;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getBoundarySimpleExtensions() {return this.boundarySimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getBoundaryObjectExtensions() {return this.boundaryObjectExtensions;}

}
