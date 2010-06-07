package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class PointDefault extends AbstractGeometryDefault implements Point {

    private final boolean extrude;
    private final AltitudeMode altitudeMode;
    private final Coordinates coordinates;
    private final List<SimpleType> pointSimpleExtensions;
    private final List<AbstractObject> pointObjectExtensions;

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractGeometrySimpleExtensions
     * @param abstractGeometryObjectExtensions
     * @param extrude
     * @param altitudeMode
     * @param coordinates
     * @param pointSimpleExtensions
     * @param pointObjectExtensions
     */
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
        this.pointSimpleExtensions = (pointSimpleExtensions == null) ? EMPTY_LIST : pointSimpleExtensions;
        this.pointObjectExtensions = (pointObjectExtensions == null) ? EMPTY_LIST : pointObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public boolean getExtrude() {return this.extrude;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AltitudeMode getAltitudeMode() {return this.altitudeMode;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Coordinates getCoordinates() {return this.coordinates;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getPointSimpleExtensions() {return this.pointSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
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
