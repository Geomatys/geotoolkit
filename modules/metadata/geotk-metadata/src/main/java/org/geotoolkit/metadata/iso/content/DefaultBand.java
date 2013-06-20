/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.metadata.iso.content;

import javax.measure.unit.Unit;
import javax.measure.quantity.Length;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.content.Band;
import org.opengis.metadata.content.BandDefinition;
import org.opengis.metadata.content.PolarizationOrientation;
import org.opengis.metadata.content.TransferFunctionType;

import org.geotoolkit.lang.ValueRange;
import org.geotoolkit.xml.Namespaces;
import org.geotoolkit.internal.jaxb.gco.GO_Real;


/**
 * Range of wavelengths in the electromagnetic spectrum.
 *
 * {@section Geotk extension}
 * The {@link Band} interface defined by ISO 19115-2 is specific to measurements in
 * electromagnetic spectrum. For the needs of Image I/O, an additional interface -
 * {@link org.geotoolkit.image.io.metadata.SampleDimension} - has been defined with
 * a subset of the {@code Band} API but without the restriction to wavelengths.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.19
 *
 * @since 2.1
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.metadata.iso} package.
 */
@Deprecated
@ThreadSafe
@XmlType(name = "MD_Band_Type", propOrder={
    "maxValue",
    "minValue",
    "units",
    "peakResponse",
    "bitsPerValue",
    "toneGradation",
    "scaleFactor",
    "offset",
    "bandBoundaryDefinition",
    "nominalSpatialResolution",
    "transferFunctionType",
    "transmittedPolarization",
    "detectedPolarization"
})
@XmlRootElement(name = "MD_Band")
@XmlSeeAlso(org.geotoolkit.internal.jaxb.gmi.MI_Band.class)
public class DefaultBand extends DefaultRangeDimension implements Band {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -3543010637264725421L;

    /**
     * Longest wavelength that the sensor is capable of collecting within a designated band.
     */
    private Double maxValue;

    /**
     * Shortest wavelength that the sensor is capable of collecting within a designated band.
     */
    private Double minValue;

    /**
     * Units in which sensor wavelengths are expressed. Should be non-null if
     * {@linkplain #getMinValue min value} or {@linkplain #getMaxValue max value}
     * are provided.
     */
    private Unit<Length> units;

    /**
     * Wavelength at which the response is the highest.
     * {@code null} if unspecified.
     */
    private Double peakResponse;

    /**
     * Maximum number of significant bits in the uncompressed representation for the value
     * in each band of each pixel.
     * {@code null} if unspecified.
     */
    private Integer bitsPerValue;

    /**
     * Number of discrete numerical values in the grid data.
     * {@code null} if unspecified.
     */
    private Integer toneGradation;

    /**
     * Scale factor which has been applied to the cell value.
     * {@code null} if unspecified.
     */
    private Double scaleFactor;

    /**
     * The physical value corresponding to a cell value of zero.
     * {@code null} if unspecified.
     */
    private Double offset;

    /**
     * Designation of criterion for defining maximum and minimum wavelengths for a spectral band.
     * {@code null} if unspecified.
     */
    private BandDefinition bandBoundaryDefinition;

    /**
     * Smallest distance between which separate points can be distinguished, as specified in
     * instrument design. {@code null} if unspecified.
     */
    private Double nominalSpatialResolution;

    /**
     * Type of transfer function to be used when scaling a physical value for a given element.
     * {@code null} if unspecified.
     */
    private TransferFunctionType transferFunctionType;

    /**
     * Polarization of the radiation transmitted. {@code null} if unspecified.
     */
    private PolarizationOrientation transmittedPolarization;

    /**
     * Polarization of the radiation detected. {@code null} if unspecified.
     */
    private PolarizationOrientation detectedPolarization;

    /**
     * Constructs an initially empty band.
     */
    public DefaultBand() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultBand(final Band source) {
        super(source);
    }

