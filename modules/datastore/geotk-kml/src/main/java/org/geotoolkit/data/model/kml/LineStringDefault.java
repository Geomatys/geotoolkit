package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class LineStringDefault extends AbstractGeometryDefault implements LineString {

    private final boolean extrude;
    private final boolean tessellate;
    private final AltitudeMode altitudeMode;
    private final Coordinates coordinates;
    private final List<SimpleType> lineStringSimpleExtensions;
    private final List<AbstractObject> lineStringObjectExtensions;

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
     * @param lineStringSimpleExtensions
     * @param lineStringObjectExtensions
     */
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
        this.lineStringSimpleExtensions = (lineStringSimpleExtensions == null) ? EMPTY_LIST : lineStringSimpleExtensions;
        this.lineStringObjectExtensions = (lineStringObjectExtensions == null) ? EMPTY_LIST : lineStringObjectExtensions;
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
    public List<SimpleType> getLineStringSimpleExtensions() {return this.lineStringSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
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
