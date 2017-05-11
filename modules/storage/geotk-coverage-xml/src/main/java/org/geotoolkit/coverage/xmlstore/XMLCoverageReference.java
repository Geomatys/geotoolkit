/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2014, Geomatys
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
package org.geotoolkit.coverage.xmlstore;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import javax.swing.ProgressMonitor;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.storage.coverage.AbstractPyramidalCoverageReference;
import org.geotoolkit.storage.coverage.GridMosaic;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.storage.coverage.Pyramid;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.util.NamesExt;
import org.opengis.coverage.SampleDimensionType;
import org.opengis.util.GenericName;
import org.geotoolkit.image.color.ScaledColorSpace;
import org.geotoolkit.image.internal.ImageUtils;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.internal.jdk8.JDK8;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.image.internal.PlanarConfiguration;
import org.geotoolkit.image.internal.SampleType;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

/**
 * XML implementation of {@link PyramidalCoverageReference}.
 *
 * @author Johann Sorel  (Geomatys)
 * @author Remi Marechal (Geomatys)
 * @module
 */
@XmlRootElement(name="CoverageReference")
public class XMLCoverageReference extends AbstractPyramidalCoverageReference {

    /**
     * Changes :
     * 1.1 - number format used to name folder was using system local,
     *      local is fixed to EN in 1.1.
     */
    private static final String CURRENT_VERSION = "1.1";

    @XmlTransient
    private static MarshallerPool POOL;
    static {
        try {
            POOL = new MarshallerPool(JAXBContext.newInstance(XMLCoverageReference.class), null);
        } catch (JAXBException ex) {
            Logging.getLogger("org.geotoolkit.coverage.xmlstore").log(Level.WARNING, ex.getMessage(), ex);
            throw new RuntimeException("Failed to initialize JAXB XML Coverage reference marshaller pool.");
        }
    }

    private static final GenericName DEFAULT_NAME = NamesExt.create("default");

    @XmlElement(name="Version")
    private String version = CURRENT_VERSION;

    @XmlElement(name="PyramidSet")
    private XMLPyramidSet set;

    /** One of geophysics/native */
    @XmlElement(name="packMode")
    private String packMode = ViewType.RENDERED.name();

    @XmlElement(name="SampleDimension")
    private List<XMLSampleDimension> sampleDimensions = null;

    @XmlElement(name="NumDimension")
    private int numDimension;

    @XmlElement(name="PreferredFormat")
    private String preferredFormat;

    //-- needed element to define sampleModel of current pyramided tiles

    /**
     * Define band number of all internaly pyramid stored tiles.
     */
    @XmlElement(name="NumBands")
    private int nbBands = -1;

    /**
     * Define internal datatype of all internaly pyramid stored tiles.
     */
    @XmlElement(name="SampleType")
    private int sampleType = -1;

    /**
     * Define bits per sample number of all internaly pyramid stored tiles.
     */
    @XmlElement(name="BitPerSample")
    private int bitPerSample = -1;

    /**
     * Define sample number by pixels of all internaly pyramid stored tiles.
     */
    @XmlElement(name="SamplePerPixel")
    private int samplePerPixel = -1;

    /**
     * Define planar configuration of all internaly pyramid stored tiles.
     *
     * @see ImageUtils#PLANAR_BANDED
     * @see ImageUtils#PLANAR_INTERLEAVED
     */
    @XmlElement(name="PlanarConfiguration")
    private int planarConfiguration = -1;

    /**
     * Define sample format of all internaly pyramid stored tiles.
     *
     * @see ImageUtils#SAMPLEFORMAT_IEEEFP
     * @see ImageUtils#SAMPLEFORMAT_INT
     * @see ImageUtils#SAMPLEFORMAT_UINT
     */
    @XmlElement(name="SampleFormat")
    private int sampleFormat = -1;

    /**
     * Define photometric interpretation of all internaly pyramid stored tiles.
     *
     * @see ImageUtils#PHOTOMETRIC_MINISBLACK
     * @see ImageUtils#PHOTOMETRIC_PALETTE
     * @see ImageUtils#PHOTOMETRIC_RGB
     */
    @XmlElement(name="PhotometricInterpretation")
    private int photometricInterpretation = -1;