    /**
     * Returns a Geotk metadata implementation with the same values than the given arbitrary
     * implementation. If the given object is {@code null}, then this method returns {@code null}.
     * Otherwise if the given object is already a Geotk implementation, then the given object is
     * returned unchanged. Otherwise a new Geotk implementation is created and initialized to the
     * attribute values of the given object, using a <cite>shallow</cite> copy operation
     * (i.e. attributes are not cloned).
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     *
     * @since 3.18
     */
    public static DefaultBand castOrCopy(final Band object) {
        return (object == null) || (object instanceof DefaultBand)
                ? (DefaultBand) object : new DefaultBand(object);
    }

    /**
     * Returns the longest wavelength that the sensor is capable of collecting within
     * a designated band. Returns {@code null} if unspecified.
     */
    @Override
    @XmlElement(name = "maxValue")
    @XmlJavaTypeAdapter(GO_Real.class)
    public synchronized Double getMaxValue() {
        return maxValue;
    }

    /**
     * Sets the longest wavelength that the sensor is capable of collecting within a
     * designated band. Returns {@code null} if unspecified.
     *
     * @param newValue The new longest wavelength.
     */
    public synchronized void setMaxValue(final Double newValue) {
        checkWritePermission();
        maxValue = newValue;
    }

    /**
     * Returns the shortest wavelength that the sensor is capable of collecting
     * within a designated band.
     */
    @Override
    @XmlElement(name = "minValue")
    @XmlJavaTypeAdapter(GO_Real.class)
    public synchronized Double getMinValue() {
        return minValue;
    }

    /**
     * Sets the shortest wavelength that the sensor is capable of collecting within
     * a designated band.
     *
     * @param newValue The new shortest wavelength.
     */
    public synchronized void setMinValue(final Double newValue) {
        checkWritePermission();
        minValue = newValue;
    }

    /**
     * Returns the units in which sensor wavelengths are expressed. Should be non-null
     * if {@linkplain #getMinValue min value} or {@linkplain #getMaxValue max value}
     * are provided.
     */
    @Override
    @XmlElement(name = "units")
    public synchronized Unit<Length> getUnits() {
        return units;
    }

    /**
     * Sets the units in which sensor wavelengths are expressed. Should be non-null if
     * {@linkplain #getMinValue min value} or {@linkplain #getMaxValue max value}
     * are provided.
     *
     * @param newValue The new units.
     */
    public synchronized void setUnits(final Unit<Length> newValue) {
        checkWritePermission();
        units = newValue;
    }

    /**
     * Returns the wavelength at which the response is the highest.
     * Returns {@code null} if unspecified.
     */
    @Override
    @XmlElement(name = "peakResponse")
    public synchronized Double getPeakResponse() {
        return peakResponse;
    }

    /**
     * Sets the wavelength at which the response is the highest.
     *
     * @param newValue The new peak response.
     */
    public synchronized void setPeakResponse(final Double newValue) {
        checkWritePermission();
        peakResponse = newValue;
    }

    /**
     * Returns the maximum number of significant bits in the uncompressed
     * representation for the value in each band of each pixel.
     * Returns {@code null} if unspecified.
     */
    @Override
    @ValueRange(minimum=1)
    @XmlElement(name = "bitsPerValue")
    public synchronized Integer getBitsPerValue() {
        return bitsPerValue;
    }

    /**
     * Sets the maximum number of significant bits in the uncompressed representation
     * for the value in each band of each pixel.
     *
     * @param newValue The new number of bits per value.
     */
    public synchronized void setBitsPerValue(final Integer newValue) {
        checkWritePermission();
        bitsPerValue = newValue;
    }

    /**
     * Returns the number of discrete numerical values in the grid data.
     * Returns {@code null} if unspecified.
     */
    @Override
    @ValueRange(minimum=0)
    @XmlElement(name = "toneGradation")
    public synchronized Integer getToneGradation() {
        return toneGradation;
    }

    /**
     * Sets the number of discrete numerical values in the grid data.
     *
     * @param newValue The new tone gradation.
     */
    public synchronized void setToneGradation(final Integer newValue) {
        checkWritePermission();
        toneGradation = newValue;
    }

    /**
     * Returns the scale factor which has been applied to the cell value.
     * Returns {@code null} if unspecified.
     */
    @Override
    @XmlElement(name = "scaleFactor")
    public synchronized Double getScaleFactor() {
        return scaleFactor;
    }

