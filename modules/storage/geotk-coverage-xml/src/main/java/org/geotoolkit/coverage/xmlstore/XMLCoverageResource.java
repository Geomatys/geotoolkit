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

import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.internal.storage.AbstractGridResource;
import org.apache.sis.internal.storage.StoreResource;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.WritableGridCoverageResource;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.logging.Logging;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.coverage.SampleDimensionType;
import org.geotoolkit.coverage.SampleDimensionUtils;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.storage.multires.MultiResolutionModel;
import org.geotoolkit.storage.multires.MultiResolutionResource;
import org.geotoolkit.storage.multires.Pyramid;
import org.geotoolkit.storage.multires.Pyramids;
import org.geotoolkit.image.internal.ImageUtils;
import org.geotoolkit.image.internal.PlanarConfiguration;
import org.geotoolkit.image.internal.SampleType;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.storage.coverage.PyramidReader;
import org.geotoolkit.util.NamesExt;
import org.opengis.util.GenericName;

/**
 * XML implementation of {@link PyramidalCoverageReference}.
 *
 * @author Johann Sorel  (Geomatys)
 * @author Remi Marechal (Geomatys)
 * @module
 */
@XmlRootElement(name="CoverageReference")
public class XMLCoverageResource extends AbstractGridResource implements MultiResolutionResource, StoreResource, WritableGridCoverageResource {

    /**
     * Changes :
     * 1.1 - number format used to name folder was using system local,
     *      local is fixed to EN in 1.1.
     */
    static final String CURRENT_VERSION = "1.1";

