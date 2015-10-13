/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2015, Geomatys
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
package org.geotoolkit.processing;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import org.opengis.util.InternationalString;
import org.apache.sis.util.iso.ResourceInternationalString;
import org.apache.sis.util.resources.IndexedResourceBundle;


/**
 * Locale-dependent resources for words or simple sentences.
 *
 * @author Martin Desruisseaux (Geomatys)
 */
public final class ProcessBundle extends IndexedResourceBundle {
    /**
     * Resource keys. This class is used when compiling sources, but no dependencies to
     * {@code Keys} should appear in any resulting class files. Since the Java compiler
     * inlines final integer values, using long identifiers will not bloat the constant
     * pools of compiled classes.
     *
     * @author Martin Desruisseaux (IRD)
     * @version 3.00
     *
     * @since 2.2
     */
    public static final class Keys {
        private Keys() {
        }

        /**
         * Input coverages
         */
        public static final short coverage_bandcombine_inCoverages = 0;

        /**
         * Coverages to merge
         */
        public static final short coverage_bandcombine_inCoveragesDesc = 1;

        /**
         * Output coverage
         */
        public static final short coverage_bandcombine_outCoverage = 2;

        /**
         * Result combined coverage
         */
        public static final short coverage_bandcombine_outCoverageDesc = 3;

        /**
         * Input band selection
         */
        public static final short coverage_bandselect_inBands = 4;

        /**
         * Indexes of coverage bands to extract
         */
        public static final short coverage_bandselect_inBandsDesc = 5;

        /**
         * Input coverage
         */
        public static final short coverage_bandselect_inCoverage = 6;

        /**
         * Coverage from where to extract bands
         */
        public static final short coverage_bandselect_inCoverageDesc = 7;

        /**
         * Output coverage
         */
        public static final short coverage_bandselect_outCoverage = 8;

        /**
         * Result coverage
         */
        public static final short coverage_bandselect_outCoverageDesc = 9;

        /**
         * Create an isoline FeatureCollection from a GridCoverage and an array of intervals.
         */
        public static final short coverage_isoline_abstract = 10;

        /**
         * Input GridCoverage.
         */
        public static final short coverage_isoline_inCoverage = 11;

        /**
         * Input CoverageReference.
         */
        public static final short coverage_isoline_inCoverageRef = 12;

        /**
         * Input FeatureStore used to store output isolines.
         */
        public static final short coverage_isoline_inFeatureStore = 13;

        /**
         * Output isoline FeatureType name
         */
        public static final short coverage_isoline_inFeatureTypeName = 14;

        /**
         * Array of intervals.
         */
        public static final short coverage_isoline_inIntervals = 15;

        /**
         * Output FeatureCollection.
         */
        public static final short coverage_isoline_outFeatureCollection = 16;

        /**
         * Input coverages
         */
        public static final short coverage_mathcalc_inCoverages = 17;

        /**
         * Formula
         */
        public static final short coverage_mathcalc_inFormula = 18;

        /**
         * Coverage to formula variable
         */
        public static final short coverage_mathcalc_inMapping = 19;

        /**
         * Output writable coverage
         */
        public static final short coverage_mathcalc_inResultCoverage = 20;

        /**
         * Input coverages
         */
        public static final short coverage_merge_inCoverages = 21;

        /**
         * Coverages to merge
         */
        public static final short coverage_merge_inCoveragesDesc = 22;

        /**
         * Merge envelope
         */
        public static final short coverage_merge_inEnvelope = 23;

        /**
         * Result coverage envelope
         */
        public static final short coverage_merge_inEnvelopeDesc = 24;

        /**
         * Merge resolution
         */
        public static final short coverage_merge_inResolution = 25;

        /**
         * Result coverage resolution
         */
        public static final short coverage_merge_inResolutionDesc = 26;

        /**
         * Output coverage
         */
        public static final short coverage_merge_outCoverage = 27;

        /**
         * Result combined coverage
         */
        public static final short coverage_merge_outCoverageDesc = 28;

        /**
         * Source coverage
         */
        public static final short coverage_metaextract_inCoverage = 29;

        /**
         * Coverage to extract metadata from.
         */
        public static final short coverage_metaextract_inCoverageDesc = 30;

        /**
         * Extracted metadata
         */
        public static final short coverage_metaextract_outMeta = 31;

        /**
         * The metadata process succeed to get from the source coverage.
         */
        public static final short coverage_metaextract_outMetaDesc = 32;

        /**
         * Input coverage
         */
        public static final short coverage_reformat_inCoverage = 33;

        /**
         * Coverage to reformat
         */
        public static final short coverage_reformat_inCoverageDesc = 34;

        /**
         * Sample type
         */
        public static final short coverage_reformat_inType = 35;

        /**
         * Sample primitive type
         */
        public static final short coverage_reformat_inTypeDesc = 36;

