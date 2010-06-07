package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public abstract class AbstractGeometryDefault extends AbstractObjectDefault implements AbstractGeometry {

    protected final List<SimpleType> geometrySimpleExtensions;
    protected final List<AbstractObject> geometryObjectExtensions;

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractGeometrySimpleExtensions
     * @param abstractGeometryObjectExtensions
     */
    protected AbstractGeometryDefault(List<SimpleType> objectSimpleExtensions, 
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

    @Override
    public String toString(){
        String resultat = super.toString();
        resultat += "Abstract Geometry : ";
        return resultat;
    }
}
