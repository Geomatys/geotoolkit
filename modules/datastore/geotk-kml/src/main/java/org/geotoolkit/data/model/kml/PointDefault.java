package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class PointDefault extends AbstractGeometryDefault implements Point {

    private boolean extrude;
    private AltitudeMode altitudeMode;
    private Coordinates coordinates;
    private List<SimpleType> pointSimpleExtensions;
    private List<AbstractObject> pointObjectExtensions;

    public PointDefault(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions,
            boolean extrude,
            AltitudeMode altitudeMode,
            Coordinates coordinates,
            List<SimpleType> pointSimpleExtensions,
            List<AbstractObject> pointObjectExtensions){
        super(objectSimpleExtensions, idAttributes,
            abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions);
        this.extrude = extrude;
        this.altitudeMode = altitudeMode;
        this.coordinates = coordinates;
        this.pointSimpleExtensions = pointSimpleExtensions;
        this.pointObjectExtensions = pointObjectExtensions;
    }

    @Override
    public boolean getExtrude() {return this.extrude;}

    @Override
    public AltitudeMode getAltitudeMode() {return this.altitudeMode;}

    @Override
    public Coordinates getCoordinates() {return this.coordinates;}

    @Override
    public List<SimpleType> getPointSimpleExtensions() {return this.pointSimpleExtensions;}

    @Override
    public List<AbstractObject> getPointObjectExtensions() {return this.pointObjectExtensions;}

    @Override
    public String toString(){
        String resultat = super.toString();
        resultat += "Point : ";
        resultat += "\n\t"+coordinates.toString();
        return resultat;
    }

}
