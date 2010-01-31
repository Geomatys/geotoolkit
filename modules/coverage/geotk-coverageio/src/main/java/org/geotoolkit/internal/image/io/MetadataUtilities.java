/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
package org.geotoolkit.internal.image.io;

import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.metadata.IIOInvalidTreeException;

import org.geotoolkit.lang.Static;
import org.geotoolkit.util.XArrays;


/**
 * Utility methods working on {@link IIOMetadata} objects.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
 *
 * @since 3.08
 * @module
 */
@Static
public final class MetadataUtilities {
    /**
     * Do not allow instatiation of this class.
     */
    private MetadataUtilities() {
    }

    /**
     * Merges the two specified trees. If both source and target metadata are non-null,
     * then this method performs the following steps:
     * <p>
     * <ul>
     *   <li>Searches for the first format name which is common to both metadata;</li>
     *   <li>invokes {@link IIOMetadata#getAsTree(String)} on the source metadata;</li>
     *   <li>invokes {@link IIOMetadata#mergeTree(String, Node)} on the target metadata.</li>
     * </ul>
     *
     * @param  source The source metadata, or {@code null}.
     * @param  target The target metadata, or {@code null}.
     * @return {@code source} if {@code target} was null, or {@code target} otherwise.
     *
     * @throws IllegalStateException if {@code target} is read-only.
     * @throws IIOInvalidTreeException if the {@code source} tree cannot be parsed successfully.
     */
    public static IIOMetadata merge(final IIOMetadata source, final IIOMetadata target)
            throws IllegalStateException, IIOInvalidTreeException
    {
        if (source == null) {
            return target;
        }
        if (target == null) {
            return source;
        }
        final String format = commonFormatName(source, target);
        if (format != null) {
            target.mergeTree(format, source.getAsTree(format));
        }
        return null;
    }

    /**
     * Returns the name of a format which is common to both metadata.
     * The preferred formats are (in order):
     * <p>
     * <ul>
     *   <li>The native format of target metadata.<li>
     *   <li>The native format of source metadata.<li>
     *   <li>A format supported by both metadata which is not the standard format.</li>
     *   <li>The standard format is last resort, because it contains no geographic
     *       data and we wanted to give the priority to geographic formats.</li>
     * </ul>
     * <p>
     * If no common format is found, then this method returns {@code null}.
     */
    private static String commonFormatName(final IIOMetadata source, final IIOMetadata target) {
        final String[] sourceFormats = source.getMetadataFormatNames();
        String format = target.getNativeMetadataFormatName();
        if (format != null) {
            if (XArrays.contains(sourceFormats, format)) {
                return format;
            }
        }
        /*
         * The target native format is not supported. Try the source native format. We will search
         * only in extra names (not in all names) because we don't want to consider the standard
         * format now, and because it is not worth to test again the target native format since we
         * just did that in the block before.
         */
        final String[] targetFormats = target.getExtraMetadataFormatNames();
        if (targetFormats != null) {
            format = source.getNativeMetadataFormatName();
            if (format != null) {
                if (XArrays.contains(targetFormats, format)) {
                    return format;
                }
            }
            /*
             * Checks if there is a target extra format supported by the source metadata.
             */
            for (int i=0; i<targetFormats.length; i++) {
                format = targetFormats[i];
                if (XArrays.contains(sourceFormats, format)) {
                    return format;
                }
            }
        }
        /*
         * The standard format is the only one left. We try it last because it contains no
         * geographic information, and we wanted to give the priority to geographic formats.
         */
        if (source.isStandardMetadataFormatSupported() && target.isStandardMetadataFormatSupported()) {
            return IIOMetadataFormatImpl.standardMetadataFormatName;
        }
        return null;
    }
}
