package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class LineStringDefault extends AbstractGeometryDefault implements LineString {

    private boolean extrude;
    private boolean tessellate;
    private AltitudeMode altitudeMode;
    private Coordinates coordinates;
    private List<SimpleType> lineStringSimpleExtensions;
    private List<AbstractObject> lineStringObjectExtensions;

    public LineStringDefault(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions,
            boolean extrude, boolean tessellate,
            AltitudeMode altitudeMode,
            Coordinates coordinates,
            List<SimpleType> lineStringSimpleExtensions,
            List<AbstractObject> lineStringObjectExtensions){
        super(objectSimpleExtensions, idAttributes,
            abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions);
        this.extrude = extrude;
        this.tessellate = tessellate;
        this.altitudeMode = altitudeMode;
        this.coordinates = coordinates;
        this.lineStringSimpleExtensions = lineStringSimpleExtensions;
        this.lineStringObjectExtensions = lineStringObjectExtensions;
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
    public List<SimpleType> getLineStringSimpleExtensions() {return this.lineStringSimpleExtensions;}

    @Override
    public List<AbstractObject> getLineStringObjectExtensions() {return this.lineStringObjectExtensions;}

    @Override
    public String toString(){
        String resultat = super.toString();
        resultat += "LineString : ";
        resultat += "\n\t"+coordinates.toString();
        return resultat;
    }

}
