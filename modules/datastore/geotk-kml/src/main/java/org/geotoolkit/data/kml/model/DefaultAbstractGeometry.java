package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public abstract class DefaultAbstractGeometry extends DefaultAbstractObject implements AbstractGeometry {

    protected List<SimpleType> geometrySimpleExtensions;
    protected List<AbstractObject> geometryObjectExtensions;

    /**
     * 
     */
    protected DefaultAbstractGeometry(){
        this.geometrySimpleExtensions = EMPTY_LIST;
        this.geometryObjectExtensions = EMPTY_LIST;
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractGeometrySimpleExtensions
     * @param abstractGeometryObjectExtensions
     */
    protected DefaultAbstractGeometry(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions){
        super(objectSimpleExtensions, idAttributes);
        this.geometrySimpleExtensions = (abstractGeometrySimpleExtensions == null) ? EMPTY_LIST : abstractGeometrySimpleExtensions;
        this.geometryObjectExtensions = (abstractGeometryObjectExtensions == null) ? EMPTY_LIST : abstractGeometryObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getAbstractGeometrySimpleExtensions(){return this.geometrySimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getAbstractGeometryObjectExtensions(){return this.geometryObjectExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAbstractGeometrySimpleExtensions(List<SimpleType> geometrySimpleExtensions){
        this.geometrySimpleExtensions = geometrySimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAbstractGeometryObjectExtensions(List<AbstractObject> geometryObjectExtensions){
        this.geometryObjectExtensions = geometryObjectExtensions;
    }

    @Override
    public String toString(){
        String resultat = super.toString();
        resultat += "Abstract Geometry : ";
        return resultat;
    }
}
