/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.coverage.sql;

import java.io.File;
import java.util.Map;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.Collection;
import java.awt.image.RenderedImage;

import org.opengis.metadata.extent.GeographicBoundingBox;

import org.geotoolkit.util.DateRange;
import org.apache.sis.measure.MeasurementRange;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.io.CoverageStoreException;


/**
 * A layer of grid coverages sharing common properties. Layers are created by the
 * {@link CoverageDatabase#getLayer(String)} method. The list of layers available
 * in a database can be displayed in a <cite>Swing</cite> widget using the
 * {@link org.geotoolkit.gui.swing.coverage.LayerList} panel.
 * <p>
 * {@code Layer} instances are immutable and thread-safe.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.16
 *
 * @since 3.10 (derived from Seagis)
 * @module
 */
public interface Layer {
    /**
     * Returns the database which created this layer, or {@code null} if unknown.
     * The returned value is never null except when this {@code Layer} is the result
     * of a deserialization.
     *
     * @return The database which created this layer, or {@code null} if unknown.
     *
     * @since 3.12
     */
    CoverageDatabase getCoverageDatabase();

    /**
     * Returns the name of this layer.
     *
     * @return The layer name.
     */
    String getName();

    /**
     * Returns the number of coverages in this layer.
     *
     * @return The number of coverages in this layer.
     * @throws CoverageStoreException if an error occurred while counting the coverages.
     */
    int getCoverageCount() throws CoverageStoreException;

    /**
     * Returns a time range encompassing all coverages in this layer, or {@code null} if none.
     *
     * @return The time range encompassing all coverages, or {@code null}.
     * @throws CoverageStoreException if an error occurred while fetching the time range.
     */
    DateRange getTimeRange() throws CoverageStoreException;

    /**
     * Returns the set of dates when a coverage is available.
     *
     * @return The set of dates, or {@code null} if unknown.
     * @throws CoverageStoreException if an error occurred while fetching the set.
     */
    SortedSet<Date> getAvailableTimes() throws CoverageStoreException;

    /**
     * Returns the set of altitudes where a coverage is available. If different coverages
     * have different set of altitudes, then this method returns the union of all altitudes
     * set.
     *
     * @return The set of altitudes, or {@code null} if unknown.
     * @throws CoverageStoreException if an error occurred while fetching the set.
     */
    SortedSet<Number> getAvailableElevations() throws CoverageStoreException;

    /**
     * Returns the ranges of valid <cite>geophysics</cite> values for each band. The length of
     * the returned list is the maximal number of bands in all coverages. If some coverages
     * found in this layer have different range of values, then this method returns the union
     * of their ranges.
     *
     * @return The range of valid sample values for each bands.
     * @throws CoverageStoreException If an error occurred while fetching the information.
     */
    List<MeasurementRange<?>> getSampleValueRanges() throws CoverageStoreException;

