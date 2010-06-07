package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class LinearRingDefault extends AbstractGeometryDefault implements LinearRing {

    private final boolean extrude;
    private final boolean tessellate;
    private final AltitudeMode altitudeMode;
    private final Coordinates coordinates;
    private final List<SimpleType> linearRingSimpleExtensions;
    private final List<AbstractObject> linearRingObjectExtensions;

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractGeometrySimpleExtensions
     * @param abstractGeometryObjectExtensions
     * @param extrude
     * @param tessellate
     * @param altitudeMode
     * @param coordinates
     * @param linearRingSimpleExtensions
     * @param linearRingObjectExtensions
     */
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
        this.linearRingSimpleExtensions = (linearRingSimpleExtensions == null) ? EMPTY_LIST : linearRingSimpleExtensions;
        this.linearRingObjectExtensions = (linearRingObjectExtensions == null) ? EMPTY_LIST : linearRingObjectExtensions;
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
    public boolean getTessellate() {return this.tessellate;}

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
    public List<SimpleType> getLinearRingSimpleExtensions() {return this.linearRingSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
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