    /**
     * Define color map of all internaly pyramid stored tiles.
     * Only use if photometric interpretation is use as {@linkplain ImageUtils#PHOTOMETRIC_PALETTE palette}.
     *
     * @see ImageUtils#PHOTOMETRIC_PALETTE
     */
    @XmlElement(name="ColorMap")
    private int[] colorMap = null;

    /**
     * Minimum sample value only use in case where {@link ColorSpace} from {@link #colorModel}
     * is instance of {@link ScaledColorSpace} and type of samples are Float or double type.
     * @see ScaledColorSpace
     */
    @XmlElement(name="MinColorSpaceSampleValue")
    private Double minColorSampleValue = null;

    /**
     * Maximum sample value only use in case where {@link ColorSpace} from {@link #colorModel}
     * is instance of {@link ScaledColorSpace} and type of samples are Float or double type.
     * @see ScaledColorSpace
     */
    @XmlElement(name="MaxColorSpaceSampleValue")
    private Double maxColorSampleValue = null;

    /**
     * Define if current {@link ColorModel} own an alpha component.
     * @see #getColorModel()
     * @see ColorModel#hasAlpha()
     */
    @XmlElement(name="HasAlpha")
    private Boolean hasAlpha = null;

    /**
     * Define if alpha has been premultiplied in the
     * pixel values from by this {@linkplain #colorModel ColorModel}.
     * @see #getColorModel()
     * @see ColorModel#isAlphaPremultiplied()
     */
    @XmlElement(name="IsAlphaPremultiplied")
    private Boolean isAlphaPremultiplied = null;

    /**
     * {@link SampleModel} of all internally pyramid stored tiles.
     */
    private SampleModel sampleModel;

    /**
     * {@link ColorModel} of all internally pyramid stored tiles.
     */
    private ColorModel colorModel;

    private String id;
    private Path mainfile;
    //caches
    private List<GridSampleDimension> cacheDimensions = null;

    public XMLCoverageReference() {
        super(null, DEFAULT_NAME, 0);
    }

    public XMLCoverageReference(XMLCoverageStore store, GenericName name, XMLPyramidSet set) {
        super(store,name,0);
        this.set = set;
        this.set.setRef(this);
    }

    public List<XMLSampleDimension> getXMLSampleDimensions() {
        if (sampleDimensions == null) sampleDimensions = new ArrayList<>();
        return sampleDimensions;
    }

    public void copy(XMLCoverageReference ref){
        this.version                    = ref.version;
        this.id                         = ref.id;
        this.mainfile                   = ref.mainfile;
        this.set                        = ref.set;
        this.packMode                   = ref.packMode;
        this.sampleDimensions           = ref.sampleDimensions;
        this.preferredFormat            = ref.preferredFormat;
        this.nbBands                    = ref.nbBands;
//        this.sampleType                 = ref.sampleType;
        this.bitPerSample               = ref.bitPerSample;
//        this.samplePerPixel             = ref.samplePerPixel;
        this.planarConfiguration        = ref.planarConfiguration;
        this.sampleFormat               = ref.sampleFormat;
        this.photometricInterpretation  = ref.photometricInterpretation;
        this.colorMap                   = ref.colorMap;
        this.minColorSampleValue        = ref.minColorSampleValue;
        this.maxColorSampleValue        = ref.maxColorSampleValue;
        this.set.setRef(this);
    }

    void initialize(Path mainFile) throws DataStoreException {
        this.mainfile = mainFile;
        //calculate id based on file name
        id = IOUtilities.filenameWithoutExtension(mainFile);

        // In case we created the reference by unmarshalling a file, the pyramid set is not bound to its parent coverage reference.
        final XMLPyramidSet set = getPyramidSet();
        if (set.getRef() == null) {
            set.setRef(this);
        }
        for (XMLPyramid pyramid : set.pyramids()) {
            pyramid.initialize(set);
        }
    }

