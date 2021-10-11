/*
 *     (C) 2019, Geomatys
 */
package org.geotoolkit.processing.science.drift.v2;

import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.util.ArgumentChecks;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class Origin {

    /**
     * Origin position given as argument. It's a brut information stored as is. We keep it in case we'd need it later,
     * but please use {@link #getOrigin2d() } to retrieve the start location of drift process as a 2D UTM point.
     */
    private final DirectPosition source;

    private final DirectPosition2D origin2d;

    public Origin(DirectPosition source) throws TransformException, FactoryException {
        ArgumentChecks.ensureNonNull("Source position", source);
        final CoordinateReferenceSystem sourceCrs = source.getCoordinateReferenceSystem();
        ArgumentChecks.ensureNonNull("Source position CRS", sourceCrs);
        this.source = source;

        final DirectPosition tmpGeo = CRS.findOperation(sourceCrs, CommonCRS.WGS84.geographic(), null)
                .getMathTransform()
                .transform(source, null);
        final double lat = tmpGeo.getOrdinate(0);
        final double lon = tmpGeo.getOrdinate(1);
        final ProjectedCRS utmCrs = CommonCRS.WGS84.universal(lat, lon);
        // TODO : get utm extent instead of crappy hard-coded extent.
        final DirectPosition tmpUtm = CRS.findOperation(sourceCrs, utmCrs, new DefaultGeographicBoundingBox(lon-3, lon+3, lat-3, lat+3))
                .getMathTransform()
                .transform(source, null);
        origin2d = new DirectPosition2D(tmpUtm);
        origin2d.setCoordinateReferenceSystem(utmCrs);
    }

    /**
     *
     * @return The drift origin position, as a 2D UTM position.
     */
    public DirectPosition2D getOrigin2d() {
        return new DirectPosition2D(origin2d);
    }

    public DirectPosition getSource() {
        return source;
    }
}
