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
        public static final short coverage_bandcombine_inCoverages = 1;

        /**
         * Coverages to merge
         */
        public static final short coverage_bandcombine_inCoveragesDesc = 2;

        /**
         * Output coverage
         */
        public static final short coverage_bandcombine_outCoverage = 3;

        /**
         * Result combined coverage
         */
        public static final short coverage_bandcombine_outCoverageDesc = 4;

        /**
         * Input band selection
         */
        public static final short coverage_bandselect_inBands = 5;

        /**
         * Indexes of coverage bands to extract
         */
        public static final short coverage_bandselect_inBandsDesc = 6;

        /**
         * Input coverage
         */
        public static final short coverage_bandselect_inCoverage = 7;

        /**
         * Coverage from where to extract bands
         */
        public static final short coverage_bandselect_inCoverageDesc = 8;

        /**
         * Output coverage
         */
        public static final short coverage_bandselect_outCoverage = 9;

        /**
         * Result coverage
         */
        public static final short coverage_bandselect_outCoverageDesc = 10;

        /**
         * Create an isoline FeatureCollection from a GridCoverage and an array of intervals.
         */
        public static final short coverage_isoline_abstract = 11;

        /**
         * Input GridCoverage.
         */
        public static final short coverage_isoline_inCoverage = 12;

        /**
         * Input CoverageReference.
         */
        public static final short coverage_isoline_inCoverageRef = 13;

        /**
         * Input FeatureStore used to store output isolines.
         */
        public static final short coverage_isoline_inFeatureStore = 14;

        /**
         * Output isoline FeatureType name
         */
        public static final short coverage_isoline_inFeatureTypeName = 15;

        /**
         * Array of intervals.
         */
        public static final short coverage_isoline_inIntervals = 16;

        /**
         * Output FeatureCollection.
         */
        public static final short coverage_isoline_outFeatureCollection = 17;

        /**
         * Input coverages
         */
        public static final short coverage_mathcalc_inCoverages = 18;

        /**
         * Formula
         */
        public static final short coverage_mathcalc_inFormula = 19;

        /**
         * Coverage to formula variable
         */
        public static final short coverage_mathcalc_inMapping = 20;

        /**
         * Output writable coverage
         */
        public static final short coverage_mathcalc_inResultCoverage = 21;

        /**
         * Input coverages
         */
        public static final short coverage_merge_inCoverages = 22;

        /**
         * Coverages to merge
         */
        public static final short coverage_merge_inCoveragesDesc = 23;

        /**
         * Merge envelope
         */
        public static final short coverage_merge_inEnvelope = 24;

        /**
         * Result coverage envelope
         */
        public static final short coverage_merge_inEnvelopeDesc = 25;

        /**
         * Merge resolution
         */
        public static final short coverage_merge_inResolution = 26;

        /**
         * Result coverage resolution
         */
        public static final short coverage_merge_inResolutionDesc = 27;

        /**
         * Output coverage
         */
        public static final short coverage_merge_outCoverage = 28;

        /**
         * Result combined coverage
         */
        public static final short coverage_merge_outCoverageDesc = 29;

        /**
         * Source coverage
         */
        public static final short coverage_metaextract_inCoverage = 30;

        /**
         * Coverage to extract metadata from.
         */
        public static final short coverage_metaextract_inCoverageDesc = 31;

        /**
         * Extracted metadata
         */
        public static final short coverage_metaextract_outMeta = 32;

        /**
         * The metadata process succeed to get from the source coverage.
         */
        public static final short coverage_metaextract_outMetaDesc = 33;

        /**
         * Input coverage
         */
        public static final short coverage_reformat_inCoverage = 34;

        /**
         * Coverage to reformat
         */
        public static final short coverage_reformat_inCoverageDesc = 35;

        /**
         * Sample type
         */
        public static final short coverage_reformat_inType = 36;

        /**
         * Sample primitive type
         */
        public static final short coverage_reformat_inTypeDesc = 37;

        /**
         * Output coverage
         */
        public static final short coverage_reformat_outCoverage = 38;

        /**
         * Result reformated coverage
         */
        public static final short coverage_reformat_outCoverageDesc = 39;

        /**
         * input coordinate reference system
         */
        public static final short coverage_resample_inCRS = 40;

        /**
         * target crs for resampling
         */
        public static final short coverage_resample_inCRSDesc = 41;

        /**
         * input coverage
         */
        public static final short coverage_resample_inCoverage = 42;

        /**
         * the input coverage to resample
         */
        public static final short coverage_resample_inCoverageDesc = 43;

        /**
         * input envelope
         */
        public static final short coverage_resample_inEnvelope = 44;

        /**
         * the target envelope of resampling
         */
        public static final short coverage_resample_inEnvelopeDesc = 45;

        /**
         * output coverage
         */
        public static final short coverage_resample_outCoverage = 46;

        /**
         * result resampled coverage
         */
        public static final short coverage_resample_outCoverageDesc = 47;

        /**
         * Graphical shaded relief
         */
        public static final short coverage_shadedrelief_abstract = 48;

        /**
         * Input base coverage
         */
        public static final short coverage_shadedrelief_inCoverage = 49;

        /**
         * Input elevation coverage
         */
        public static final short coverage_shadedrelief_inElevation = 50;

        /**
         * Result shaded coverage
         */
        public static final short coverage_shadedrelief_outCoverage = 51;

        /**
         * Give statistics on image (min, max, distribution array)
         */
        public static final short coverage_statistic_abstract = 52;

        /**
         * Statistic Process
         */
        public static final short coverage_statistic_display_name = 53;

        /**
         * Input coverage
         */
        public static final short coverage_statistic_inCoverage = 54;

        /**
         * Exclude no-data
         */
        public static final short coverage_statistic_inExcludeNoData = 55;

        /**
         * Input image
         */
        public static final short coverage_statistic_inImage = 56;

        /**
         * Input image index
         */
        public static final short coverage_statistic_inImageIdx = 57;

        /**
         * Input coverage reader
         */
        public static final short coverage_statistic_inReader = 58;

        /**
         * Input coverage reference
         */
        public static final short coverage_statistic_inReference = 59;

        /**
         * Statistic from input coverage
         */
        public static final short coverage_statistic_outCoverage = 60;

        /**
         * Input images
         */
        public static final short image_bandcombine_inImages = 61;

        /**
         * Images to merge
         */
        public static final short image_bandcombine_inImagesDesc = 62;

        /**
         * Output image
         */
        public static final short image_bandcombine_outImage = 63;

        /**
         * Result combined image
         */
        public static final short image_bandcombine_outImageDesc = 64;

        /**
         * Input band selection
         */
        public static final short image_bandselect_inBands = 65;

        /**
         * Indexes of image bands to extract
         */
        public static final short image_bandselect_inBandsDesc = 66;

        /**
         * Input image
         */
        public static final short image_bandselect_inImage = 67;

        /**
         * Image from where to extract bands
         */
        public static final short image_bandselect_inImageDesc = 68;

        /**
         * Output image
         */
        public static final short image_bandselect_outImage = 69;

        /**
         * Result image
         */
        public static final short image_bandselect_outImageDesc = 70;

        /**
         * Input band selection
         */
        public static final short image_colorstretch_inBands = 71;

        /**
         * Indexes of image bands to extract
         */
        public static final short image_colorstretch_inBandsDesc = 72;

        /**
         * Input image
         */
        public static final short image_colorstretch_inImage = 73;

        /**
         * Image to recolor
         */
        public static final short image_colorstretch_inImageDesc = 74;

        /**
         * Input band value range
         */
        public static final short image_colorstretch_inRanges = 75;

        /**
         * Input band value range
         */
        public static final short image_colorstretch_inRangesDesc = 76;

        /**
         * Output image
         */
        public static final short image_colorstretch_outImage = 77;

        /**
         * Result recolored image
         */
        public static final short image_colorstretch_outImageDesc = 78;

        /**
         * Input image
         */
        public static final short image_reformat_inImage = 79;

        /**
         * Image to reformat
         */
        public static final short image_reformat_inImageDesc = 80;

        /**
         * Sample type
         */
        public static final short image_reformat_inType = 81;

        /**
         * Sample primitive type
         */
        public static final short image_reformat_inTypeDesc = 82;

        /**
         * Output image
         */
        public static final short image_reformat_outImage = 83;

        /**
         * Result reformated image
         */
        public static final short image_reformat_outImageDesc = 84;

        /**
         * Input image
         */
        public static final short image_replace_inImage = 85;

        /**
         * Image where to replace samples
         */
        public static final short image_replace_inImageDesc = 86;

        /**
         * Replacement values
         */
        public static final short image_replace_inReplacements = 87;

        /**
         * Replacement values
         */
        public static final short image_replace_inReplacementsDesc = 88;

        /**
         * Output image
         */
        public static final short image_replace_outImage = 89;

        /**
         * Result image
         */
        public static final short image_replace_outImageDesc = 90;
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