    public String getVersion() {
        if(version==null) version = CURRENT_VERSION;
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getId() {
        return id;
    }

    /**
     * @return xml file where the pyramid set definition is stored.
     */
    public Path getMainfile() {
        return mainfile;
    }

    /**
     * @return Folder where each pyramid is stored.
     */
    public Path getFolder(){
        return mainfile.getParent().resolve(getId());
    }

    @Override
    public XMLPyramidSet getPyramidSet() {
        return set;
    }

    private void checkColorModel(){
        if (minColorSampleValue == null) {
            assert maxColorSampleValue == null;
            if (colorModel != null)
            assert !(colorModel.getColorSpace() instanceof ScaledColorSpace) : "with min and max NULL sample value color space should not be instance of ScaledColorSpace.";
        } else {
            assert maxColorSampleValue != null;
            assert (colorModel.getColorSpace() instanceof ScaledColorSpace) : "with NOT NULL min and max sample value color space must be instance of ScaledColorSpace.";
            assert JDK8.isFinite(minColorSampleValue) : "To write minColorSampleValue into XML Pyramid File, it should be finite. Found : "+minColorSampleValue;
            assert JDK8.isFinite(maxColorSampleValue) : "To write maxColorSampleValue into XML Pyramid File, it should be finite. Found : "+maxColorSampleValue;
        }
    }

    /**
     * Save the coverage reference in the file
     * @throws DataStoreException
     */
    private final AtomicInteger save = new AtomicInteger(0b00);

    void save() throws DataStoreException {
        //unecessary here, values are checked when setting colormodel and updating tile
        //checkColorModel();

        /*
        The save atomic integer contains 2 bits.
        0b01 : is a flag for 'save is needed'
        0b10 : is a flag for 'someone has taken the charge of saving'
        This is an optimized opportunist saving approach.
        One thread might do more then one saving work but this avoid a global
        contention when multiple save operations occur.
        */
        if((save.getAndSet(0b11) & 0b10) == 0){
            //0b10 flag was not set, no thread was currently saving so we must take this role.
            while(updateAndGet(save)!=0){
                //keep saving un 0b01 flag is set to zero
                try (OutputStream os = Files.newOutputStream(getMainfile(), CREATE, WRITE, TRUNCATE_EXISTING))  {
                    final Marshaller marshaller = POOL.acquireMarshaller();
                    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                    marshaller.marshal(this, os);
                    POOL.recycle(marshaller);
                } catch (JAXBException ex) {
                    Logging.getLogger("org.geotoolkit.coverage.xmlstore").log(Level.WARNING, ex.getMessage(), ex);
                } catch (IOException e) {
                    throw new DataStoreException("Unable to save pyramid definition : "+e.getLocalizedMessage(), e);
                }
            }
        }
    }

    /**
     * Modified backport copy from JDK8 AtomicInteger.updateAndGet
     */
    private static final int updateAndGet(AtomicInteger ati) {
        int prev, next;
        do {
            prev = ati.get();
            next = prev==0b11 ? 0b10 : 0b00;
        } while (!ati.compareAndSet(prev, next));
        return next;
    }


    /**
     * Read the given file and return an XMLCoverageReference.
     *
     * @param file
     * @return
     * @throws JAXBException if an error occured while reading descriptor file.
     * @throws org.apache.sis.storage.DataStoreException if the file describe a pyramid, but it contains an invalid CRS.
     */
    @Deprecated
    public static XMLCoverageReference read(File file) throws JAXBException, DataStoreException {
        final Unmarshaller unmarshaller = POOL.acquireUnmarshaller();
        final XMLCoverageReference ref;
        ref = (XMLCoverageReference) unmarshaller.unmarshal(file);
        POOL.recycle(unmarshaller);
        ref.initialize(file.toPath());
        return ref;
    }

    /**
     * Read the given Path and return an XMLCoverageReference.
     *
     * @param file
     * @return
     * @throws JAXBException if an error occurred while reading descriptor file.
     * @throws org.apache.sis.storage.DataStoreException if the file describe a pyramid, but it contains an invalid CRS.
     */
    public static XMLCoverageReference read(Path file) throws JAXBException, DataStoreException, IOException {
        final Unmarshaller unmarshaller = POOL.acquireUnmarshaller();
        final XMLCoverageReference ref;
        try (InputStream is = Files.newInputStream(file)) {
            ref = (XMLCoverageReference) unmarshaller.unmarshal(is);
            POOL.recycle(unmarshaller);
            ref.initialize(file);
        }
        return ref;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Meta informations methods ///////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    public void setPreferredFormat(String preferredFormat) {
        this.preferredFormat = preferredFormat;
    }

    public String getPreferredFormat() {
        return preferredFormat;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public synchronized List<GridSampleDimension> getSampleDimensions() throws DataStoreException {
        if (cacheDimensions == null) {
            if (sampleDimensions == null) return null;
            assert !sampleDimensions.isEmpty() : "XmlCoverageReference.getSampleDimension : sampleDimension should not be empty.";
            cacheDimensions = new CopyOnWriteArrayList<>();
            for (XMLSampleDimension xsd : sampleDimensions) {
                cacheDimensions.add(xsd.buildSampleDimension());
            }
        }
        return cacheDimensions;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void setSampleDimensions(List<GridSampleDimension> dimensions) throws DataStoreException {
        if (dimensions == null || dimensions.isEmpty()) return;
        this.cacheDimensions = null; //clear cache

        if (sampleDimensions == null) sampleDimensions = new CopyOnWriteArrayList<>();
        sampleDimensions.clear();
        for (GridSampleDimension dimension : dimensions) {
            dimension = dimension.geophysics(false);
            final SampleDimensionType sdt = dimension.getSampleDimensionType();
            final XMLSampleDimension dim  = new XMLSampleDimension();
            dim.fill(dimension);
            dim.setSampleType(sdt);
            sampleDimensions.add(dim);
        }
        assert !sampleDimensions.isEmpty() : "XmlCoverageReference.setSampleDimension : sampleDimension should not be empty.";
        save();
    }

    /**
     * Returns {@link ColorModel} associated with internal pyramid data.
     *
     * Note : if internal {@link ColorModel} is {@code null}, the expected sample
     * model is re-built from internal unmarshalling informations which are :<br>
     * - {@linkplain #bitPerSample bit Per Sample}<br>
     * - {@linkplain #nbBands band number}<br>
     * - {@linkplain #samplePerPixel sample Per Pixel}<br>
     * - {@linkplain #planarConfiguration planar configuration}<br>
     * - {@linkplain #photometricInterpretation photometric interpretation}<br>
     * - {@linkplain #sampleFormat sample format}<br><br>
     *
     * Moreover should return {@code null} when old pyramid was unmarshall, where
     * internal color model informations was missing.
     *
     * @return {@link ColorModel} associated with internal pyramid data.
     * @throws IllegalArgumentException if photometric interpretation is define
     * as palette (photometricInterpretation == 3) and colorMap is {@code null}.
     * @see ImageUtils#createColorModel(int, int, short, short, long[])
     */
    @Override
    public ColorModel getColorModel() {
        if (colorModel == null) {
            if (checkAttributs()) {
                if (minColorSampleValue == null) {
                    assert maxColorSampleValue == null;
                    if (colorModel != null)
                    assert !(colorModel.getColorSpace() instanceof ScaledColorSpace) : "with min and max NULL sample value color space should not be instance of ScaledColorSpace.";
                } else {
                    assert maxColorSampleValue != null;
                    assert (colorModel.getColorSpace() instanceof ScaledColorSpace) : "with NOT NULL min and max sample value color space must be instance of ScaledColorSpace.";
                    assert JDK8.isFinite(minColorSampleValue) : "To write minColorSampleValue into XML Pyramid File, it should be finite. Found : "+minColorSampleValue;
                    assert JDK8.isFinite(maxColorSampleValue) : "To write maxColorSampleValue into XML Pyramid File, it should be finite. Found : "+maxColorSampleValue;
                }
                colorModel = ImageUtils.createColorModel(bitPerSample, nbBands,
                        (short) photometricInterpretation, (short) sampleFormat,
                        minColorSampleValue, maxColorSampleValue,
                        (hasAlpha != null) ? hasAlpha : false, (isAlphaPremultiplied != null) ? isAlphaPremultiplied : false,
                        colorMap);
            }
        }
        return colorModel;
    }

    /**
     * Set the associate internal pyramid data {@link ColorModel}.<br>
     *
     * Note : also set some attributes needed to marshall / unmarshall.
     *
     * @param colorModel {@code ColorModel} internal data.
     * @throws DataStoreException
     * @see #photometricInterpretation
     */
    @Override
    public void setColorModel(ColorModel colorModel) throws DataStoreException {
        ArgumentChecks.ensureNonNull("colorModel", colorModel);
        //--photometric
        this.photometricInterpretation = ImageUtils.getPhotometricInterpretation(colorModel);
        this.hasAlpha                  = colorModel.hasAlpha();
        this.isAlphaPremultiplied      = colorModel.isAlphaPremultiplied();
        if (photometricInterpretation == 3) {
            assert colorModel instanceof IndexColorModel : "with photometric interpretation "
                    + "define as palette color model should be instance of IndexColorModel.";
            final IndexColorModel indexColorMod = ((IndexColorModel) colorModel);
            int mapSize = indexColorMod.getMapSize();
            colorMap  = new int[mapSize];
            indexColorMod.getRGBs(colorMap);
        }
        this.colorModel = colorModel;
        checkColorModel();
        //-- code in comment in attempt to update TiffImageReader to scan all sampleValues to build appropriate colorSpace
//        final ColorSpace colorSpace = colorModel.getColorSpace();
//        if (colorSpace instanceof ScaledColorSpace) {
//            minColorSampleValue = (minColorSampleValue == null)
//                                  ? colorSpace.getMinValue(0)
//                                  : StrictMath.min(minColorSampleValue, colorSpace.getMinValue(0));
//            maxColorSampleValue = (maxColorSampleValue == null)
//                                  ? colorSpace.getMaxValue(0)
//                                  : StrictMath.max(maxColorSampleValue, colorSpace.getMaxValue(0));
//
//        }
        save();
    }

    /**
     * Returns {@link SampleModel} associated with internal pyramid data.
     *
     * Note : if internal {@link SampleModel} is {@code null}, the expected sample
     * model is re-built from internal unmarshalling informations which are :<br>
     * - {@linkplain #bitPerSample bit Per Sample}<br>
     * - {@linkplain #nbBands band number}<br>
     * - {@linkplain #samplePerPixel sample Per Pixel}<br>
     * - {@linkplain #planarConfiguration planar configuration}<br>
     * - {@linkplain #photometricInterpretation photometric interpretation}<br>
     * - {@linkplain #sampleFormat sample format}<br><br>
     *
     * Moreover should return {@code null} when old pyramid was unmarshall, where
     * internal sample model informations was missing.
     *
     * @return {@link SampleModel} associated with internal pyramid data.
     * @see ImageUtils#buildImageTypeSpecifier(int, int, short, short, short, long[])
     */
    @Override
    public SampleModel getSampleModel() {
        if (sampleModel == null) {
            final ColorModel colMod = getColorModel();
            if (colMod != null && planarConfiguration != -1) {
                //-- we can directly create sample model, attributs have already been checked.
                sampleModel = ImageUtils.createSampleModel(PlanarConfiguration.valueOf(planarConfiguration),
                                                           SampleType.valueOf(bitPerSample, sampleFormat),
                                                           1, 1, nbBands);
            }
            //-- else do nothing case where unmarshall old pyramid. older comportement.
        }
        return sampleModel;
    }

    /**
     * Returns {@code true} if all needed attributs to re-build appropriate
     * {@link SampleModel} and {@link ColorModel} else return {@code false}.
     *
     * @return
     */
    private boolean checkAttributs() {
        return (bitPerSample != -1)
            && (nbBands != -1)
//            && (samplePerPixel != -1)
            && (sampleFormat != -1)
            && (planarConfiguration != -1)
            && (photometricInterpretation != -1);
    }

    /**
     * Set the associate internal pyramid data {@link SampleModel}.<br>
     * note : also set some attributs needed to marshall/unmarshall pyramid.
     *
     * @param sampleModel expected {@link SampleModel} needed to marshall.
     * @throws org.apache.sis.storage.DataStoreException if problem during XML save.
     * @see #nbBands
     * @see #sampleType
     * @see #samplePerPixel
     */
    @Override
    public void setSampleModel(SampleModel sampleModel) throws DataStoreException {
//        this.sampleType          = sampleModel.getDataType();
        this.nbBands             = sampleModel.getNumBands();
        sampleFormat             = ImageUtils.getSampleFormat(sampleModel);
        final int[] bitPerSample = sampleModel.getSampleSize();
//        samplePerPixel           = bitPerSample.length;
        assert bitPerSample.length == nbBands;
        this.bitPerSample        = bitPerSample[0];
        planarConfiguration      = ImageUtils.getPlanarConfiguration(sampleModel);
        this.sampleModel         = sampleModel;
        save();
    }

    /**
     * Verify conformity between a {@link SampleModel} and {@link ColorModel} given by a tile which WILL BE stored
     * into this pyramid and the already pyramid setted sampleModel.
     *
     * If they haven't got any {@link SampleModel} and {@link ColorModel} precedently setted,
     * this method set them automaticaly, from image parameter.
     *
     * @param image {@link RenderedImage} which contain samplemodel color model informations.
     */
    private void checkOrSetSampleColor(final RenderedImage image) throws DataStoreException {
        final SampleModel imgSm = image.getSampleModel();
        final SampleModel sm = getSampleModel();
        if (sm != null) {
//            if (imgSm.getDataType() != sampleType)
//                throw new IllegalArgumentException(String.format("Mismatch sample type. Expected : %d .Found : %d", sampleType, imgSm.getDataType()));

            if (imgSm.getNumBands() != nbBands)
                throw new IllegalArgumentException(String.format("Mismatch bands number. Expected : %d .Found : %d", nbBands, imgSm.getNumBands()));

            if (ImageUtils.getSampleFormat(imgSm) != sampleFormat)
                throw new IllegalArgumentException(String.format("Mismatch sample format. Expected : %d .Found : %d", sampleFormat, ImageUtils.getSampleFormat(imgSm)));

            if (imgSm.getSampleSize()[0] != bitPerSample)
                throw new IllegalArgumentException(String.format("Mismatch sample size (bits per samples). Expected : %d .Found : %d", bitPerSample, imgSm.getSampleSize()[0]));

//            if (imgSm.getSampleSize().length != samplePerPixel)
//                throw new IllegalArgumentException(String.format("Mismatch sample per pixel. Expected : %d .Found : %d", samplePerPixel, imgSm.getSampleSize().length));

            if (ImageUtils.getPlanarConfiguration(imgSm) != planarConfiguration)
                throw new IllegalArgumentException(String.format("Mismatch planar configuration. Expected : %d .Found : %d", planarConfiguration, ImageUtils.getPlanarConfiguration(imgSm)));
        } else {
            setSampleModel(imgSm);
        }

        final ColorModel cm    = getColorModel();
        final ColorModel imgCm = image.getColorModel();

        //-- each tile may have different min and max value in its internal colorspace but it must be same type class.
        final ColorSpace imgCmCS = imgCm.getColorSpace();
        if (imgCmCS instanceof ScaledColorSpace) {

            if (cm != null && (!(cm.getColorSpace() instanceof ScaledColorSpace)))
                throw new IllegalArgumentException(String.format("Mismatch color space."));

            if (minColorSampleValue == null) minColorSampleValue = Double.POSITIVE_INFINITY;
            if (maxColorSampleValue == null) maxColorSampleValue = Double.NEGATIVE_INFINITY;

            //-- when largeRenderedImage will own its right ScaledColorSpace
            //-- moreover to have right ScaledColorSpace tiff reader must travel all of sample values.
            //-- discomment following coding row
//                {
//                    minColorSampleValue = StrictMath.min(minColorSampleValue, imgCmCS.getMinValue(0));
//                    maxColorSampleValue = StrictMath.max(maxColorSampleValue, imgCmCS.getMaxValue(0));
//                }
            //-- now to be in accordance with ScaledColorSpace properties
            //-- travel all current image to find minimum and maximum raster values
            final PixelIterator pix = PixelIteratorFactory.createDefaultIterator(image);
            while (pix.next()) {
                //-- to avoid unexpected NAN values comportement don't use StrictMath class.
                final double value = pix.getSampleDouble();
                if (value < minColorSampleValue) {
                    minColorSampleValue = value;
                } else if (value > maxColorSampleValue) {
                    maxColorSampleValue = value;
                }
            }
            //-- to refresh min and max of current stored color model
            this.colorModel = null; //-- see : getColorModel()
        }

        if (this.colorModel != null) {
            if (ImageUtils.getPhotometricInterpretation(this.colorModel) != photometricInterpretation)
                throw new IllegalArgumentException(String.format("Mismatch photometric interpretation. Expected : %d .Found : %d", photometricInterpretation, ImageUtils.getPhotometricInterpretation(cm)));
        } else {
            setColorModel(imgCm);
        }
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public ViewType getPackMode() {
        return ViewType.valueOf(packMode);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void setPackMode(ViewType packMode) {
        this.packMode = packMode.name();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Creation,Edition methods ////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc }.
     */
    @Override
    public boolean isWritable() throws CoverageStoreException {
        return true;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public Pyramid createPyramid(CoordinateReferenceSystem crs) throws DataStoreException {
        final XMLPyramidSet set = getPyramidSet();
        final Pyramid pyramid = set.createPyramid(getName().tip().toString(),crs);
        save();
        return pyramid;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void deletePyramid(String pyramidId) throws DataStoreException {
        throw new DataStoreException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public GridMosaic createMosaic(String pyramidId, Dimension gridSize,
    Dimension tilePixelSize, DirectPosition upperleft, double pixelscale) throws DataStoreException {
        final XMLPyramidSet set = getPyramidSet();
        final XMLPyramid pyramid = (XMLPyramid) set.getPyramid(pyramidId);
        final XMLMosaic mosaic = pyramid.createMosaic(gridSize, tilePixelSize, upperleft, pixelscale);
        save();
        return mosaic;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public GridMosaic createMosaic(String pyramidId, Dimension gridSize, Dimension tilePixelSize,
            Dimension dataPixelSize, DirectPosition upperleft, double pixelscale) throws DataStoreException {
        final XMLPyramidSet set = getPyramidSet();
        final XMLPyramid pyramid = (XMLPyramid) set.getPyramid(pyramidId);
        final XMLMosaic mosaic = pyramid.createMosaic(gridSize, tilePixelSize, dataPixelSize, upperleft, pixelscale);
        save();
        return mosaic;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void deleteMosaic(String pyramidId, String mosaicId) throws DataStoreException {
        throw new DataStoreException("Not supported yet.");
    }

    public Runnable acquireTileWriter(final BlockingQueue<XMLTileWriter.XMLTileInfo> tilesToWrite) {
        return new XMLTileWriter(tilesToWrite, this);
    }

    /**
     * {@inheritDoc }.
     * @throws java.lang.IllegalArgumentException if mismatch in image DataType or image band number.
     */
    @Override
    public void writeTile(String pyramidId, String mosaicId, int col, int row, RenderedImage image) throws DataStoreException {
        final XMLPyramidSet set = getPyramidSet();
        final XMLPyramid pyramid = (XMLPyramid) set.getPyramid(pyramidId);
        final XMLMosaic mosaic = pyramid.getMosaic(mosaicId);

        //-- check conformity between internal data and currentImage.
        //-- if no sm and cm automatical set from image (use for the first insertion)
        checkOrSetSampleColor(image);

        mosaic.createTile(col,row,image);
        if (!mosaic.cacheTileState && mosaic.tileExist != null) {
            save();
        }
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void writeTiles(final String pyramidId, final String mosaicId, final RenderedImage image, final Rectangle area,
                           final boolean onlyMissing, final ProgressMonitor monitor) throws DataStoreException {
        final XMLPyramidSet set = getPyramidSet();
        final XMLPyramid pyramid = (XMLPyramid) set.getPyramid(pyramidId);
        final XMLMosaic mosaic = pyramid.getMosaic(mosaicId);

        //-- check conformity between internal data and currentImage.
        //-- if no sm and cm automatical set from image (use for the first insertion)
        checkOrSetSampleColor(image);

        mosaic.writeTiles(image, area, onlyMissing, monitor);
        if (!mosaic.cacheTileState && mosaic.tileExist != null) {
            save();
        }
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void deleteTile(String pyramidId, String mosaicId, int col, int row) throws DataStoreException {
        throw new DataStoreException("Not supported yet.");
    }
}
