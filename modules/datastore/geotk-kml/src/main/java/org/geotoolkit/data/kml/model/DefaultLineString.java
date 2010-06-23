package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static java.util.Collections.*;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultLineString extends DefaultAbstractGeometry implements LineString {

    private boolean extrude;
    private boolean tessellate;
    private AltitudeMode altitudeMode;
    private Coordinates coordinates;
    private List<SimpleType> lineStringSimpleExtensions;
    private List<AbstractObject> lineStringObjectExtensions;

    /**
     * 
     */
    public DefaultLineString(){
        this.extrude = DEF_EXTRUDE;
        this.tessellate = DEF_TESSELLATE;
        this.altitudeMode = DEF_ALTITUDE_MODE;
        this.lineStringSimpleExtensions = EMPTY_LIST;
        this.lineStringObjectExtensions = EMPTY_LIST;
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
     * @param lineStringSimpleExtensions
     * @param lineStringObjectExtensions
     */
    public DefaultLineString(List<SimpleType> objectSimpleExtensions,
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
    public void setTessellate(boolean tessellate) {
        this.tessellate = tessellate;
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
    public void setLineStringSimpleExtensions(List<SimpleType> lineStringSimpleExtensions) {
        this.lineStringSimpleExtensions = lineStringSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLineStringObjectExtensions(List<AbstractObject> lineStringObjectExtensions) {
        this.lineStringObjectExtensions = lineStringObjectExtensions;
    }

    @Override
    public String toString(){
        String resultat = super.toString();
        resultat += "LineString : ";
        resultat += "\n\t"+coordinates.toString();
        return resultat;
    }
}