        /**
         * Output coverage
         */
        public static final short coverage_reformat_outCoverage = 37;

        /**
         * Result reformated coverage
         */
        public static final short coverage_reformat_outCoverageDesc = 38;

        /**
         * input coordinate reference system
         */
        public static final short coverage_resample_inCRS = 39;

        /**
         * target crs for resampling
         */
        public static final short coverage_resample_inCRSDesc = 40;

        /**
         * input coverage
         */
        public static final short coverage_resample_inCoverage = 41;

        /**
         * the input coverage to resample
         */
        public static final short coverage_resample_inCoverageDesc = 42;

        /**
         * input envelope
         */
        public static final short coverage_resample_inEnvelope = 43;

        /**
         * the target envelope of resampling
         */
        public static final short coverage_resample_inEnvelopeDesc = 44;

        /**
         * output coverage
         */
        public static final short coverage_resample_outCoverage = 45;

        /**
         * result resampled coverage
         */
        public static final short coverage_resample_outCoverageDesc = 46;

        /**
         * Graphical shaded relief
         */
        public static final short coverage_shadedrelief_abstract = 47;

        /**
         * Input base coverage
         */
        public static final short coverage_shadedrelief_inCoverage = 48;

        /**
         * Input elevation coverage
         */
        public static final short coverage_shadedrelief_inElevation = 49;

        /**
         * Result shaded coverage
         */
        public static final short coverage_shadedrelief_outCoverage = 50;

        /**
         * Give statistics on image (min, max, distribution array)
         */
        public static final short coverage_statistic_abstract = 51;

        /**
         * Statistic Process
         */
        public static final short coverage_statistic_display_name = 52;

        /**
         * Input coverage
         */
        public static final short coverage_statistic_inCoverage = 53;

        /**
         * Exclude no-data
         */
        public static final short coverage_statistic_inExcludeNoData = 54;

        /**
         * Input image
         */
        public static final short coverage_statistic_inImage = 55;

        /**
         * Input image index
         */
        public static final short coverage_statistic_inImageIdx = 56;

        /**
         * Input coverage reader
         */
        public static final short coverage_statistic_inReader = 57;

        /**
         * Input coverage reference
         */
        public static final short coverage_statistic_inReference = 58;

        /**
         * Statistic from input coverage
         */
        public static final short coverage_statistic_outCoverage = 59;

        /**
         * Input images
         */
        public static final short image_bandcombine_inImages = 60;

        /**
         * Images to merge
         */
        public static final short image_bandcombine_inImagesDesc = 61;

        /**
         * Output image
         */
        public static final short image_bandcombine_outImage = 62;

        /**
         * Result combined image
         */
        public static final short image_bandcombine_outImageDesc = 63;

        /**
         * Input band selection
         */
        public static final short image_bandselect_inBands = 64;

        /**
         * Indexes of image bands to extract
         */
        public static final short image_bandselect_inBandsDesc = 65;

        /**
         * Input image
         */
        public static final short image_bandselect_inImage = 66;

        /**
         * Image from where to extract bands
         */
        public static final short image_bandselect_inImageDesc = 67;

        /**
         * Output image
         */
        public static final short image_bandselect_outImage = 68;

        /**
         * Result image
         */
        public static final short image_bandselect_outImageDesc = 69;

        /**
         * Input band selection
         */
        public static final short image_colorstretch_inBands = 70;

        /**
         * Indexes of image bands to extract
         */
        public static final short image_colorstretch_inBandsDesc = 71;

        /**
         * Input image
         */
        public static final short image_colorstretch_inImage = 72;

        /**
         * Image to recolor
         */
        public static final short image_colorstretch_inImageDesc = 73;

        /**
         * Input band value range
         */
        public static final short image_colorstretch_inRanges = 74;

        /**
         * Input band value range
         */
        public static final short image_colorstretch_inRangesDesc = 75;

        /**
         * Output image
         */
        public static final short image_colorstretch_outImage = 76;

        /**
         * Result recolored image
         */
        public static final short image_colorstretch_outImageDesc = 77;

        /**
         * Input image
         */
        public static final short image_reformat_inImage = 78;

        /**
         * Image to reformat
         */
        public static final short image_reformat_inImageDesc = 79;

        /**
         * Sample type
         */
        public static final short image_reformat_inType = 80;

        /**
         * Sample primitive type
         */
        public static final short image_reformat_inTypeDesc = 81;

        /**
         * Output image
         */
        public static final short image_reformat_outImage = 82;

        /**
         * Result reformated image
         */
        public static final short image_reformat_outImageDesc = 83;

        /**
         * Input image
         */
        public static final short image_replace_inImage = 84;

        /**
         * Image where to replace samples
         */
        public static final short image_replace_inImageDesc = 85;

        /**
         * Replacement values
         */
        public static final short image_replace_inReplacements = 86;

