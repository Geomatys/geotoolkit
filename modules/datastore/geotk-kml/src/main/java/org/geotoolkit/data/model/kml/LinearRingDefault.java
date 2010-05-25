package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class LinearRingDefault extends AbstractGeometryDefault implements LinearRing {

    private boolean extrude;
    private boolean tessellate;
    private AltitudeMode altitudeMode;
    private Coordinates coordinates;
    private List<SimpleType> linearRingSimpleExtensions;
    private List<AbstractObject> linearRingObjectExtensions;

    public LinearRingDefault(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions,
            boolean extrude, boolean tessellate,
            AltitudeMode altitudeMode,
            Coordinates coordinates,
            List<SimpleType> linearRingSimpleExtensions,
            List<AbstractObject> linearRingObjectExtensions){
        super(objectSimpleExtensions, idAttributes,
            abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions);
        this.extrude = extrude;
        this.tessellate = tessellate;
        this.altitudeMode = altitudeMode;
        this.coordinates = coordinates;
        this.linearRingSimpleExtensions = linearRingSimpleExtensions;
        this.linearRingObjectExtensions = linearRingObjectExtensions;
    }

    @Override
    public boolean getExtrude() {return this.extrude;}

    @Override
    public boolean getTessellate() {return this.tessellate;}

    @Override
    public AltitudeMode getAltitudeMode() {return this.altitudeMode;}

    @Override
    public Coordinates getCoordinates() {return this.coordinates;}

    @Override
    public List<SimpleType> getLinearRingSimpleExtensions() {return this.linearRingSimpleExtensions;}

    @Override
    public List<AbstractObject> getLinearRingObjectExtensions() {return this.linearRingObjectExtensions;}

    @Override
    public String toString(){
        String resultat = super.toString();
        resultat += "LinearRing : ";
        resultat += "\n\t"+coordinates.toString();
        return resultat;
    }

}
