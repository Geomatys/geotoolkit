package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static java.util.Collections.*;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultLinearRing extends DefaultAbstractGeometry implements LinearRing {

    private boolean extrude;
    private boolean tessellate;
    private AltitudeMode altitudeMode;
    private Coordinates coordinates;
    private List<SimpleType> linearRingSimpleExtensions;
    private List<AbstractObject> linearRingObjectExtensions;

    /**
     * 
     */
    public DefaultLinearRing() {
        this.extrude = DEF_EXTRUDE;
        this.tessellate = DEF_TESSELLATE;
        this.altitudeMode = DEF_ALTITUDE_MODE;
        this.linearRingSimpleExtensions = EMPTY_LIST;
        this.linearRingObjectExtensions = EMPTY_LIST;
    }

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
    public DefaultLinearRing(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions,
            boolean extrude, boolean tessellate,
            AltitudeMode altitudeMode,
            Coordinates coordinates,
            List<SimpleType> linearRingSimpleExtensions,
            List<AbstractObject> linearRingObjectExtensions) {
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
    public boolean getExtrude() {
        return this.extrude;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public boolean getTessellate() {
        return this.tessellate;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AltitudeMode getAltitudeMode() {
        return this.altitudeMode;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Coordinates getCoordinates() {
        return this.coordinates;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getLinearRingSimpleExtensions() {
        return this.linearRingSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getLinearRingObjectExtensions() {
        return this.linearRingObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setExtrude(boolean extrude) {
        this.extrude = extrude;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setTessellate(boolean tesselate) {
        this.tessellate = tesselate;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAltitudeMode(AltitudeMode altitudeMode) {
        this.altitudeMode = altitudeMode;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLinearRingSimpleExtensions(List<SimpleType> linearRingSimpleExtensions) {
        this.linearRingSimpleExtensions = linearRingSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLinearRingObjectExtensions(List<AbstractObject> linearRingObjectExtensions) {
        this.linearRingObjectExtensions = linearRingObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String toString() {
        String resultat = super.toString();
        resultat += "LinearRing : ";
        resultat += "\n\t" + coordinates.toString();
        return resultat;
    }
}
