/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2012, Open Source Geospatial Foundation (OSGeo)
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
 */
package org.geotoolkit.image.io.plugin;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.net.URL;
import java.net.URI;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import javax.imageio.IIOException;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;

import ucar.ma2.Array;
import ucar.ma2.Range;
import ucar.ma2.IndexIterator;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.dataset.CoordinateAxis;
import ucar.nc2.dataset.CoordinateSystem;
import ucar.nc2.dataset.CoordSysBuilder;
import ucar.nc2.dataset.CoordSysBuilderIF;
import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.dataset.VariableDS;
import ucar.nc2.dataset.Enhancements;
import ucar.nc2.ncml.Aggregation;
import ucar.nc2.util.CancelTask;
import ucar.nc2.Dimension;
import ucar.nc2.Variable;
import ucar.nc2.VariableIF;
import ucar.nc2.NetcdfFile;

import org.opengis.coverage.grid.GridEnvelope;

import org.geotoolkit.image.io.Protocol;
import org.geotoolkit.image.io.DimensionSlice;
import org.geotoolkit.image.io.FileImageReader;
import org.geotoolkit.image.io.DimensionIdentification;
import org.geotoolkit.image.io.IllegalImageDimensionException;
import org.geotoolkit.image.io.MultidimensionalImageStore;
import org.geotoolkit.image.io.AggregatedImageStore;
import org.geotoolkit.image.io.NamedImageStore;
import org.geotoolkit.image.io.SampleConverter;
import org.geotoolkit.image.io.SpatialImageReadParam;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.coverage.grid.GeneralGridEnvelope;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.internal.image.io.DimensionManager;
import org.geotoolkit.internal.image.io.IIOImageHelper;
import org.geotoolkit.internal.image.io.NetcdfVariable;
import org.geotoolkit.referencing.adapters.NetcdfAxis;
import org.geotoolkit.referencing.adapters.NetcdfCRS;
import org.geotoolkit.referencing.adapters.NetcdfCRSBuilder;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.lang.Workaround;
import org.geotoolkit.util.Version;
import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.collection.BackingStoreException;
import org.geotoolkit.util.collection.UnmodifiableArrayList;

import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.ISO_FORMAT_NAME;
import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.GEOTK_FORMAT_NAME;


/**
 * Base implementation for NetCDF image readers. Pixels are assumed organized according the COARDS
 * convention (a precursor of <A HREF="http://www.cfconventions.org/">CF Metadata conventions</A>).
 * For a 4-D dataset with horizontal, vertical and temporal ordinates, the dimension are typically
 * (<var>x</var>,<var>y</var>,<var>z</var>,<var>t</var>) where <var>x</var> index varies faster.
 *
 * {@note NetCDF data files actually declare dimensions in reverse order. For the above example,
 * the dimensions would be declared as (<var>t</var>,<var>z</var>,<var>y</var>,<var>x</var>). This
 * <code>NetcdfImageReader</code> plugin reverse the order of dimensions read in the NetCDF file,
 * in order to get an ordering consistent with the ordering used by other plugins.}
 *
 * The image is created from the (<var>x</var>,<var>y</var>) dimensions in the above example.
 * Additional dimensions (if any) are ignored by default: only the slice at index 0 is read,
 * which is <var>z</var><sub>0</sub> and <var>t</var><sub>0</sub> in the example above. See
 * below for selecting slices in other dimensions.
 *
 * {@section Specifying the variable to read}
 * Each variable having at least two dimensions (except the variables used for Coordinate System
 * axes) is an image. The variables to read can be specified using the methods defined in the
 * {@link NamedImageStore} interface, as in the example below:
 *
 * {@preformat java
 *     imageReader.setImageNames("temperature", "salinity");
 *     BufferedImage temperature = imageReader.read(0);
 *     BufferedImage salinity    = imageReader.read(1);
 * }
 *
 * Alternatively, the variables can be assigned to bands instead than images. This is useful
 * when two related variables - for example the East-West (<var>U</var>) and North-South
 * (<var>V</var>) components of wind speed - shall be stored in the same image:
 *
 * {@preformat java
 *     imageReader.setBandNames(0, "WindSpeed-U", "WindSpeed-V");
 *     BufferedImage windSpeed = imageReader.read(0);
 *     // windSpeed is now an image with two bands.
 * }
 *
 * {@section Specifying the slice to read in extra dimensions}
 * For any dimension greater than 2, the region to read can be specified in two different ways:
 * <p>
 * <ul>
 *   <li>The slice to read can be specified by {@link DimensionSlice} objects, which are
 *       associated to the {@link SpatialImageReadParam} object controlling the reading
 *       process. This approach is similar to the WCS 2.0 specification.</li>
 *   <li>The slice to read can be specified as bands or as image index, using the methods
 *       defined in the {@link MultidimensionalImageStore} interface. This approach allows
 *       compatibility with library working only with the Java Image I/O API.</li>
 * </ul>
 *
 * {@section Connection to DODS servers}
 * This image reader accepts {@link String}, {@link File}, {@link URL} and {@link URI} inputs.
 * The input can use the DODS protocol (as in "{@code dods://opendap.aviso.oceanobs.com/}"),
 * in order to connect to the specified DODS remote server.
 *
 * {@section Support of related formats}
 * This implementation uses the <a href="http://www.unidata.ucar.edu/software/netcdf-java/">UCAR
 * NetCDF library</a> for reading data. Consequently, it can be used for reading other formats
 * supported by that library. For a list of supported formats, see
 * <a href="http://www.unidata.ucar.edu/software/netcdf-java/formats/FileTypes.html">file types
 * and remote access protocols</a> on the NetCDF web site.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Antoine Hnawia (IRD)
 * @author Johann Sorel (Geomatys)
 * @version 3.20
 *
 * @see org.geotoolkit.referencing.adapters.NetcdfCRS
 *
 * @since 3.08 (derived from 2.4)
 * @module
 */