    /**
     * Sets the scale factor which has been applied to the cell value.
     *
     * @param newValue The new scale factor.
     */
    public synchronized void setScaleFactor(final Double newValue) {
        checkWritePermission();
        scaleFactor = newValue;
    }

    /**
     * Returns the physical value corresponding to a cell value of zero.
     * Returns {@code null} if unspecified.
     */
    @Override
    @XmlElement(name = "offset")
    public synchronized Double getOffset() {
        return offset;
    }

    /**
     * Sets the physical value corresponding to a cell value of zero.
     *
     * @param newValue The new offset.
     */
    public synchronized void setOffset(final Double newValue) {
        checkWritePermission();
        offset = newValue;
    }

    /**
     * Returns the designation of criterion for defining maximum and minimum wavelengths
     * for a spectral band. Returns {@code null} if unspecified.
     *
     * @since 3.03
     */
    @Override
    @XmlElement(name = "bandBoundaryDefinition", namespace = Namespaces.GMI)
    public synchronized BandDefinition getBandBoundaryDefinition() {
        return bandBoundaryDefinition;
    }

    /**
     * Sets designation of criterion for defining maximum and minimum wavelengths
     * for a spectral band.
     *
     * @param newValue The new band definition.
     *
     * @since 3.03
     */
    public synchronized void setBandBoundaryDefinition(final BandDefinition newValue) {
        checkWritePermission();
        bandBoundaryDefinition = newValue;
    }

    /**
     * Returns the smallest distance between which separate points can be distinguished,
     * as specified in instrument design. Returns {@code null} if unspecified.
     *
     * @since 3.03
     */
    @Override
    @ValueRange(minimum=0, isMinIncluded=false)
    @XmlElement(name = "nominalSpatialResolution", namespace = Namespaces.GMI)
    public synchronized Double getNominalSpatialResolution() {
        return nominalSpatialResolution;
    }

    /**
     * Sets the smallest distance between which separate points can be distinguished,
     * as specified in instrument design.
     *
     * @param newValue The new nominal spatial resolution.
     *
     * @since 3.03
     */
    public synchronized void setNominalSpatialResolution(final Double newValue) {
        checkWritePermission();
        nominalSpatialResolution = newValue;
    }

    /**
     * Returns type of transfer function to be used when scaling a physical value for a
     * given element. Returns {@code null} if unspecified.
     *
     * @since 3.03
     */
    @Override
    @XmlElement(name = "transferFunctionType", namespace = Namespaces.GMI)
    public synchronized TransferFunctionType getTransferFunctionType() {
        return transferFunctionType;
    }

    /**
     * Sets the type of transfer function to be used when scaling a physical value for a
     * given element.
     *
     * @param newValue The new transfer function value.
     *
     * @since 3.03
     */
    public synchronized void setTransferFunctionType(final TransferFunctionType newValue) {
        checkWritePermission();
        transferFunctionType = newValue;
    }

    /**
     * Polarization of the radiation transmitted. Returns {@code null} if unspecified.
     *
     * @since 3.03
     */
    @Override
    @XmlElement(name = "transmittedPolarization", namespace = Namespaces.GMI)
    public synchronized PolarizationOrientation getTransmittedPolarization() {
        return transmittedPolarization;
    }

    /**
     * Sets the polarization of the radiation transmitted.
     *
     * @param newValue The new transmitted polarization.
     *
     * @since 3.03
     */
    public synchronized void setTransmittedPolarization(final PolarizationOrientation newValue) {
        checkWritePermission();
        transmittedPolarization = newValue;
    }

    /**
     * Polarization of the radiation detected. Returns {@code null} if unspecified.
     *
     * @since 3.03
     */
    @Override
    @XmlElement(name = "detectedPolarization", namespace = Namespaces.GMI)
    public synchronized PolarizationOrientation getDetectedPolarization() {
        return detectedPolarization;
    }

    /**
     * Sets the polarization of the radiation detected.
     *
     * @param newValue The new detected polarization.
     *
     * @since 3.03
     */
    public synchronized void setDetectedPolarization(final PolarizationOrientation newValue) {
        checkWritePermission();
        detectedPolarization = newValue;
    }
}
