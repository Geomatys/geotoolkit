package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public abstract class AbstractGeometryDefault extends AbstractObjectDefault implements AbstractGeometry {

    protected List<SimpleType> geometrySimpleExtensions;
    protected List<AbstractObject> geometryObjectExtensions;

    protected AbstractGeometryDefault(List<SimpleType> objectSimpleExtensions, 
            IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions){
        super(objectSimpleExtensions, idAttributes);
        this.geometrySimpleExtensions = abstractGeometrySimpleExtensions;
        this.geometryObjectExtensions = abstractGeometryObjectExtensions;
    }

    @Override
    public List<SimpleType> getAbstractGeometrySimpleExtensions(){return this.geometrySimpleExtensions;}
    @Override
    public List<AbstractObject> getAbstractGeometryObjectExtensions(){return this.geometryObjectExtensions;}

    @Override
    public String toString(){
        String resultat = super.toString();
        resultat += "Abstract Geometry : ";
        return resultat;
    }
}