    @XmlTransient
    private static MarshallerPool POOL;
    static {
        try {
            POOL = new MarshallerPool(JAXBContext.newInstance(XMLCoverageResource.class), null);
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

    private XMLCoverageStore store;
    private NamedIdentifier id;
    private Path mainfile;
    //caches
    private List<SampleDimension> cacheDimensions = null;

    public XMLCoverageResource() {
        super(null);
    }

    public XMLCoverageResource(XMLCoverageStore store, GenericName name, XMLPyramidSet set) {
        super(null);
        this.store = store;
        id = new NamedIdentifier(name);
        this.set = set;
        this.set.setRef(this);
    }

    @Override
    public DataStore getOriginator() {
        return store;
    }

    public List<XMLSampleDimension> getXMLSampleDimensions() {
        if (sampleDimensions == null) sampleDimensions = new ArrayList<>();
        return sampleDimensions;
    }

    void initialize(Path mainFile) throws DataStoreException {
        this.mainfile = mainFile;
        //calculate id based on file name
        id = new NamedIdentifier(null, IOUtilities.filenameWithoutExtension(mainFile));

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
        return id.getCode();
    }

    @Override
    public Optional<GenericName> getIdentifier() {
        return Optional.of(id);
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

    public XMLPyramidSet getPyramidSet() {
        return set;
    }

    @Override
    public Collection<Pyramid> getModels() throws DataStoreException {
        return set.getPyramids();
    }

    private static boolean isScaledColorSpace(final ColorSpace cs) {
        return (cs != null) && cs.getClass().getSimpleName().equals("ScaledColorSpace");        // TODO: hopefully temporary hack.
    }

    private void checkColorModel(){
        if (minColorSampleValue == null) {
            assert maxColorSampleValue == null;
            if (colorModel != null)
            assert !isScaledColorSpace(colorModel.getColorSpace()) : "with min and max NULL sample value color space should not be instance of ScaledColorSpace.";
        } else {
            assert maxColorSampleValue != null;
            assert isScaledColorSpace(colorModel.getColorSpace()) : "with NOT NULL min and max sample value color space must be instance of ScaledColorSpace.";
            assert Double.isFinite(minColorSampleValue) : "To write minColorSampleValue into XML Pyramid File, it should be finite. Found : "+minColorSampleValue;
            assert Double.isFinite(maxColorSampleValue) : "To write maxColorSampleValue into XML Pyramid File, it should be finite. Found : "+maxColorSampleValue;
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
     * Read the given file and return an XMLCoverageResource.
     *
     * @param file
     * @return
     * @throws JAXBException if an error occured while reading descriptor file.
     * @throws org.apache.sis.storage.DataStoreException if the file describe a pyramid, but it contains an invalid CRS.
     */
    @Deprecated
    public static XMLCoverageResource read(File file) throws JAXBException, DataStoreException {
        final Unmarshaller unmarshaller = POOL.acquireUnmarshaller();
        final XMLCoverageResource ref;
        ref = (XMLCoverageResource) unmarshaller.unmarshal(file);
        POOL.recycle(unmarshaller);
        ref.initialize(file.toPath());
        return ref;
    }

    /**
     * Read the given Path and return an XMLCoverageResource.
     *
     * @param file
     * @return
     * @throws JAXBException if an error occurred while reading descriptor file.
     * @throws org.apache.sis.storage.DataStoreException if the file describe a pyramid, but it contains an invalid CRS.
     */
    public static XMLCoverageResource read(Path file) throws JAXBException, DataStoreException, IOException {
        final Unmarshaller unmarshaller = POOL.acquireUnmarshaller();
        final XMLCoverageResource ref;
        try (InputStream is = Files.newInputStream(file)) {
            ref = (XMLCoverageResource) unmarshaller.unmarshal(is);
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
    public synchronized List<SampleDimension> getSampleDimensions() throws DataStoreException {
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
    public void setSampleDimensions(List<SampleDimension> dimensions) throws DataStoreException {
        if (dimensions == null || dimensions.isEmpty()) return;
        this.cacheDimensions = null; //clear cache

        if (sampleDimensions == null) sampleDimensions = new CopyOnWriteArrayList<>();
        sampleDimensions.clear();
        for (SampleDimension dimension : dimensions) {
            dimension = dimension.forConvertedValues(false);
            final SampleDimensionType sdt = SampleDimensionUtils.getSampleDimensionType(dimension);
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
    public ColorModel getColorModel() {
        if (colorModel == null) {
            if (checkAttributs()) {
                if (minColorSampleValue == null) {
                    assert maxColorSampleValue == null;
                    if (colorModel != null)
                    assert !isScaledColorSpace(colorModel.getColorSpace()) : "with min and max NULL sample value color space should not be instance of ScaledColorSpace.";
                } else {
                    assert maxColorSampleValue != null;
                    //assert isScaledColorSpace(colorModel.getColorSpace()) : "with NOT NULL min and max sample value color space must be instance of ScaledColorSpace.";
                    assert Double.isFinite(minColorSampleValue) : "To write minColorSampleValue into XML Pyramid File, it should be finite. Found : "+minColorSampleValue;
                    assert Double.isFinite(maxColorSampleValue) : "To write maxColorSampleValue into XML Pyramid File, it should be finite. Found : "+maxColorSampleValue;
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
    void checkOrSetSampleColor(final RenderedImage image) throws DataStoreException {
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
        if (isScaledColorSpace(imgCmCS)) {

            if (cm != null && (!isScaledColorSpace(cm.getColorSpace())))
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
            final PixelIterator pix = PixelIterator.create(image);
            double[] pixel = new double[pix.getNumBands()];
            while (pix.next()) {
                pix.getPixel(pixel);
                for (int b = 0; b < pixel.length; b++) {
                    //-- to avoid unexpected NAN values comportement don't use StrictMath class.
                    final double value = pixel[b];
                    if (value < minColorSampleValue) {
                        minColorSampleValue = value;
                    } else if (value > maxColorSampleValue) {
                        maxColorSampleValue = value;
                    }
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
    public ViewType getPackMode() {
        return ViewType.valueOf(packMode);
    }

    /**
     * {@inheritDoc }.
     */
    public void setPackMode(ViewType packMode) {
        this.packMode = packMode.name();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Creation,Edition methods ////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc }.
     */
    public boolean isWritable() throws CoverageStoreException {
        return true;
    }

    @Override
    public MultiResolutionModel createModel(MultiResolutionModel template) throws DataStoreException {
        if (template instanceof Pyramid) {
            final Pyramid base = (Pyramid) template;
            final XMLPyramidSet set = getPyramidSet();
            final Pyramid pyramid = set.createPyramid(getIdentifier().get().tip().toString(), base.getCoordinateReferenceSystem());
            save();
            Pyramids.copyStructure(base, pyramid);
            return pyramid;
        } else {
            throw new DataStoreException("Unsupported template : "+template);
        }
    }

    @Override
    public void removeModel(String identifier) throws DataStoreException {
        throw new DataStoreException("Not supported yet.");
    }

    public Runnable acquireTileWriter(final BlockingQueue<XMLTileWriter.XMLTileInfo> tilesToWrite) {
        return new XMLTileWriter(tilesToWrite, this);
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        return new PyramidReader<>(this).getGridGeometry();
    }

    @Override
    public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {
        return new PyramidReader<>(this).read(domain, range);
    }

    @Override
    public void write(GridCoverage coverage, Option... options) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

}