public class NetcdfImageReader extends FileImageReader implements
        MultidimensionalImageStore, NamedImageStore, AggregatedImageStore, CancelTask
{
    /**
     * The enhancements to enable when opening a NetCDF data set.
     */
    private static final Set<NetcdfDataset.Enhance> ENHANCEMENTS;
    static {
        final Set<NetcdfDataset.Enhance> modes = EnumSet.noneOf(NetcdfDataset.Enhance.class);
        modes.add(NetcdfDataset.Enhance.ScaleMissingDefer);
        modes.add(NetcdfDataset.Enhance.CoordSystems);
        modes.add(NetcdfDataset.Enhance.ConvertEnums);
        ENHANCEMENTS = Collections.unmodifiableSet(modes);
    }

    /**
     * The API to use for selecting dimensions above the two standard (column, row) dimensions.
     */
    private final DimensionManager dimensionManager;

    /**
     * The NetCDF dataset, or {@code null} if not yet open. The NetCDF file is open by
     * {@link #ensureFileOpen()} when first needed.
     */
    private NetcdfDataset dataset;

    /**
     * The builder for {@link NetcdfCRS} objects, created when first needed.
     * <p>
     * This field is not used directly by this class.
     * But it is used by {@link NetcdfMetadata#setCoordinateSystem}.
     */
    NetcdfCRSBuilder crsBuilder;

    /**
     * The name of the {@linkplain Variable variables} found in the NetCDF file.
     * The first name is assigned to image index 0, the second name to image index 1,
     * <i>etc</i>. This list shall be immutable.
     * <p>
     * The user can override this list with his own list of variable to read,
     * by calls to the {@link #setImageNames(String[])} method.
     */
    private List<String> variableNames;

    /**
     * The image index of the current {@linkplain #variable variable}.
     */
    private int variableIndex;

    /**
     * The name of the current {@linkplain #variable variable}. This is the name specified
     * to {@link #setImageNames(String[])} or {@link #setBandNames(int, String[])}, which
     * may be slightly different than {@link Variable#getName()}.
     */
    private String variableName;

    /**
     * The data from the NetCDF file for a given image index. The value for this field is set by
     * {@link #prepareVariable} when first needed, and may be updated when the argument given to
     * any method expecting a {@code imageIndex} parameter has changed.
     * <p>
     * This field is typically (but not necessarily) an instance of {@link VariableDS}.
     */
    protected Variable variable;

    /**
     * The last error from the NetCDF library.
     */
    private String lastError;

    /**
     * {@code true} if {@link CoordSysBuilderIF#buildCoordinateSystems} has been invoked
     * for the current dataset.
     */
    private boolean metadataLoaded;

    /**
     * The CRS and <cite>grid to CRS</cite> transform provided by GDAL in the {@code spatial_ref_sys}
     * and {@code GeoTransform} variable attributes. Those attributes are not conform to the CF
     * conventions and don't seem to be recognized by the UCAR NetCDF library version 4.2.26.
     *
     * @see #getGridMapping()
     */
    @Workaround(library="NetCDF", version="4.2.26")
    private transient Map<String,GDALGridMapping> gridMapping;

    /**
     * Constructs a new NetCDF reader.
     *
     * @param spi The service provider.
     */
    public NetcdfImageReader(final Spi spi) {
        super(spi != null ? spi : new Spi());
        dimensionManager = new DimensionManager(this);
    }

    /**
     * Returns the dimension assigned to the given API. By default, the Image I/O API is used
     * as below:
     * <p>
     * <ul>
     *   <li>The {@linkplain ImageReadParam#setSourceRegion source region} specifies the region
     *       to read in the 2 first dimensions (typically <var>x</var> and <var>y</var>).</li>
     *   <li>The {@linkplain ImageReadParam#setSourceBands(int[]) source bands} are not used
     *       and should be set to 0.</li>
     *   <li>The {@code imageIndex} specifies the variable to read, where the variable name
     *       is determined by <code>{@linkplain #getImageNames()}.get(imageIndex)</code>.</li>
     * </ul>
     * <p>
     * The above-cited default can be changed by calls to this method. For example, the following
     * method call allows usage of the otherwise unused bands API for selecting slices in the third
     * dimension:
     *
     * {@preformat java
     *     reader.setImageNames("temperature");
     *     reader.getDimensionForAPI(API.BANDS).addDimensionId(2); // Zero-based index
     * }
     *
     * The following method call reads the same image than the above example,
     * but uses the image index for selecting slices in the third dimension:
     *
     * {@preformat java
     *     reader.setBandNames(0, "temperature");
     *     reader.getDimensionForAPI(API.IMAGES).addDimensionId(3);
     * }
     *
     * This NetCDF reader allows usage of
     * {@link org.geotoolkit.image.io.DimensionSlice.API#BANDS BANDS} and
     * {@link org.geotoolkit.image.io.DimensionSlice.API#IMAGES IMAGES} API.
     * The {@code COLUMNS} and {@code ROWS} API can not be assigned to new
     * dimensions in current implementation.
     *
     * @since 3.15
     */
    @Override
    public DimensionIdentification getDimensionForAPI(final DimensionSlice.API api) {
        return dimensionManager.getOrCreate(api);
    }

    /**
     * {@inheritDoc}
     *
     * @since 3.15
     */
    @Override
    public DimensionSlice.API getAPIForDimension(Object... identifiers) {
        return dimensionManager.getAPI(identifiers);
    }

    /**
     * {@inheritDoc}
     *
     * @since 3.15
     */
    @Override
    public Set<DimensionSlice.API> getAPIForDimensions() {
        return dimensionManager.getAPIs();
    }

    /**
     * Returns the URIs to the aggregated files, or {@code null} if none. This information applies
     * mostly to NcML files, which are XML files listing many NetCDF files to be aggregated as if
     * they were a single dataset. The method returns the individual files that compose such
     * aggregation.
     *
     * @param  imageIndex The index of the variable for which to get the aggregated files.
     * @return The individual files which are aggregated, or {@code null} if none.
     * @throws IOException If an error occurred while building the list of files.
     *
     * @see NetcdfDataset#getAggregation()
     *
     * @since 3.16
     */
    @Override
    public List<URI> getAggregatedFiles(int imageIndex) throws IOException {
        clearAbortRequest();
        ensureFileOpen();
        imageIndex = dimensionManager.replaceImageIndex(imageIndex);
        String name = dimensionManager.getVariableName(imageIndex);
        if (name == null) {
            name = getVariableNames().get(imageIndex);
        }
        return getAggregatedFiles(dataset, name, null);
    }

    /**
     * Adds the aggregated files to the given list. This method invokes itself recursively
     * if an aggregation is a outer aggregation containing inner elements.
     *
     * @param  dataset  The dataset from which to get the aggregation.
     * @param  variable The name of the variable for which to get aggregated elements.
     * @param  addTo    The list in which to add the URI, or {@code null} if not yet created.
     * @return The {@code addTo} list, or a new list if {@code addTo} was null and new elements
     *         were found.
     * @throws IOException If an error occurred while building the list of files.
     */
    private List<URI> getAggregatedFiles(NetcdfDataset dataset, final String variable,
            List<URI> addTo) throws IOException
    {
        final Aggregation aggregation = dataset.getAggregation();
        if (aggregation != null) {
            final List<Aggregation.Dataset> components = aggregation.getDatasets();
            if (components != null) {
                if (addTo == null) {
                    addTo = new ArrayList<>(components.size());
                }
                for (final Aggregation.Dataset component : components) {
                    if (abortRequested()) {
                        throw new IIOException(errors().getString(Errors.Keys.CANCELED_OPERATION));
                    }
                    if (component != null) {
                        /*
                         * We will process the aggregated file only if it contains the variable
                         * we are looking for.
                         */
                        final NetcdfFile componentFile = component.acquireFile(this);
                        if (componentFile.findVariable(variable) != null) {
                            final String location = component.getLocation();
                            if (location == null) {
                                /*
                                 * If the component does not contain a link to a file, it may be an
                                 * outer aggregation which contain inner aggregations.  Explore the
                                 * content recursively.
                                 */
                                if (componentFile instanceof NetcdfDataset) {
                                    addTo = getAggregatedFiles((NetcdfDataset) componentFile, variable, addTo);
                                }
                            } else {
                                /*
                                 * Get the URI, wrapping exception in a MalformedURLException since
                                 * in order to get a subclass of IOException. We give the location
                                 * as the message a let the more detailled explanation in the cause.
                                 * We do that because MalformedURLException does not have an getInput()
                                 * method (at the opposite of URISyntaxException).
                                 */
                                final URI url;
                                try {
                                    url = new URI(location);
                                } catch (URISyntaxException c) {
                                    MalformedURLException e = new MalformedURLException(location);
                                    e.initCause(c);
                                    throw e;
                                }
                                addTo.add(url);
                            }
                        }
                        componentFile.close();
                    }
                }
            }
        }
        return addTo;
    }

    /**
     * Returns the names of the variables to be read. The first name is assigned to the image
     * at index 0, the second name to the image at index 1, <i>etc</i>. In other words a call
     * to <code>{@linkplain #read(int) read}(imageIndex)</code> will read the variable named
     * {@code variables.get(imageIndex)} where {@code variables} is the list returned by this
     * method.
     * <p>
     * The sequence of variables to be read can be changed by a call to
     * {@link #setImageNames(String[])}.
     *
     * @return The names of the variables to be read.
     * @throws IOException if the NetCDF file can not be read.
     *
     * @see NetcdfDataset#getVariables()
     */
    @Override
    public List<String> getImageNames() throws IOException {
        List<String> names = dimensionManager.getImageNames();
        if (names == null) {
            ensureFileOpen();
            names = getVariableNames();
        }
        return names;
    }

    /**
     * Sets the name of the {@linkplain Variable variables} to be read in a NetCDF file.
     * The first name is assigned to image index 0, the second name to image index 1,
     * <i>etc</i>.
     * <p>
     * Special cases:
     * <ul>
     *   <li>If {@code variableNames} is set to {@code null}, then the variables will be inferred
     *       from the content of the NetCDF file. This is the default behavior.</li>
     *   <li>If the {@link org.geotoolkit.image.io.DimensionSlice.API#IMAGES IMAGES} API has been
     *       assigned to a dimension, then at most one variable can be specified.</li>
     * </ul>
     *
     * @param names The set of variables to be assigned to image index,
     *        or {@code null} for all variables declared in the NetCDF file.
     * @throws IOException if the NetCDF file can not be read.
     */
    @Override
    public void setImageNames(final String... names) throws IOException {
        dimensionManager.setImageNames(names);
        variable = null; // Will force a reload.
    }

    /**
     * Returns the number of images available from the current input source. By default, this
     * method returns the number of {@linkplain #getImageNames() variables} since each variable
     * is considered as an image. However if the
     * {@link org.geotoolkit.image.io.DimensionSlice.API#IMAGES IMAGES} API has been assigned
     * to a dimension, then this method returns the number of slices in that dimension.
     *
     * @throws IllegalStateException if the input source has not been set.
     * @throws IOException if an error occurs reading the information from the input source.
     */
    @Override
    public int getNumImages(final boolean allowSearch) throws IllegalStateException, IOException {
        if (dimensionManager.usesImageAPI()) {
            /*
             * If the user uses the image index for reading slices in the hyper-cube,
             * returns the number of slices in the selected dimension.
             */
            if (!allowSearch) {
                // It is necessary to NOT invoke 'prepareVariable(0)' in this case in order
                // to avoid an infinite loop when 'checkImageIndex(int)' is invoked.
                return -1;
            }
            // Index 0 below is arbitrary - just the most likely one to be wanted.
            prepareVariable(DimensionManager.DEFAULT_IMAGE_INDEX);
            final int imageDimension = findDimensionIndex(DimensionSlice.API.IMAGES, variable.getRank());
            if (imageDimension >= 0) {
                return variable.getDimension(imageDimension).getLength();
            }
        }
        return getImageNames().size();
    }

    /**
     * Returns the names of the bands for the given image, or {@code null} if none.
     * By default, this method returns {@code null} for every image index. Non-null
     * values can be specified with calls to the {@link #setBandNames(int, String[])}
     * method.
     *
     * @param  imageIndex Index of the image for which to get the band names.
     * @return The variable names of the bands for the given image, or {@code null}
     *         if the bands for the given image are unnamed.
     * @throws IOException if the NetCDF file can not be read.
     *
     * @since 3.11
     */
    @Override
    public List<String> getBandNames(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        return dimensionManager.getBandNames(imageIndex);
    }

    /**
     * Sets the names of the bands for the given image, or {@code null} for removing any naming.
     * This method is useful for merging different variables as different bands in the image to
     * be read, typically because each band is a vector component. See the {@link NamedImageStore}
     * javadoc for an example.
     *
     * @param  imageIndex Index of the image for which to set the band names.
     * @param  names The variable names of the bands for the given image,
     *         or {@code null} for removing any naming.
     * @throws IOException if the NetCDF file can not be read.
     *
     * @since 3.11
     */
    @Override
    public void setBandNames(final int imageIndex, String... names) throws IOException {
        checkImageIndex(imageIndex);
        dimensionManager.setBandNames(imageIndex, names);
    }

    /**
     * Returns the number of bands available for the image identified by the given index. The
     * default implementation returns the value of the first of the following conditions which
     * is hold:
     * <p>
     * <ol>
     *   <li>If the bands at the give image index {@linkplain #setBandNames have been
     *       assigned to variable names}, returns the number of assigned variables.</li>
     *   <li>Otherwise if the bands API has been {@linkplain MultidimensionalImageStore assigned to
     *       a dimension}, return the {@linkplain VariableIF#getDimension(int) dimension} length of
     *       the {@linkplain #variable} identified by the given image index.</li>
     *   <li>Otherwise the {@linkplain FileImageReader#getNumBands(int) default number of bands}
     *       is 1.</li>
     * </ol>
     *
     * @param  imageIndex The image index.
     * @return The number of bands available.
     * @throws IOException if an error occurs reading the information from the input source.
     */
    @Override
    public int getNumBands(final int imageIndex) throws IOException {
        final int internalIndex = dimensionManager.replaceImageIndex(imageIndex);
        final List<String> bandNames = dimensionManager.getBandNames(internalIndex);
        if (bandNames != null) {
            return bandNames.size();
        }
        prepareVariable(imageIndex);
        final int bandDimension = findDimensionIndex(DimensionSlice.API.BANDS, variable.getRank());
        if (bandDimension >= 0) {
            return variable.getDimension(bandDimension).getLength();
        }
        return super.getNumBands(imageIndex);
    }

    /**
     * Returns the image width.
     *
     * @throws IOException If an error occurred while reading the NetCDF file.
     *
     * @see Variable#getDimension(int)
     */
    @Override
    public int getWidth(final int imageIndex) throws IOException {
        prepareVariable(imageIndex);
        return variable.getDimension(variable.getRank() - (X_DIMENSION + 1)).getLength();
    }

    /**
     * Returns the image height.
     *
     * @throws IOException If an error occurred while reading the NetCDF file.
     *
     * @see Variable#getDimension(int)
     */
    @Override
    public int getHeight(final int imageIndex) throws IOException {
        prepareVariable(imageIndex);
        return variable.getDimension(variable.getRank() - (Y_DIMENSION + 1)).getLength();
    }

    /**
     * Returns the grid envelope in the image identified by the given index.
     *
     * @see Variable#getDimension(int)
     *
     * @since 3.19
     */
    @Override
    public GridEnvelope getGridEnvelope(final int imageIndex) throws IOException {
        prepareVariable(imageIndex);
        final int rank = variable.getRank();
        final int[] lower = new int[rank];
        final int[] upper = new int[rank];
        for (int i=0; i<rank;) {
            upper[i] = variable.getDimension(rank - ++i).getLength();
        }
        return new GeneralGridEnvelope(lower, upper, false);
    }

    /**
     * Returns the number of dimensions in the image identified by the given index.
     * In the case of NetCDF files, this is the {@linkplain VariableIF#getRank() rank}
     * of the {@linkplain #variable} associated to the given image index.
     *
     * @param  imageIndex The image index.
     * @return The number of dimension for the image at the given index.
     * @throws IOException if an error occurs reading the information from the input source.
     *
     * @see Variable#getRank()
     */
    @Override
    public int getDimension(final int imageIndex) throws IOException {
        prepareVariable(imageIndex);
        return variable.getRank();
    }

    /**
     * Returns the index of the dimension which has been assigned to the given API, or -1 if none.
     * The {@link #prepareVariable} method shall be invoked prior this method (this is not verified).
     * <p>
     * Note that this method returns the index in the NetCDF {@linkplain #variable}, which is the
     * reverse order of axis order as viewed from this {@code ImageReader}.
     *
     * @param  api  The API for which to get the dimension index in NetCDF variable.
     * @param  rank The number of dimensions (the rank) in the {@linkplain #variable}.
     * @return The dimension index in the NetCDF variable, or {@code -1} if none.
     * @throws IOException If an I/O error occurred.
     */
    private int findDimensionIndex(final DimensionSlice.API api, final int rank) throws IOException {
        final DimensionIdentification dimension = dimensionManager.get(api);
        if (dimension != null) {
            /*
             * The code below uses a custom Iterable in order to invoke the getAxes(...)
             * method (which may force the loading of metadata) only if really needed.
             */
            int n;
            try {
                n = dimension.findDimensionIndex(new Iterable<Map.Entry<?,Integer>>() {
                    @Override public Iterator<Map.Entry<?,Integer>> iterator() {
                        final List<CoordinateAxis> axes;
                        try {
                            axes = getAxes(rank);
                        } catch (IOException e) {
                            // Will be caught in the enclosing method.
                            throw new org.geotoolkit.util.collection.BackingStoreException(e);
                        }
                        return (axes != null) ? new NetcdfAxesIterator(axes) :
                                Collections.<Map.Entry<?,Integer>>emptySet().iterator();
                    }
                });
            } catch (BackingStoreException e) {
                throw e.unwrapOrRethrow(IOException.class);
            }
            /*
             * If we found the dimension, convert the index from this ImageReader axis order
             * (typically (x,y,z,t)) to the NetCDF axis order (typically (t,z,y,x)). In this
             * process, we also ensure that the index is not one of the reserved ones.
             */
            if (n >= 0) {
                switch (n) {
                    case X_DIMENSION:
                    case Y_DIMENSION: {
                        throw new IllegalImageDimensionException(errors().getString(Errors.Keys.ILLEGAL_PARAMETER_VALUE_$2,
                                "DimensionSlice(" + api.name() + ')', n));
                    }
                }
                return rank - (n+1);
            }
        }
        return -1;
    }

    /**
     * Returns the indices along all dimensions of the slice to read. The default value for
     * indices that were not explicitly specified is 0. This method returns elements in the
     * NetCDF order (reverse of the usual order).
     * <p>
     * The {@link #prepareVariable} method shall be invoked prior this method (this is not verified).
     *
     * @param  param The parameters supplied by the user to the {@code read} method.
     * @param  rank The number of dimensions (the rank) in the {@linkplain #variable}.
     * @return The indices in NetCDF order (reverse of usual order), as values from 0
     *         inclusive to {@link Dimension#getLength()} exclusive.
     * @throws IOException If an error occurred while reading the NetCDF file.
     */
    @SuppressWarnings("fallthrough")
    private int[] getSourceIndices(final ImageReadParam param, final int rank) throws IOException {
        final int[] indices = new int[rank];
        if (param instanceof SpatialImageReadParam) {
            final SpatialImageReadParam p = (SpatialImageReadParam) param;
            if (!p.getDimensionSlices().isEmpty()) {
                final List<CoordinateAxis> axes = getAxes(rank);
                final Object[] properties = new Object[axes != null ? 3 : 1];
                for (int i=0; i<rank; i++) {
                    final CoordinateAxis axis = (axes != null) ? axes.get(i) : null;
                    switch (properties.length) {
                        default: properties[2] = NetcdfAxis.getDirection(axis);
                        case 2:  properties[1] = axis.getFullName();
                        case 1:  properties[0] = (rank - 1) - i;
                        case 0:  break;
                    }
                    indices[i] = p.getSliceIndex(properties);
                }
            }
        }
        return indices;
    }

    /**
     * Returns the axes of the first coordinate system having at least the given number of
     * dimensions. The {@link #prepareVariable} method shall be invoked prior this method
     * (this is not verified).
     * <p>
     * This method is not public because it duplicates (in a different form) the informations
     * already provided in image metadata. We use it when we want only this specific information
     * without the rest of metadata. In many cases this method will not be invoked at all, thus
     * avoiding the need to load metadata.
     * <p>
     * This method returns axes in NetCDF order (reverse of the usual order).
     *
     * @param  rank The number of dimensions (the rank) in the {@linkplain #variable}.
     * @return The axes in NetCDF order, or {@code null} if no set of axis is applicable.
     * @throws IOException If an error occurred while reading the NetCDF file.
     */
    private List<CoordinateAxis> getAxes(final int rank) throws IOException {
        if (variable instanceof Enhancements) {
            ensureMetadataLoaded();
            final List<CoordinateSystem> sys = ((Enhancements) variable).getCoordinateSystems();
            if (sys != null) {
                final int count = sys.size();
                for (int i=0; i<count; i++) {
                    final CoordinateSystem cs = sys.get(i);
                    final List<CoordinateAxis> axes = cs.getCoordinateAxes();
                    if (axes != null && axes.size() >= rank) {
                        return axes;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns the map containing the CRS and <cite>grid to CRS</cite> transform provided by GDAL.
     * This method is for use by {@link NetcdfMetadata} only.
     */
    final Map<String,GDALGridMapping> getGridMapping() {
        if (gridMapping == null) {
            gridMapping = new HashMap<>();
        }
        return gridMapping;
    }

    /**
     * Ensures that metadata are loaded.
     */
    private void ensureMetadataLoaded() throws IOException {
        if (!metadataLoaded) {
            CoordSysBuilder.factory(dataset, this).buildCoordinateSystems(dataset);
            metadataLoaded = true;
        }
    }

    /**
     * Creates a new stream or image metadata. This method is invoked automatically when first
     * needed.
     *
     * @see CoordSysBuilder#factory(NetcdfDataset, CancelTask)
     */
    @Override
    protected SpatialMetadata createMetadata(final int imageIndex) throws IOException {
        ensureFileOpen();
        ensureMetadataLoaded();
        /*
         * For stream metadata, returns a tree built from the global attributes only.
         */
        if (imageIndex < 0) {
            return new NetcdfMetadata(this, dataset);
        }
        /*
         * If the image index is used for navigating through a third dimension, then the
         * image metadata are the same for all image index. Returns the common metadata.
         */
        final int internalIndex = dimensionManager.replaceImageIndex(imageIndex);
        if (internalIndex != imageIndex) {
            return super.getImageMetadata(internalIndex);
        }
        /*
         * For image metadata, returns a tree built from the variable attributes, where
         * the variable is inferred from the image index. In the special case where the
         * user assigned many variable to the same image index (where each variable is
         * handled as a band), we need to build the variables list from the band names.
         */
        final Variable[] variables;
        final List<String> bandNames = dimensionManager.getBandNames(internalIndex);
        if (bandNames != null) {
            variables = new Variable[bandNames.size()];
            for (int i=0; i<variables.length; i++) {
                variables[i] = findVariable(bandNames.get(i));
            }
        } else {
            prepareVariable(imageIndex);
            variables = new Variable[] {variable};
        }
        final NetcdfMetadata metadata = new NetcdfMetadata(this, dataset, variables);
        metadata.workaroundNonStandard(dataset);
        return metadata;
    }

    /**
     * Returns the data type which most closely represents the "raw" internal data of the image.
     * In the case of NetCDF files, The raw type is determined from the value returned by the
     * {@link VariableIF#getDataType()} method on the {@linkplain #variable} identified by the
     * given index. The NetCDF {@link ucar.ma2.DataType} is then mapped to one of the
     * {@link DataBuffer} constants.
     *
     * @param  imageIndex The index of the image to be queried.
     * @return The data type, or {@link DataBuffer#TYPE_UNDEFINED} if unknown.
     * @throws IOException If an error occurred while reading the NetCDF file.
     */
    @Override
    protected int getRawDataType(final int imageIndex) throws IOException {
        prepareVariable(imageIndex);
        return NetcdfVariable.getRawDataType(variable);
    }

    /**
     * Ensures that the NetCDF file is open, but does not load any variable yet.
     * The {@linkplain #variable} will be read by {@link #prepareVariable} only.
     */
    private void ensureFileOpen() throws IOException {
        if (dataset == null) {
            /*
             * Clears the 'abort' flag here (instead of in 'read' method only) because
             * we pass this ImageReader instance to the NetCDF DataSet as a CancelTask.
             */
            lastError = null;
            clearAbortRequest();
            final String inputURL;
            boolean useCache = true;
            final Object input = this.input;
            if (input instanceof NetcdfFile) {
                if (input instanceof NetcdfDataset) {
                    dataset = (NetcdfDataset) input;
                } else {
                    dataset = new NetcdfDataset((NetcdfFile) input);
                }
                return;
            }
            switch (Protocol.getProtocol(input)) {
                case DODS: {
                    inputURL = input.toString();
                    final int s = inputURL.indexOf('?');
                    if (s >= 0) {
                        variableNames = UnmodifiableArrayList.wrap(inputURL.substring(s + 1));
                    }
                    break;
                }
                default: {
                    /*
                     * The NetCDF library accepts URL, so don't create a temporary file for them.
                     * Just convert the URL to a String and use that directly.
                     */
                    if (input instanceof String || input instanceof URL || input instanceof URI || input instanceof File) {
                        inputURL = input.toString();
                    } else {
                        /*
                         * For other types (especially ImageInputStream), we need
                         * to copy the content to a temporary file before to open it.
                         */
                        inputURL = getInputFile().getPath();
                        useCache = false;
                    }
                    break;
                }
            }
            if (useCache) {
                dataset = NetcdfDataset.acquireDataset(null, inputURL, ENHANCEMENTS, 0, this, null);
            } else {
                dataset = NetcdfDataset.openDataset(inputURL, ENHANCEMENTS, 0, this, null);
            }
            if (dataset == null) {
                throw new FileNotFoundException(errors().getString(
                        Errors.Keys.FILE_DOES_NOT_EXIST_$1, inputURL));
            }
        }
    }

    /**
     * Returns the name of all variables in the current NetCDF file. The {@link #ensureFileOpen()}
     * method must have been invoked before this method (this is not verified).
     */
    private List<String> getVariableNames() throws IOException {
        if (variableNames == null) {
            /*
             * Gets a list of every variables found in the NetcdfDataset and copies the names
             * in a filtered list which exclude every variable that are dimension of an other
             * variable. For example "longitude" may be a variable found in the NetcdfDataset,
             * but is declared only because it is needed as a dimension for the "temperature"
             * variable. The "longitude" variable is usually not of direct interest to the user
             * (the interesting variable is "temperature"), so we exclude it.
             */
            final List<Variable> variables = dataset.getVariables();
            final String[] filtered = new String[variables.size()];
            int count = 0;
            for (int minLength=2; minLength>=1; minLength--) {
                for (final VariableIF candidate : variables) {
                    if (NetcdfVariable.isCoverage(candidate, variables, minLength)) {
                        /*
                         * - Images require at least 2 dimensions. They may have more dimensions,
                         *   in which case a slice will be taken later.
                         *
                         * - Excludes axis. They are often already excluded by the first condition
                         *   because axis are usually 1-dimensional, but some are 2-dimensional,
                         *   e.g. a localization grid.
                         *
                         * - Excludes characters, strings and structures, which can not be easily
                         *   mapped to an image type. In addition, 2-dimensional character arrays
                         *   are often used for annotations and we don't want to confuse them
                         *   with images.
                         */
                        filtered[count++] = candidate.getShortName();
                    }
                }
                if (count != 0) break;
                // If we didn't found any variable with a length of at least 2 along
                // 2 dimensions, try again but be less strict (require a length of 1).
            }
            variableNames = UnmodifiableArrayList.wrap(ArraysExt.resize(filtered, count));
        }
        return variableNames;
    }

    /**
     * Ensures that data are loaded in the NetCDF {@linkplain #variable}. If data are already
     * loaded, then this method does nothing.
     * <p>
     * This method is invoked automatically before any operation requiring the NetCDF
     * variable, including (but not limited to):
     * <ul>
     *   <li>{@link #getWidth(int)}</li>
     *   <li>{@link #getHeight(int)}</li>
     *   <li>{@link #createMetadata(int)}</li>
     *   <li>{@link #getRawDataType(int)}</li>
     *   <li>{@link #read(int,ImageReadParam)}</li>
     * </ul>
     *
     * @param  imageIndex The image index.
     * @return {@code true} if the {@linkplain #variable} changed as a result of this call,
     *         or {@code false} if the current value is already appropriate.
     * @throws IndexOutOfBoundsException if the specified index is outside the expected range.
     * @throws IllegalStateException If {@link #input} is not set.
     * @throws IOException If an error occurred while reading the NetCDF file.
     */
    protected boolean prepareVariable(final int imageIndex) throws IOException {
        final int internalIndex = dimensionManager.replaceImageIndex(imageIndex);
        if (variable == null || variableIndex != internalIndex) {
            checkImageIndex(imageIndex);
            ensureFileOpen();
            /*
             * Get the name of the variable to search for. This is usually the name at the
             * given index in the 'selectedName' list. However a special case is performed
             * if the user invoked the setBandNames(...) method (i.e. specified explicitly
             * which variable to assign to each band). In such case we will load the first
             * variable (using the first variable is an arbitrary choice, but work well if
             * the bands are going to be read in sequential order).
             */
            String name = dimensionManager.getVariableName(internalIndex);
            if (name == null) {
                name = getVariableNames().get(internalIndex);
            }
            /*
             * Now get the NetCDF variable and initialize this instance fields.
             */
            final Variable candidate = findVariable(name);
            final int rank = candidate.getRank();
            if (rank < 2) {
                throw new IIOException(errors().getString(Errors.Keys.NOT_TWO_DIMENSIONAL_$1, rank));
            }
            variable      = candidate;
            variableName  = name;
            variableIndex = internalIndex;
            return true;
        }
        return false;
    }

    /**
     * Returns the variable of the given name. This method is similar to
     * {@link NetcdfDataset#findVariable(String)} except that the search
     * is case-insensitive and an exception is thrown if no variable has
     * been found for the given name.
     * <p>
     * Subclasses can override this method if they want this {@code NetcdfImageReader}
     * to use a different variable for the given name.
     *
     * @param  name The name of the variable to search.
     * @return The variable for the given name.
     * @throws IOException If an error occurred while reading the NetCDF file.
     *
     * @see NetcdfDataset#findVariable(String)
     */
    protected Variable findVariable(final String name) throws IOException {
        ensureFileOpen();
        /*
         * First tries a case-sensitive search. Case matter since the same letter in different
         * case may represent different variables. For example "t" and "T" are typically "time"
         * and "temperature" respectively.
         */
        Variable candidate = dataset.findVariable(name);
        if (candidate != null) {
            return candidate;
        }
        /*
         * We tried a case-sensitive search without success. Now tries a case-insensitive search
         * before to report a failure.
         */
        @SuppressWarnings("unchecked")
        final List<Variable> variables = dataset.getVariables();
        if (variables != null) {
            for (final Variable variable : variables) {
                if (variable!=null && name.equalsIgnoreCase(variable.getFullName())) {
                    return variable;
                }
            }
        }
        throw new IIOException(errors().getString(
                Errors.Keys.VARIABLE_NOT_FOUND_IN_FILE_$2, name, dataset.getLocation()));
    }

    /**
     * Creates an image from the specified parameters.
     *
     * @throws IOException If an error occurred while reading the NetCDF file.
     */
    @Override
    public BufferedImage read(final int imageIndex, final ImageReadParam param) throws IOException {
        clearAbortRequest();
        prepareVariable(imageIndex);
        /*
         * Fetches the parameters that are not already processed by utility
         * methods like 'getDestination' or 'computeRegions' (invoked below).
         */
        final int strideX, strideY;
        final int[] srcBands, dstBands;
        if (param != null) {
            strideX  = param.getSourceXSubsampling();
            strideY  = param.getSourceYSubsampling();
            srcBands = param.getSourceBands();
            dstBands = param.getDestinationBands();
        } else {
            strideX  = 1;
            strideY  = 1;
            srcBands = null;
            dstBands = null;
        }
        final int rank = variable.getRank();
        final int imageDimension = findDimensionIndex(DimensionSlice.API.IMAGES, rank);
        final int bandDimension;
        /*
         * Gets the number of source bands, in preference order (must be consistent with the
         * getNumBands(int) method): from the explicit list of band names, from the variable
         * dimension at the index identified by DimensionIdentification, or 1. Then check that
         * the number of source and target bands are consistent.
         */
        final int numSrcBands;
        final List<String> bandNames = dimensionManager.getBandNames(variableIndex);
        if (bandNames != null) {
            numSrcBands = bandNames.size();
            bandDimension = -1;
        } else {
            bandDimension = findDimensionIndex(DimensionSlice.API.BANDS, rank);
            numSrcBands = (bandDimension >= 0) ? variable.getDimension(bandDimension).getLength() : 1;
        }
        final int numDstBands = (dstBands != null) ? dstBands.length :
                                (srcBands != null) ? srcBands.length : numSrcBands;
        checkReadParamBandSettings(param, numSrcBands, numDstBands);
        /*
         * Gets the destination image of appropriate size.
         */
        final int width  = variable.getDimension(rank - (X_DIMENSION + 1)).getLength();
        final int height = variable.getDimension(rank - (Y_DIMENSION + 1)).getLength();
        final SampleConverter[] converters = new SampleConverter[numDstBands];
        final BufferedImage  image  = getDestination(imageIndex, param, width, height, converters);
        final WritableRaster raster = image.getRaster();
        assert raster.getNumBands() == numDstBands : numDstBands;
        /*
         * Computes the source region (in the NetCDF file) and the destination region
         * (in the buffered image). Copies those informations into UCAR Range structure.
         */
        final Rectangle  srcRegion = new Rectangle();
        final Rectangle destRegion = new Rectangle();
        computeRegions(param, width, height, image, srcRegion, destRegion);
        final boolean isGrib = dataset.getFileTypeId().startsWith("GRIB");
        if (!isGrib) {
            IIOImageHelper.flipVertically(param, height, srcRegion);
        }
        final int[] dimensionSlices = getSourceIndices(param, rank);
        final Range[] ranges = new Range[rank];
        for (int i=0; i<ranges.length; i++) {
            final int first, length, stride;
            switch (rank - i) {
                case X_DIMENSION + 1: {
                    first  = srcRegion.x;
                    length = srcRegion.width;
                    stride = strideX;
                    break;
                }
                case Y_DIMENSION + 1: {
                    first  = srcRegion.y;
                    length = srcRegion.height;
                    stride = strideY;
                    break;
                }
                default: {
                    first  = dimensionSlices[i]; // Already in NetCDF order.
                    length = 1;
                    stride = 1;
                    break;
                }
            }
            try {
                ranges[i] = new Range(first, first+length-1, stride);
            } catch (InvalidRangeException e) {
                throw netcdfFailure(e);
            }
        }
        final List<Range> sections = Arrays.asList(ranges);
        /*
         * Reads the requested sub-region only. In the usual case, we read only the current
         * variable. However if the setBandNames(...) method has been invoked, we may have
         * many different variables to read, one for each band.
         */
        processImageStarted(imageIndex);
        final float toPercent = 100f / numDstBands;
        final int type = raster.getSampleModel().getDataType();
        final int xmin = destRegion.x;
        final int ymin = destRegion.y;
        final int xmax = destRegion.width  + xmin;
        final int ymax = destRegion.height + ymin;
        for (int zi=0; zi<numDstBands; zi++) {
            final int srcBand = (srcBands == null) ? zi : srcBands[zi];
            final int dstBand = (dstBands == null) ? zi : dstBands[zi];
            Variable bandVariable = variable;
            if (bandNames != null) {
                final String name = bandNames.get(srcBand);
                if (!name.equals(variableName)) {
                    bandVariable = findVariable(name);
                }
            }
            final Array array;
            try {
                if (bandDimension >= 0) {
                    /*
                     * Update the Section instance with the index of the slice to read. This code
                     * is executed only if the band API is used for one of the variable dimension,
                     * and the bands are not different variables (i.e. bandNames == null). Note
                     * that there is no need to update 'sections' directly since it wraps directly
                     * the 'ranges' array.
                     */
                    ranges[bandDimension] = new Range(srcBand, srcBand, 1);
                }
                if (imageDimension >= 0) {
                    /*
                     * Like above, but for image index.
                     */
                    ranges[imageDimension] = new Range(imageIndex, imageIndex, 1);
                }
                array = bandVariable.read(sections);
            } catch (InvalidRangeException e) {
                throw netcdfFailure(e);
            }
            SampleConverter converter = converters[zi];
            if (converter == null) {
                converter = SampleConverter.IDENTITY;
            }
            final IndexIterator it = array.getIndexIterator();
            /*
             * TEMPORARY PATCH: We are not supposed to flip the image for any format, either
             * Grib or NetCDF. Instead, we are supposed to flip the sign of the scaleY value
             * in the 'gridToCRS' affine transform. We will try to fix this problem in the
             * port to Apache SIS.
             */
            for (int y=isGrib ? ymin : ymax; isGrib ? y<ymax : --y>=ymin;) { // Y_POSITION
                for (int x=xmin; x<xmax; x++) {                              // X_POSITION
                    switch (type) {
                        case DataBuffer.TYPE_DOUBLE: {
                            raster.setSample(x, y, dstBand, converter.convert(it.getDoubleNext()));
                            break;
                        }
                        case DataBuffer.TYPE_FLOAT: {
                            raster.setSample(x, y, dstBand, converter.convert(it.getFloatNext()));
                            break;
                        }
                        default: {
                            raster.setSample(x, y, dstBand, converter.convert(it.getIntNext()));
                            break;
                        }
                    }
                }
                if (isGrib) y++;
            }
            /*
             * Checks for abort requests after reading. It would be a waste of a potentially
             * good image (maybe the abort request occurred after we just finished the reading)
             * if we didn't implemented the 'isCancel()' method. But because of the later, which
             * is checked by the NetCDF library, we can't assume that the image is complete.
             */
            if (abortRequested()) {
                processReadAborted();
                return image;
            }
            /*
             * Reports progress here, not in the deeper loop, because the costly part is the
             * call to 'variable.read(...)' which can't report progress.  The loop that copy
             * pixel values is fast, so reporting progress there would be pointless.
             */
            processImageProgress(zi * toPercent);
        }
        if (lastError != null) {
            throw new IIOException(lastError);
        }
        processImageComplete();
        return image;
    }

    /**
     * Creates a raster from the specified parameters. This method is a bit closer to the actual
     * NetCDF model than the {@link #read(int, ImageReadParam)}, because NetCDF file usually don't
     * provide color information.
     *
     * @throws IOException If an error occurred while reading the NetCDF file.
     *
     * @todo Current implementation delegates to {@code read(int, param)}.
     *       Futures versions should do a more efficient work.
     *
     * @since 3.20
     */
    @Override
    public Raster readRaster(final int imageIndex, final ImageReadParam param) throws IOException {
        return read(imageIndex, param).getRaster();
    }

    /**
     * Returns {@code true} since this class supports calls to
     * {@link #readRaster(int, ImageReadParam)}.
     *
     * @since 3.20
     */
    @Override
    public boolean canReadRaster() {
        return true;
    }

    /**
     * Wraps a generic exception into an {@link IIOException}.
     */
    private IIOException netcdfFailure(final Exception e) throws IOException {
        return new IIOException(errors().getString(Errors.Keys.CANT_READ_FILE_$1, dataset.getLocation()), e);
    }

    /**
     * Invoked by the NetCDF library during read operation in order to check if the task has
     * been canceled. Users should not invoke this method directly.
     *
     * @return {@code true} if abort has been requested.
     */
    @Override
    public boolean isCancel() {
        return abortRequested();
    }

    /**
     * Invoked by the NetCDF library when an error occurred during the read operation.
     * Users should not invoke this method directly.
     *
     * @param message An error message to report.
     */
    @Override
    public void setError(final String message) {
        lastError = message;
    }

    /**
     * Returns the error resources bundle.
     */
    private Errors errors() {
        return Errors.getResources(locale);
    }

    /**
     * Closes the NetCDF file.
     *
     * @throws IOException If an error occurred while accessing the NetCDF file.
     */
    @Override
    protected void close() throws IOException {
        metadataLoaded = false;
        crsBuilder     = null;
        gridMapping    = null;
        lastError      = null;
        variable       = null;
        variableName   = null;
        variableNames  = null;
        try {
            if (dataset != null) {
                dataset.close();
                dataset = null;
            }
        } finally {
            super.close(); // Must delete the temporary file only after we closed the dataset.
        }
    }

    /**
     * Restores the {@code ImageReader} to its initial state. This method removes the input,
     * the locale, all listeners and any {@link org.geotoolkit.image.io.DimensionSlice.API}.
     */
    @Override
    public void reset() {
        super.reset();
        dimensionManager.clear();
    }



    /**
     * The service provider for {@code NetcdfImageReader}. This SPI provides
     * necessary implementation for creating default {@link NetcdfImageReader}.
     * <p>
     * The default constructor initializes the fields to the values listed below.
     * Users wanting different values should create a subclass of {@code Spi} and
     * set the desired values in their constructor.
     * <p>
     * <table border="1" cellspacing="0">
     *   <tr bgcolor="lightblue"><th>Field</th><th>Value</th></tr>
     *   <tr><td>&nbsp;{@link #names}           &nbsp;</td><td>&nbsp;{@code "NetCDF"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #MIMETypes}       &nbsp;</td><td>&nbsp;{@code "application/netcdf"}, {@code "application/x-netcdf"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #pluginClassName} &nbsp;</td><td>&nbsp;{@code "org.geotoolkit.image.io.plugin.NetcdfImageReader"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #vendorName}      &nbsp;</td><td>&nbsp;{@code "Geotoolkit.org"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #version}         &nbsp;</td><td>&nbsp;Value of {@link org.geotoolkit.util.Version#GEOTOOLKIT}&nbsp;</td></tr>
     *   <tr><td colspan="2" align="center">See super-class javadoc for remaining fields</td></tr>
     * </table>
     *
     * @author Martin Desruisseaux (Geomatys)
     * @author Antoine Hnawia (IRD)
     * @version 3.20
     *
     * @since 3.08 (derived from 2.4)
     * @module
     */
    public static class Spi extends FileImageReader.Spi {
        /**
         * The name of the native format. It has no version number because this is
         * a "dynamic" format inferred from the actual content of the NetCDF file.
         */
        static final String NATIVE_FORMAT_NAME = "NetCDF";

        /**
         * List of legal names for NetCDF readers.
         */
        static final String[] NAMES = new String[] {"NetCDF", "netcdf"};

        /**
         * The mime types for the default {@link NetcdfImageReader} configuration.
         */
        static final String[] MIME_TYPES = new String[] {"application/netcdf", "application/x-netcdf"};

        /**
         * Default list of file's extensions.
         */
        static final String[] SUFFIXES = new String[] {"nc", "ncml", "cdf", "grib", "grib1", "grib2", "grb", "grb1", "grb2", "grd"};

        /**
         * Constructs a default {@code NetcdfImageReader.Spi}. The fields are initialized as
         * documented in the <a href="#skip-navbar_top">class javadoc</a>. Subclasses can
         * modify those values if desired.
         * <p>
         * For efficiency reasons, the fields are initialized to shared arrays.
         * Subclasses can assign new arrays, but should not modify the default array content.
         */
        public Spi() {
            names           = NAMES;
            MIMETypes       = MIME_TYPES;
            suffixes        = SUFFIXES;
            pluginClassName = "org.geotoolkit.image.io.plugin.NetcdfImageReader";
            writerSpiNames  = new String[] {"org.geotoolkit.image.io.plugin.NetcdfImageWriter$Spi"};
            final int length = inputTypes.length;
            inputTypes = Arrays.copyOf(inputTypes, length+1);
            inputTypes[length] = NetcdfFile.class;
            nativeStreamMetadataFormatName = NATIVE_FORMAT_NAME;
            nativeImageMetadataFormatName  = NATIVE_FORMAT_NAME;
            addExtraMetadataFormat(GEOTK_FORMAT_NAME, true, true);
            addExtraMetadataFormat(ISO_FORMAT_NAME, true, false);
        }

        /**
         * Returns a description for this provider.
         *
         * @todo Localize
         */
        @Override
        public String getDescription(final Locale locale) {
            return "NetCDF image decoder";
        }

        /**
         * Checks if the specified input seems to be a readable NetCDF file. Current
         * implementation check if the given source is a filename having one of the
         * NetCDF {@linkplain #getFileSuffixes() file suffixes}. In particular this
         * method conservatively returns {@code false} if the given input is a stream
         * like {@link javax.imageio.stream.ImageInputStream}, because testing the stream
         * content would require copying it to a temporary file.
         *
         * @param  source the object (typically a {@link File}) to be decoded.
         * @return {@code true} if it is likely that the given source can be decoded.
         * @throws IOException If an error occurred while opening the file.
         */
        @Override
        public boolean canDecodeInput(final Object source) throws IOException {
            if (IOUtilities.canProcessAsPath(source)) {
                return ArraysExt.containsIgnoreCase(SUFFIXES, IOUtilities.extension(source));
                /*
                 * If a future version wants to use NetcdfFile.canOpen(String),
                 * then please verify that the following issues are resolved:
                 *
                 *   - canOpen(String) recognizes NcML files (last time we tried, it didn't read
                 *     any XML file, which is one reason why we had to rely on file extension).
                 *
                 *   - canOpen(String) doesn't throw an OutOfMemoryError when given non-NetCDF
                 *     file (e.g. PNG, TIFF or JPEG files). Last time we tried, some text file
                 *     format decoders invoked BufferedReader.readLine(), which can read huge
                 *     amount of data when a binary file contains few '\n' or '\r' bytes.
                 *
                 *   - canOpen(String) doesn't load large library like VisAD just for testing
                 *     a few bytes, or at the very least doesn't throw NoClassDefFoundError
                 *     when such optional dependency is not on the classpath.
                 *
                 *   - canOpen(String) returns 'false' if the format is not recognized. Last
                 *     time we tried, some code paths throw IOException instead, which make
                 *     difficult to distinguish unrecognized formats from real I/O errors.
                 */
            }
            return false;
        }

        /**
         * Returns an instance of the {@code NetcdfImageReader} implementation associated
         * with this service provider.
         *
         * @param  extension An optional extension object, which may be null.
         * @return An image reader instance.
         * @throws IOException if the attempt to instantiate the reader fails.
         */
        @Override
        public ImageReader createReaderInstance(final Object extension) throws IOException {
            return new NetcdfImageReader(this);
        }
    }
}
