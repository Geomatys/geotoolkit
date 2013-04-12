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

import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.Objects;
import java.util.Locale;
import java.util.HashSet;
import java.util.Iterator;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;

import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.io.GridCoverageStorePool;
import org.geotoolkit.gui.swing.tree.MutableTreeNode;
import org.geotoolkit.gui.swing.tree.DefaultMutableTreeNode;
import org.geotoolkit.image.io.metadata.SampleDomain;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.apache.sis.util.ArraysExt;
import org.geotoolkit.util.MeasurementRange;
import org.geotoolkit.internal.sql.table.DefaultEntry;

import static org.geotoolkit.internal.InternalUtilities.adjustForRoundingError;


/**
 * Information about an image format.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.13
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
final class FormatEntry extends DefaultEntry {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -8790032968708208057L;

    /**
     * The image format name as declared in the database. This value shall be a name
     * usable in calls to {@link javax.imageio.ImageIO#getImageReadersByFormatName}.
     * <p>
     * For compatibility reason, the user should be prepared to handle MIME type
     * (as understood by {@link javax.imageio.ImageIO#getImageReadersByMIMEType}).
     * as well. As a heuristic rule, we can consider this value as a MIME type if
     * it contains the {@code '/'} character.
     */
    public final String imageFormat;

    /**
     * Alternative image format names or MIME types for the same reader than the one
     * identified by {@link #imageFormat}. This array is computed when first needed
     * from the list of registered {@link ImageReaderSpi}.
     */
    private transient String[] imageFormats;

    /**
     * The sample dimensions for coverages encoded with this format, or {@code null} if undefined.
     * If non-null, then the list is guaranteed to be non-empty and the list size is equals to the
     * expected number of bands.
     * <p>
     * Empty lists are not allowed because our Image I/O framework interprets that as "no bands",
     * as opposed to "unknown bands" (which is what we mean in the particular case of our database
     * schema).
     * <p>
     * Each {@code SampleDimension} specifies how to convert pixel values to geophysics values,
     * or conversely. Their type (geophysics or not) is format dependent. For example coverages
     * read from PNG files will typically store their data as integer values (non-geophysics),
     * while coverages read from ASCII files will often store their pixel values as real numbers
     * (geophysics values).
     *
     * @see GridSampleDimension#geophysics(boolean)
     */
    public final List<GridSampleDimension> sampleDimensions;

    /**
     * The range of valid sample values and the fill values for each sample dimension.
     */
    public final List<SampleDomain> sampleDomains;

    /**
     * The name of the color palette, or {@code null} if unspecified.
     */
    public final String paletteName;

    /**
     * {@code GEOPHYSICS} if the coverage to be read contains already geophysics values, or
     * {@code PACKED} if the coverage to be read use packed integers.
     */
    public final ViewType viewType;

    /**
     * The pool of coverage loaders, to be created when first needed. We use a different pool
     * instance for each format in order to reuse the same {@link javax.imageio.ImageReader}
     * instance when a {@code coverageLoaders.acquireReader().read(...)} method is invoked.
     */
    private transient GridCoverageStorePool coverageLoaders;

    /**
     * Creates a new entry for this format.
     *
     * @param name        An identifier for this entry.
     * @param imageFormat The Image I/O format name (i.e. the plugin to use).
     * @param paletteName The name of the color palette, or {@code null} if unspecified.
     * @param bands       Sample dimensions for coverages encoded with this format, or {@code null}.
     *                    The bands given to this constructor shall <strong>not</strong> be geophysics.
     */
    protected FormatEntry(final String name, final String imageFormat, final String paletteName,
            final GridSampleDimension[] bands, final ViewType viewType, final String comments)
    {
        super(name, comments);
        this.imageFormat = imageFormat.trim();
        if (bands != null) {
            final boolean geophysics = (viewType == ViewType.GEOPHYSICS);
            final SampleDomain[] domains = new SampleDomain[bands.length];
            for (int i=0; i<bands.length; i++) {
                final GridSampleDimension band = bands[i];
                domains[i] = new FormatSampleDomain(band);
                bands  [i] = band.geophysics(geophysics);
            }
            sampleDimensions = UnmodifiableArrayList.wrap(bands);
            sampleDomains    = UnmodifiableArrayList.wrap(domains);
        } else {
            sampleDimensions = null;
            sampleDomains    = null;
        }
        this.paletteName = paletteName;
        this.viewType    = viewType;
    }

    /**
     * Returns the name of this format.
     */
    @Override
    public String getIdentifier() {
        return (String) super.getIdentifier();
    }

    /**
     * Returns alternative image format names or MIME types for the same reader than the one
     * identified by {@link #imageFormat}. This array is computed from the list of registered
     * {@link ImageReaderSpi}.
     *
     * @return Alternative image format names. This method returns a direct reference to
     *         its internal array - do not modify.
     */
    public synchronized String[] getImageFormats() {
        if (imageFormats == null) {
            imageFormats = getImageFormats(imageFormat);
        }
        return imageFormats;
    }

    /**
     * Returns alternatives to the given image format name or MIME type.
     *
     * @param  imageFormat The image format for which to search for alternatives.
     * @return Possible alternatives to the given image format.
     */
    static String[] getImageFormats(final String imageFormat) {
        final Set<String> names = new HashSet<>();
        for (final Iterator<ImageReaderSpi> it = IIORegistry.getDefaultInstance()
                .getServiceProviders(ImageReaderSpi.class, false); it.hasNext();)
        {
            final ImageReaderSpi spi = it.next();
            final String[] candidates = spi.getFormatNames();
            final String[] mimeTypes  = spi.getMIMETypes();
            if (ArraysExt.containsIgnoreCase(candidates, imageFormat) ||
                ArraysExt.containsIgnoreCase(mimeTypes,  imageFormat))
            {
                names.addAll(Arrays.asList(candidates));
                names.addAll(Arrays.asList(mimeTypes));
            }
        }
        return names.toArray(new String[names.size()]);
    }

    /**
     * Returns the ranges of valid sample values for each band in this format.
     * The range are always expressed in <cite>geophysics</cite> values.
     */
    final MeasurementRange<Double>[] getSampleValueRanges() {
        final List<GridSampleDimension> bands = sampleDimensions;
        if (bands == null) {
            return null;
        }
        @SuppressWarnings({"unchecked","rawtypes"})  // Generic array creation.
        final MeasurementRange<Double>[] ranges = new MeasurementRange[bands.size()];
        for (int i=0; i<ranges.length; i++) {
            final GridSampleDimension band = bands.get(i).geophysics(true);
            /*
             * The call 'roundIfAlmostInteger' is a work-around for rounding error. We perform the
             * workaround here instead than at GridSampleDimension construction time because the
             * minimal and maximal values are the result of a computation, not a stored value.
             */
            ranges[i] = MeasurementRange.create(
                    adjustForRoundingError(band.getMinimumValue()),
                    adjustForRoundingError(band.getMaximumValue()),
                    band.getUnits());
        }
        return ranges;
    }

    /**
     * Returns the pool of coverage loaders associated with this format.
     *
     * @return The pool of coverage loaders.
     */
    public synchronized GridCoverageStorePool getCoverageLoaders() {
        if (coverageLoaders == null) {
            coverageLoaders = new GridCoverageLoader.Pool(this);
        }
        return coverageLoaders;
    }

    /**
     * Returns a tree representation of this format, including
     * {@linkplain SampleDimension sample dimensions} and {@linkplain Category categories}.
     *
     * @param  locale The locale to use for formatting labels in the tree.
     * @return The tree root.
     */
    public MutableTreeNode getTree(final Locale locale) {
        final DefaultMutableTreeNode root = new FormatTreeNode(this);
        if (sampleDimensions != null) {
            for (final GridSampleDimension band : sampleDimensions) {
                final List<Category> categories = band.getCategories();
                final int categoryCount = categories.size();
                final DefaultMutableTreeNode node = new FormatTreeNode(band, locale);
                for (int j=0; j<categoryCount; j++) {
                    node.add(new FormatTreeNode(categories.get(j), locale));
                }
                root.add(node);
            }
        }
        return root;
    }

    /**
     * Overridden as a safety, but should not be necessary since identifiers are supposed
     * to be unique in a given database. We don't compare the sample dimensions because
     * it may be costly.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (super.equals(object)) {
            final FormatEntry that = (FormatEntry) object;
            return Objects.equals(imageFormat, that.imageFormat);
        }
        return false;
    }
}