    /**
     * Creates a color ramp for the coverages in this layer. This method searches for a
     * {@linkplain org.geotoolkit.coverage.Category category} which intersect the given
     * range. Then, an image is created with the colors from that category and optionally
     * a graduation for the given range.
     * <p>
     * Note that the default implementation of this method requires the optional
     * <a href="http://www.geotoolkit.org/modules/display/geotk-display">{@code geotk-display}</a>
     * module to be on reachable the classpath.
     *
     * {@note There is usually only one quantitative category for a layer. However if more
     * than one quantitative category is found, the one which seems the "best fit" for the
     * given range is selected. The definition of "best fit" is implementation-dependent.}
     *
     * The range given to this method is typically the following value:
     *
     * {@preformat java
     *     MeasurementRange<?> range = getSampleValueRanges().get(band);
     * }
     *
     * However a different value can be specified, typically in the following situations:
     * <p>
     * <ul>
     *   <li>When no graduation is desired, the range can be {@code null}.</li>
     *   <li>When the same range needs to be expressed in different units.
     *       In such case, the value given to this method can be computed by
     *       <code>range.{@linkplain MeasurementRange#convertTo convertTo}(displayUnit)</code>.</li>
     *   <li>When the caller want to apply the color palette on a subrange of the layer range.
     *       In such case, the range given to this method can be the subrange of interest.
     *       Note that it is caller responsibility to apply a corresponding
     *       {@link org.geotoolkit.coverage.processing.ColorMap} operation on the
     *       {@linkplain GridCoverageReference#read coverage read}.</li>
     * </ul>
     * <p>
     * This method accepts an optional map of properties, which provide more control on
     * the image to be generated. Current implementation recognizes the following entries
     * (all other entries are silently ignored):
     * <p>
     * <table border="1" cellspacing="0" cellpadding="3">
     *   <tr>
     *     <th nowrap>Key</th>
     *     <th nowrap>Value type</th>
     *     <th nowrap>Description</th>
     *   </tr>
     *   <tr valign="top">
     *     <td>{@code "size"}</td>
     *     <td>{@link java.awt.Dimension}</td>
     *     <td>The image size, in pixels.</td>
     *   </tr>
     *   <tr valign="top">
     *     <td>{@code "font"}</td>
     *     <td>{@link java.awt.Font}</td>
     *     <td>The font to use for rendering graduation labels.</td>
     *   </tr>
     *   <tr valign="top">
     *     <td>{@code "foreground"}</td>
     *     <td>{@link java.awt.Color}</td>
     *     <td>The color to use for rendering graduation labels.</td>
     *   </tr>
     *   <tr valign="top">
     *     <td>{@code "graphics"}</td>
     *     <td>{@link java.awt.Graphics2D}</td>
     *     <td>If provided, paints the image using the given graphics handle instead than
     *         creating a new image. In such case, this method returns {@code null}.</td>
     *   </tr>
     * </table>
     *
     * @param  band The band for which to create a color ramp, from 0 inclusive to
     *         <code>{@linkplain #getSampleValueRanges()}.size()</code> exclusive.
     * @param  range The range for the graduation, or {@code null} if no graduation
     *         should be written. See the above javadoc for a suggested value.
     * @param  properties An optional map of properties controlling the rendering.
     *         See the above javadoc for a description of expected entries.
     * @return The color ramp as an image, or {@code null} if none.
     * @throws IllegalArgumentException If the units of the given range are incompatible
     *         with the units of measurement found in this layer.
     * @throws CoverageStoreException If an error occurred while creating the color ramp.
     *
     * @see org.geotoolkit.gui.swing.image.ColorRamp
     *
     * @since 3.16
     */
    RenderedImage getColorRamp(int band, MeasurementRange<?> range, Map<String,?> properties)
            throws CoverageStoreException, IllegalArgumentException;

    /**
     * Returns the image format used by the coverages in this layer, sorted by decreasing frequency
     * of use. The strings in the returned set shall be names known to the {@linkplain javax.imageio
     * Java Image I/O} framework.
     *
     * @return The image formats, with the most frequently used format first.
     * @throws CoverageStoreException if an error occurred while querying the database.
     */
    SortedSet<String> getImageFormats() throws CoverageStoreException;

    /**
     * Returns the directories where the image files are stored, sorted by decreasing frequency
     * of use.
     *
     * @return The image directories, with the most frequently used directory first.
     * @throws CoverageStoreException if an error occurred while querying the database.
     *
     * @since 3.12
     */
    SortedSet<File> getImageDirectories() throws CoverageStoreException;

    /**
     * Returns the typical pixel resolution in this layer. Values are in the unit of the
     * {@linkplain CoverageDatabase#getCoordinateReferenceSystem() main CRS used by the database}
     * (typically degrees of longitude and latitude for the horizontal part, and days for the
     * temporal part). Some elements of the returned array may be {@link Double#NaN NaN} if
     * they are unknown.
     *
     * @return The typical pixel resolution.
     * @throws CoverageStoreException if an error occurred while fetching the resolution.
     */
    double[] getTypicalResolution() throws CoverageStoreException;

    /**
     * Returns the grid geometries used by the coverages in this layer, sorted by decreasing
     * frequency of use. The grid geometries may be 2D, 3D or 4D, including the vertical and
     * temporal ranges if any. The Coordinate Reference System is the one declared in the
     * database for the coverages.
     *
     * @return The grid geometries, with the most frequently used geometry first.
     * @throws CoverageStoreException if an error occurred while querying the database.
     */
    SortedSet<GeneralGridGeometry> getGridGeometries() throws CoverageStoreException;

    /**
     * Returns the geographic bounding box, or {@code null} if unknown. If the CRS used by
     * the database is not geographic (for example if it is a projected CRS), then this method
     * will transform the layer envelope from the layer CRS to a geographic CRS.
     *
     * @return The layer geographic bounding box, or {@code null} if none.
     * @throws CoverageStoreException if an error occurred while querying the database
     *         or while projecting the layer envelope.
     *
     * @since 3.11
     */
    GeographicBoundingBox getGeographicBoundingBox() throws CoverageStoreException;

