/*
 *     (C) 2019, Geomatys
 */
package org.geotoolkit.processing.science.drift.v2;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.privy.AxisDirections;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.crs.DefaultCompoundCRS;
import org.apache.sis.referencing.crs.DefaultTemporalCRS;
import org.apache.sis.util.ArgumentChecks;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
class Utilities {

    static final Logger LOGGER = Logger.getLogger("org.geotoolkit.processing.science.drift");

    /**
     * TODO : should we use millisecond precision ?
     */
    static final TemporalCRS DEFAULT_TEMPORAL_CRS = CommonCRS.Temporal.UNIX.crs();

    /**
     * Modify or add temporal coordinate to the given point. Note that the source point is modified if i already contains
     * a temporal axis. Otherwise, a new point with an additional temporal axis is created, filled and returned.
     *
     * @param source The position to configure time for.
     * @param time The temporal position to set.
     * @return Modified source point or a new one.
     *
     * @throws FactoryException
     * @throws TransformException
     */
    static DirectPosition setTime(DirectPosition source, Instant time) throws FactoryException, TransformException {
        return getSubCrs(source::getCoordinateReferenceSystem, CRS::getTemporalComponent)
                .map(component -> {
                    double value = DefaultTemporalCRS.castOrCopy(component.crs).toValue(time);
                    source.setCoordinate(component.idx, value);
                    return source;
                })
                .orElseGet(() -> addTime(source, time));
    }

    private static GeneralDirectPosition addTime(final DirectPosition source, final Instant time) {
        final DefaultCompoundCRS newCrs = new DefaultCompoundCRS(
                Collections.singletonMap("name", "timed crs"),
                source.getCoordinateReferenceSystem(),
                DEFAULT_TEMPORAL_CRS
        );

        final GeneralDirectPosition pos = new GeneralDirectPosition(newCrs);
        final double[] newCoord = Arrays.copyOf(source.getCoordinates(), newCrs.getCoordinateSystem().getDimension());
        newCoord[newCoord.length - 1] = time.getEpochSecond();

        pos.setCoordinates(newCoord);
        return pos;
    }

    static Optional<Instant> getTime(final DirectPosition source) {
        return getSubCrs(source::getCoordinateReferenceSystem, CRS::getTemporalComponent)
                .map(component -> {
                    final double value = source.getCoordinate(component.idx);
                    return DefaultTemporalCRS.castOrCopy(component.crs).toInstant(value);
                });
    }

    static <T extends SingleCRS> Optional<IndexedComponent<T>> getSubCrs(final Supplier<CoordinateReferenceSystem> source, final Function<CoordinateReferenceSystem, T> extractor) {
        final CoordinateReferenceSystem crs = source.get();
        if (crs == null)
            throw new IllegalArgumentException("Given object has not any reference system.");
        final T component = extractor.apply(crs);
        if (component == null) {
            return Optional.empty();
        }

        return Optional.of(new IndexedComponent<>(component, crs));
    }

    static Optional<GeneralEnvelope> subEnvelope(final GeneralEnvelope source, final Function<CoordinateReferenceSystem, ? extends SingleCRS> subCrsFinder) {
        return getSubCrs(source::getCoordinateReferenceSystem, subCrsFinder)
                .map(component -> {
                    final int nbDims = component.crs.getCoordinateSystem().getDimension();
                    GeneralEnvelope subEnv = source.subEnvelope(component.idx, component.idx + nbDims);
                    subEnv.setCoordinateReferenceSystem(component.crs);
                    return subEnv;
                });
    }

    static class IndexedComponent<T extends SingleCRS> {
        final int idx;
        final T crs;
        final CoordinateReferenceSystem origin;

        public IndexedComponent(T component, CoordinateReferenceSystem origin) {
            ArgumentChecks.ensureNonNull("Origin CRS", origin);
            ArgumentChecks.ensureNonNull("Extracted component", component);
            this.crs = component;
            this.origin = origin;
            idx = AxisDirections.indexOfColinear(origin.getCoordinateSystem(), component.getCoordinateSystem());
            if (idx < 0) {
                throw new IllegalArgumentException(String.format(
                        "Component %s cannot be found in origin %s",
                        component.getName(), origin.getName()
                ));
            }
        }
    }
}
