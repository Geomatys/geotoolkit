/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.image.io.metadata;

import java.util.Date;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.metadata.IIOInvalidTreeException;
import org.w3c.dom.Node;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.util.logging.LoggedFormat;


/**
 * Geographic informations encoded in image as metadata. This class provides various methods for
 * reading and writting attribute values in {@link IIOMetadataNode} according the {@linkplain
 * GeographicMetadataFormat geographic metadata format}. If some inconsistency are found while
 * reading (for example if the coordinate system dimension doesn't match the envelope dimension),
 * then the default implementation {@linkplain #warningOccurred logs a warning}. We do not throw
 * an exception because minor errors are not uncommon in geographic data, and we want to process
 * the data on a "<cite>best effort</cite>" basis. However because every warnings are logged
 * through the {@link #warningOccurred} method, subclasses can override this method if they want
 * treat some warnings as fatal errors.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.06
 *
 * @since 2.4
 * @module
 *
 * @deprecated Replaced by {@link SpatialMetadata}, which provide a view over the standard
 *   metadata objects defined in ISO 19115-2.
 */
@Deprecated
public class GeographicMetadata extends SpatialMetadata {
    /**
     * The coordinate reference system node.
     * Will be created only when first needed.
     */
    private ImageReferencing referencing;

    /**
     * The geometry information.
     * Will be created only when first needed.
     */
    private ImageGeometry geometry;

    /**
     * The list of {@linkplain Band bands}.
     * Will be created only when first needed.
     */
    private ChildList<Band> bands;

    /**
     * The standard date format. Will be created only when first needed.
     */
    private transient LoggedFormat<Date> dateFormat;

    /**
     * Creates a default metadata instance. This constructor defines no standard or native format.
     * The only format defined is the {@linkplain GeographicMetadataFormat geographic} one.
     */
    public GeographicMetadata() {
        this((ImageReader) null);
    }

    /**
     * Creates a default metadata instance for the given reader.
     *
     * @param reader The source image reader, or {@code null} if none.
     */
    public GeographicMetadata(final ImageReader reader) {
        super(GeographicMetadataFormat.getInstance(), reader, null);
        init();
    }

    /**
     * Creates a default metadata instance for the given writer.
     *
     * @param writer The target image writer, or {@code null} if none.
     */
    public GeographicMetadata(final ImageWriter writer) {
        super(GeographicMetadataFormat.getInstance(), writer, null);
        init();
    }

    /**
     * Creates a default metadata instance. This constructor defines no standard or native format.
     * The only format defined is the {@linkplain GeographicMetadataFormat geographic} one.
     */
    private void init() {
        extraMetadataFormatNames = new String[] {
                  GeographicMetadataFormat.FORMAT_NAME
              };
        extraMetadataFormatClassNames = new String[] {
                  "org.geotoolkit.image.io.metadata.GeographicMetadataFormat"
              };
    }

    /**
     * Returns the grid referencing.
     *
     * @return The grid referencing.
     */
    public ImageReferencing getReferencing() {
        if (referencing == null) {
            referencing = new ImageReferencing(this);
        }
        return referencing;
    }

    /**
     * Returns the grid geometry.
     *
     * @return The grid geometry.
     */
    public ImageGeometry getGeometry() {
        if (geometry == null) {
            geometry = new ImageGeometry(this);
        }
        return geometry;
    }

    /**
     * Returns the list of all {@linkplain Band bands}.
     */
    final ChildList<Band> getBands() {
        if (bands == null) {
            bands = new Band.List(this);
        }
        return bands;
    }

    /**
     * Returns the sample type (typically
     * {@value org.geotoolkit.image.io.metadata.GeographicMetadataFormat#GEOPHYSICS} or
     * {@value org.geotoolkit.image.io.metadata.GeographicMetadataFormat#PACKED}), or
     * {@code null} if none. This type applies to all {@linkplain Band bands}.
     *
     * @return The type of sample values.
     */
    public String getSampleType() {
        return getBands().getAttribute("type");
    }

    /**
     * Sets the sample type for all {@linkplain Band bands}. Valid types include
     * {@value org.geotoolkit.image.io.metadata.GeographicMetadataFormat#GEOPHYSICS} and
     * {@value org.geotoolkit.image.io.metadata.GeographicMetadataFormat#PACKED}.
     *
     * @param type The sample type, or {@code null} if none.
     */
    public void setSampleType(final String type) {
        getBands().setAttribute("type", type, GeographicMetadataFormat.SAMPLE_TYPES);
    }

    /**
     * Returns the number of {@linkplain Band bands} in the coverage.
     *
     * @return The number of bands.
     */
    public int getNumBands() {
        return getBands().childCount();
    }

    /**
     * Returns the band at the specified index.
     *
     * @param  bandIndex the band index, ranging from 0 inclusive to {@link #getNumBands} exclusive.
     * @return The band at the specified index.
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     */
    public Band getBand(final int bandIndex) throws IndexOutOfBoundsException {
        return getBands().getChild(bandIndex);
    }

    /**
     * Creates a new band and returns it.
     *
     * @param name The name for the new band.
     * @return The new band.
     */
    public Band addBand(final String name) {
        final Band band = getBands().addChild();
        band.setName(name);
        return band;
    }

    /**
     * Checks the format name.
     */
    private void checkFormatName(final String formatName) throws IllegalArgumentException {
        if (!GeographicMetadataFormat.FORMAT_NAME.equals(formatName)) {
            throw new IllegalArgumentException(Errors.getResources(getLocale()).getString(
                    Errors.Keys.ILLEGAL_ARGUMENT_$2, "formatName", formatName));
        }
    }

    /**
     * Alters the internal state of this metadata from a tree defined by the specified metadata.
     * The default implementation expects the
     * {@value org.geotoolkit.image.io.metadata.GeographicMetadataFormat#FORMAT_NAME} format.
     *
     * @param  metadata The metadata to merge to this object.
     * @throws IIOInvalidTreeException If the metadata can not be merged.
     */
    @Override
    public void mergeTree(final IIOMetadata metadata) throws IIOInvalidTreeException {
        final Node tree;
        try {
            tree = metadata.getAsTree(GeographicMetadataFormat.FORMAT_NAME);
        } catch (IllegalArgumentException exception) {
            throw new IIOInvalidTreeException(Errors.format(
                    Errors.Keys.GEOTOOLKIT_EXTENSION_REQUIRED_$1, "mergeTree"), exception, null);
        }
        mergeTree(GeographicMetadataFormat.FORMAT_NAME, tree);
    }

    /**
     * Resets all the data stored in this object to default values.
     */
    @Override
    public void reset() {
        referencing = null;
        geometry    = null;
        bands       = null;
        super.reset();
    }

    /**
     * Returns a string representation of this metadata, mostly for debugging purpose.
     */
    @Override
    public String toString() {
        return Trees.toString(Trees.xmlToSwing(getAsTree(GeographicMetadataFormat.FORMAT_NAME)));
    }
}