    /**
     * Returns the envelope of this layer, optionally centered at the given date and
     * elevation. Callers are free to modify the returned instance before to pass it
     * to the {@code getCoverageReference} methods.
     *
     * @param  time The central date, or {@code null}.
     * @param  elevation The central elevation, or {@code null}.
     * @return A default envelope instance.
     * @throws CoverageStoreException if an error occurred while querying the database.
     */
    CoverageEnvelope getEnvelope(Date time, Number elevation) throws CoverageStoreException;

    /**
     * Returns a reference to every coverages available in this layer which intersect the
     * given envelope. This method does not load immediately the coverages; it returns only
     * <em>references</em> to the coverages.
     * <p>
     * If the given envelope is {@code null}, then this method returns the references to
     * every coverages available in this layer regardless of their envelope.
     *
     * @param  envelope The envelope for filtering the coverages, or {@code null} for no filtering.
     * @return The set of coverages in the layer which intersect the given envelope.
     * @throws CoverageStoreException if an error occurred while querying the database.
     */
    Set<GridCoverageReference> getCoverageReferences(CoverageEnvelope envelope) throws CoverageStoreException;

    /**
     * Returns a reference to a coverage that intersect the given envelope. If more than one
     * coverage intersect the given envelope, then this method will select the one which seem
     * the most representative. The criterion for this selection is implementation-dependant
     * and may change in future versions.
     *
     * @param  envelope The envelope for filtering the coverages, or {@code null} for no
     *         filtering. A {@code null} value is useful for layers that are expected to
     *         contain only one coverage, but should be avoided otherwise.
     * @return A reference to a coverage, or {@code null} if no coverage was found.
     * @throws CoverageStoreException if an error occurred while querying the database.
     */
    GridCoverageReference getCoverageReference(CoverageEnvelope envelope) throws CoverageStoreException;

    /**
     * Adds new coverage references in the database. The new references are given by a collection
     * of inputs. Each input can be any of the following instances:
     *
     * <ul>
     *   <li><p>{@link java.io.File}, {@link java.net.URL}, {@link java.net.URI} or
     *       {@link String} instances.</p></li>
     *
     *   <li><p>{@link org.geotoolkit.image.io.mosaic.Tile} instances, which will be added to the
     *       {@code "GridCoverages"} table (not to the {@code "Tiles"} table).</p></li>
     *
     *   <li><p>{@link javax.imageio.ImageReader} instances with their
     *       {@linkplain javax.imageio.ImageReader#getInput() input} set and
     *       {@linkplain javax.imageio.ImageReader#getImageMetadata image metadata} conform to the Geotk
     *       {@linkplain org.geotoolkit.image.io.metadata.SpatialMetadata spatial metadata} format. The
     *       reader input shall be one of the above-cited instances. If this is not possible (for example
     *       because a {@link javax.imageio.stream.ImageInputStream} is required), consider wrapping
     *       the {@link javax.imageio.spi.ImageReaderSpi} and the input in a
     *       {@link org.geotoolkit.image.io.mosaic.Tile} instance.</p></li>
     * </ul>
     *
     * This method will typically read only the required metadata rather than the full image.
     *
     * {@section Multi-images files}
     * If a file contains more than one image, then the images to insert can be selected by the
     * {@link CoverageDatabaseController#filterImages(List, boolean)} method. If no controller
     * has been supplied, then the default behavior is to insert only the first image on the
     * assumption that other images are typically error estimations or overviews.
     *
     * @param  files The image inputs.
     * @param  controller An optional controller to be notified when new references are added.
     *         The controller can modify the values declared in {@link NewGridCoverageReference}
     *         before they are written in the database.
     * @throws DatabaseVetoException If a {@linkplain CoverageDatabaseListener listener}
     *         vetoed against the operation.
     * @throws CoverageStoreException If an error occurred while accessing the database.
     *
     * @since 3.12
     */
    void addCoverageReferences(Collection<?> files, CoverageDatabaseController controller)
            throws DatabaseVetoException, CoverageStoreException;

    /**
     * Returns a layer to use as a fallback if no data is available in this layer for a given
     * position. For example if no data is available in a weekly averaged <cite>Sea Surface
     * Temperature</cite> (SST) coverage because a location is masked by clouds, we may want
     * to look in the monthly averaged SST coverage as a fallback.
     *
     * @return The fallback layer, or {@code null} if none.
     * @throws CoverageStoreException If an error occurred while fetching the information.
     */
    Layer getFallback() throws CoverageStoreException;
}