        /**
         * Replacement values
         */
        public static final short image_replace_inReplacementsDesc = 87;

        /**
         * Output image
         */
        public static final short image_replace_outImage = 88;

        /**
         * Result image
         */
        public static final short image_replace_outImageDesc = 89;
    }

    /**
     * Constructs a new resource bundle loading data from the given UTF file.
     *
     * @param filename The file or the JAR entry containing resources.
     */
    public ProcessBundle(final java.net.URL filename) {
        super(filename);
    }

    /**
     * Returns resources in the given locale.
     *
     * @param  locale The locale, or {@code null} for the default locale.
     * @return Resources in the given locale.
     * @throws MissingResourceException if resources can't be found.
     */
    public static ProcessBundle getResources(Locale locale) throws MissingResourceException {
        return getBundle(ProcessBundle.class, locale);
    }

    /**
     * The international string to be returned by {@link formatInternational}.
     */
    private static final class International extends ResourceInternationalString {
        private static final long serialVersionUID = -9199238559657784488L;

        International(final int key) {
            super(ProcessBundle.class.getName(), String.valueOf(key));
        }

        @Override
        protected ResourceBundle getBundle(final Locale locale) {
            return getResources(locale);
        }
    }

    /**
     * Gets an international string for the given key. This method does not check for the key
     * validity. If the key is invalid, then a {@link MissingResourceException} may be thrown
     * when a {@link InternationalString#toString} method is invoked.
     *
     * @param  key The key for the desired string.
     * @return An international string for the given key.
     */
    public static InternationalString formatInternational(final short key) {
        return new International(key);
    }

    /**
     * Gets an international string for the given key. This method does not check for the key
     * validity. If the key is invalid, then a {@link MissingResourceException} may be thrown
     * when a {@link InternationalString#toString} method is invoked.
     *
     * {@note This method is redundant with the one expecting <code>Object...</code>, but is
     *        provided for binary compatibility with previous Geotk versions. It also avoid the
     *        creation of a temporary array. There is no risk of confusion since the two methods
     *        delegate their work to the same <code>format</code> method anyway.}
     *
     * @param  key The key for the desired string.
     * @param  arg Values to substitute to "{0}".
     * @return An international string for the given key.
     *
     * @todo Current implementation just invokes {@link #format}. Need to format only when
     *       {@code toString(Locale)} is invoked.
     */
    public static InternationalString formatInternational(final short key, final Object arg) {
        return new org.apache.sis.util.iso.SimpleInternationalString(format(key, arg));
    }

    /**
     * Gets an international string for the given key. This method does not check for the key
     * validity. If the key is invalid, then a {@link MissingResourceException} may be thrown
     * when a {@link InternationalString#toString} method is invoked.
     *
     * @param  key The key for the desired string.
     * @param  args Values to substitute to "{0}", "{1}", <i>etc</i>.
     * @return An international string for the given key.
     *
     * @todo Current implementation just invokes {@link #format}. Need to format only when
     *       {@code toString(Locale)} is invoked.
     */
    public static InternationalString formatInternational(final short key, final Object... args) {
        return new org.apache.sis.util.iso.SimpleInternationalString(format(key, args));
    }

    /**
     * Gets a string for the given key from this resource bundle or one of its parents.
     *
     * @param  key The key for the desired string.
     * @return The string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public static String format(final short key) throws MissingResourceException {
        return getResources(null).getString(key);
    }

    /**
     * Gets a string for the given key are replace all occurrence of "{0}"
     * with values of {@code arg0}.
     *
     * @param  key The key for the desired string.
     * @param  arg0 Value to substitute to "{0}".
     * @return The formatted string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public static String format(final short  key,
                                final Object arg0) throws MissingResourceException
    {
        return getResources(null).getString(key, arg0);
    }

    /**
     * Gets a string for the given key are replace all occurrence of "{0}",
     * "{1}", with values of {@code arg0}, {@code arg1}.
     *
     * @param  key The key for the desired string.
     * @param  arg0 Value to substitute to "{0}".
     * @param  arg1 Value to substitute to "{1}".
     * @return The formatted string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public static String format(final short  key,
                                final Object arg0,
                                final Object arg1) throws MissingResourceException
    {
        return getResources(null).getString(key, arg0, arg1);
    }

    /**
     * Gets a string for the given key are replace all occurrence of "{0}",
     * "{1}", with values of {@code arg0}, {@code arg1}, etc.
     *
     * @param  key The key for the desired string.
     * @param  arg0 Value to substitute to "{0}".
     * @param  arg1 Value to substitute to "{1}".
     * @param  arg2 Value to substitute to "{2}".
     * @return The formatted string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public static String format(final short  key,
                                final Object arg0,
                                final Object arg1,
                                final Object arg2) throws MissingResourceException
    {
        return getResources(null).getString(key, arg0, arg1, arg2);
    }
}
