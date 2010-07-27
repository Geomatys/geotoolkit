/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.DataBuffer;
import java.net.URL;
import java.net.URI;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.lang.reflect.UndeclaredThrowableException;
import javax.imageio.IIOException;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageTypeSpecifier;

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
import ucar.nc2.util.CancelTask;
import ucar.nc2.Dimension;
import ucar.nc2.Variable;
import ucar.nc2.VariableIF;
import ucar.nc2.NetcdfFile;

import org.geotoolkit.image.io.Protocol;
import org.geotoolkit.image.io.DimensionSlice;
import org.geotoolkit.image.io.FileImageReader;
import org.geotoolkit.image.io.NamedImageStore;
import org.geotoolkit.image.io.SampleConverter;
import org.geotoolkit.image.io.SpatialImageReadParam;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.referencing.adapters.NetcdfAxis;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.Version;
import org.geotoolkit.util.XArrays;
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.util.collection.UnmodifiableArrayList;
import org.geotoolkit.util.logging.Logging;


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
 * Additional dimensions (if any) are ignored; only the slice at indice 0 is read by default,
 * which is <var>z</var><sub>0</sub> and <var>t</var><sub>0</sub> in the example above. Users
 * can change this behavior by specifying different slice indices, or load many slices as
 * different bands, using {@link SpatialImageReadParam}.
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
 * @version 3.11
 *
 * @since 3.08 (derived from 2.4)
 * @module
 */
public class NetcdfImageReader extends FileImageReader implements NamedImageStore, CancelTask {
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
     * The dimension <strong>relative to the rank</strong> in {@link #variable} to use as image
     * width. The actual dimension is {@code variable.getRank() - X_DIMENSION}. It is hard-coded
     * because the loop in the {@code read} method expects this order.
     */
    private static final int X_DIMENSION = 1;

    /**
     * The dimension <strong>relative to the rank</strong> in {@link #variable} to use as image
     * height. The actual dimension is {@code variable.getRank() - Y_DIMENSION}. It is hard-coded
     * because the loop in the {@code read} method expects this order.
     */
    private static final int Y_DIMENSION = 2;

    /**
     * The dimension <strong>relative to the rank</strong> in {@link #variable} to use for the
     * bands. This value is computed by {@link #initZDimension} from the user-supplied parameters.
     * <p>
     * Special values:
     * <ul>
     *   <li>0 if not yet computed.</li>
     *   <li>Any negative value if we have determined that there is no dimension for bands.
     *       This is a sentinal value for avoiding to compute it again.</li>
     * </ul>
     */
    private int zDimension;

    /**
     * The NetCDF dataset, or {@code null} if not yet open. The NetCDF file is open by
     * {@link #ensureOpen} when first needed.
     */
    private NetcdfDataset dataset;

    /**
     * The name of the {@linkplain Variable variables} to be read in a NetCDF file.
     * The first name is assigned to image index 0, the second name to image index 1,
     * <i>etc</i>. This list shall be immutable.
     */
    private List<String> variableNames, selectedNames;

    /**
     * The names of the {@linkplain Variable variables} to be assigned to bands,
     * or {@code null} if none.
     */
    private Map<Integer,List<String>> bandNames;

    /**
     * The image index of the current {@linkplain #variable variable}.
     */
    private int variableIndex;

    /**
     * The data from the NetCDF file for a given image index. The value for this field is set by
     * {@link #prepareVariable} when first needed, and may be updated when the argument given to
     * any method expecting a {@code imageIndex} parameter has changed.
     * <p>
     * This field is typically (but not necessarly) an instance of {@link VariableDS}.
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
     * Constructs a new NetCDF reader.
     *
     * @param spi The service provider.
     */
    public NetcdfImageReader(final Spi spi) {
        super(spi);
    }

    /**
     * Returns the names of the variables to be read. The first name is assigned to the image
     * at index 0, the second name to that image at index 1, <i>etc</i>. In other words a call
     * to <code>{@linkplain #read(int) read}(imageIndex)</code> will read the variable named
     * {@code variables.get(imageIndex)} where {@code variables} is the list returned by this
     * method.
     * <p>
     * The sequence of variable to be read can be changed by a call to {@link #setImageNames(String[])}.
     *
     * @return The names of the variables to be read.
     * @throws IOException if the NetCDF file can not be read.
     */
    @Override
    public List<String> getImageNames() throws IOException {
        if (selectedNames == null) {
            ensureFileOpen();
        }
        return selectedNames;
    }

    /**
     * Sets the name of the {@linkplain Variable variables} to be read in a NetCDF file.
     * The first name is assigned to image index 0, the second name to image index 1,
     * <i>etc</i>.
     * <p>
     * If {@code variableNames} is set to {@code null}, then the variables will be inferred
     * from the content of the NetCDF file. This is the default behavior.
     *
     * @param variableNames The set of variables to be assigned to image index,
     *        or {@code null} for all variables declared in the NetCDF file.
     * @throws IOException if the NetCDF file can not be read.
     */
    @Override
    public void setImageNames(String... variableNames) throws IOException {
        if (variableNames != null) {
            variableNames = variableNames.clone();
            ensureNonNull(variableNames);
            selectedNames = UnmodifiableArrayList.wrap(variableNames);
        } else {
            ensureFileOpen();
            selectedNames = getVariableNames();
        }
        variable = null; // Will force a reload.
    }

    /**
     * Ensures that the given array does not contain any null element.
     */
    private void ensureNonNull(final String[] names) {
        for (int i=0; i<names.length; i++) {
            final String name = names[i];
            if (name == null) {
                throw new NullArgumentException(errors().getString(
                        Errors.Keys.NULL_ARGUMENT_$1, "names[" + i + ']'));
            }
        }
    }

    /**
     * Returns the number of images available from the current input source.
     * This is the number of {@linkplain #getImageNames() variables}, since each
     * variable is considered an image.
     *
     * @throws IllegalStateException if the input source has not been set.
     * @throws IOException if an error occurs reading the information from the input source.
     */
    @Override
    public int getNumImages(final boolean allowSearch) throws IllegalStateException, IOException {
        ensureFileOpen();
        return selectedNames.size();
    }

    /**
     * Returns the names of the bands for the given image, or {@code null} if none.
     * By default, this method returns {@code null} for every image index.
     *
     * @param  imageIndex Index of the image for which to get the band names.
     * @return The variable names of the bands for the given image, or {@code null} if the bands
     *         are unamed.
     * @throws IOException if the NetCDF file can not be read.
     *
     * @since 3.11
     */
    @Override
    public List<String> getBandNames(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        if (bandNames != null) {
            return bandNames.get(imageIndex);
        }
        return null;
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
        if (names == null) {
            if (bandNames != null) {
                bandNames.remove(imageIndex);
            }
        } else {
            if (names.length == 0) {
                throw new IllegalArgumentException(errors().getString(Errors.Keys.EMPTY_ARRAY));
            }
            if (bandNames == null) {
                bandNames = new HashMap<Integer,List<String>>();
            }
            names = names.clone();
            ensureNonNull(names);
            bandNames.put(imageIndex, UnmodifiableArrayList.wrap(names));
        }
    }

    /**
     * Returns the number of bands available for the image identified by the given index. The
     * default implementation returns the value of the first of the following conditions which
     * is hold:
     * <p>
     * <ol>
     *   <li>If the bands at the give image index {@linkplain #setBandNames have been
     *       assigned to variable names}, returns the number of assigned variables.</li>
     *   <li>Otherwise if the bands API has been {@linkplain DimensionSlice#setAPI assigned to a
     *       dimension}, return the {@linkplain VariableIF#getDimension(int) dimension} length of
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
        if (bandNames != null) {
            final List<String> names = bandNames.get(imageIndex);
            if (names != null) {
                return names.size();
            }
        }
        if (zDimension > 0) {
            prepareVariable(imageIndex);
            final int rank = variable.getRank();
            final int bandDimension = rank - zDimension;
            if (bandDimension >= 0 && bandDimension < rank) {
                return variable.getDimension(bandDimension).getLength();
            }
        }
        return super.getNumBands(imageIndex);
    }

    /**
     * Returns the image width.
     *
     * @throws IOException If an error occurred while reading the NetCDF file.
     */
    @Override
    public int getWidth(final int imageIndex) throws IOException {
        prepareVariable(imageIndex);
        return variable.getDimension(variable.getRank() - X_DIMENSION).getLength();
    }

    /**
     * Returns the image height.
     *
     * @throws IOException If an error occurred while reading the NetCDF file.
     */
    @Override
    public int getHeight(final int imageIndex) throws IOException {
        prepareVariable(imageIndex);
        return variable.getDimension(variable.getRank() - Y_DIMENSION).getLength();
    }

    /**
     * Returns the number of dimensions in the image identified by the given index.
     * In the case of NetCDF files, this is the {@linkplain VariableIF#getRank() rank}
     * of the {@linkplain #variable} associated to the given image index.
     *
     * @param  imageIndex The image index.
     * @return The number of dimension for the image at the given index.
     * @throws IOException if an error occurs reading the information from the input source.
     */
    @Override
    public int getDimension(final int imageIndex) throws IOException {
        prepareVariable(imageIndex);
        return variable.getRank();
    }

    /**
     * Initializes the value of {@link #zDimension} if not already done, and returns its previous
     * value. The caller must reset {@code zDimension} to its previous value after he is done.
     * <p>
     * This method is invoked by any method expecting an {@link ImageReadParam} which invoke,
     * directly or indirectly, the {@link #getNumBands(int)} method. It shall be restored to
     * its previous state after the method call in order to let {@code getNumBands(int)} to
     * have its normal default behavior (value computed without {@code ImageReadParam}).
     *
     * @param  param The parameters supplied by the user to the {@code read} method.
     * @param  rank The number of dimensions (the rank) in the {@linkplain #variable}.
     * @return The previous value of {@link #zDimension}.
     */
    private int initZDimension(final ImageReadParam param, final int rank) throws IOException {
        final int old = zDimension;
        if (old == 0) {
            int n = findDimensionIndex(DimensionSlice.API.BANDS, param, rank);
            if (n >= 0) {
                switch (++n) {
                    case X_DIMENSION:
                    case Y_DIMENSION: {
                        throw new IllegalArgumentException(errors().getString(
                                Errors.Keys.BAD_PARAMETER_$2, "DimensionSlice(BANDS)", n-1));
                    }
                }
            }
            // At this point, n can not be zero.
            zDimension = n;
        }
        return old;
    }

    /**
     * Returns the index of the dimension which has been assigned to the given API, or -1 if none.
     * The {@link #prepareVariable} method shall be invoked prior this method (this is not verified).
     *
     * @param  param The parameters supplied by the user to the {@code read} method.
     * @param  rank The number of dimensions (the rank) in the {@linkplain #variable}.
     */
    private int findDimensionIndex(final DimensionSlice.API api, final ImageReadParam param,
            final int rank) throws IOException
    {
        if (param instanceof SpatialImageReadParam) {
            final DimensionSlice slice = ((SpatialImageReadParam) param).getDimensionSliceForAPI(api);
            if (slice != null) try {
                /*
                 * The code below uses a custom Iterable in order to invoke the getAxes(...)
                 * method (which may force the loading of metadata) only if really needed.
                 */
                return slice.findDimensionIndex(new Iterable<Map.Entry<?,Integer>>() {
                    @Override public Iterator<Map.Entry<?,Integer>> iterator() {
                        final List<CoordinateAxis> axes;
                        try {
                            axes = getAxes(rank);
                        } catch (IOException e) {
                            // Will be caught in the enclosing method.
                            throw new UndeclaredThrowableException(e);
                        }
                        return (axes != null) ? new NetcdfAxesIterator(axes) :
                                Collections.<Map.Entry<?,Integer>>emptySet().iterator();
                    }
                });
            } catch (UndeclaredThrowableException e) {
                final Throwable cause = e.getCause();
                if (cause instanceof IOException) {
                    throw (IOException) cause;
                }
                throw e;
            }
        }
        return -1;
    }

    /**
     * Returns the indices along all dimensions of the slice to read. The default value for
     * indices that were not explicitly specified is 0.
     * <p>
     * The {@link #prepareVariable} method shall be invoked prior this method (this is not verified).
     *
     * @param  param The parameters supplied by the user to the {@code read} method.
     * @param  rank The number of dimensions (the rank) in the {@linkplain #variable}.
     * @return The indices as a value from 0 inclusive to {@link Dimension#getLength} exclusive.
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
                    final CoordinateAxis axis = axes.get(i);
                    switch (properties.length) {
                        default: properties[2] = NetcdfAxis.getDirection(axis);
                        case 2:  properties[1] = axis.getName();
                        case 1:  properties[0] = i;
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
     *
     * @param  rank The number of dimensions (the rank) in the {@linkplain #variable}.
     * @return The axis having a length equals or greater than {@code dimension}, or {@code null}.
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
         * For image metadata, returns a tree built from the variable attributes, where
         * the variable is inferred from the image index. In the special case where the
         * user assigned many variable to the same image index (where each variable is
         * handled as a band), we need to build the variables list from the band names.
         */
        Variable[] variables = null;
        if (bandNames != null) {
            final List<String> names = bandNames.get(imageIndex);
            if (names != null) {
                variables = new Variable[names.size()];
                for (int i=0; i<variables.length; i++) {
                    variables[i] = findVariable(names.get(i));
                }
            }
        }
        if (variables == null) {
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
     * Returns an image type specifier for the image at the given index. This method delegates
     * to the {@linkplain FileImageReader#getImageType(int, ImageReadParam, SampleConverter[])
     * super-class method}, with some additional work for handling the
     * {@linkplain SpatialImageReadParam#getDimensionSliceForAPI dimension assigned to the band
     * API} if any.
     */
    @Override
    protected ImageTypeSpecifier getImageType(final int               imageIndex,
                                              final ImageReadParam    parameters,
                                              final SampleConverter[] converters)
            throws IOException
    {
        prepareVariable(imageIndex);
        final int old = initZDimension(parameters, variable.getRank());
        try {
            return super.getImageType(imageIndex, parameters, converters);
        } finally {
            zDimension = old;
        }
    }

    /**
     * Returns {@code true} if the specified variable is a dimension of an other variable.
     * Such dimensions will be excluded from the list returned by {@link #getImageNames()}.
     *
     * @param  candidate The variable to test.
     * @param  variables The list of variables.
     * @return {@code true} if the specified variable is a dimension of an other variable.
     */
    private static boolean isAxis(final VariableIF candidate, final List<? extends VariableIF> variables) {
        final String name = candidate.getName();
        final int size = variables.size();
        for (int i=0; i<size; i++) {
            final VariableIF var = variables.get(i);
            if (var != candidate) {
                Dimension dim;
                for (int d=0; (dim=var.getDimension(d)) != null; d++) {
                    if (dim.getName().equals(name)) {
                        return true;
                    }
                }
            }
        }
        return false;
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
            if (selectedNames == null) {
                selectedNames = getVariableNames();
            }
        }
    }

    /**
     * Returns the name of all variables in the current NetCDF file. The {@link #ensureFileOpen()}
     * method must have been invoked before this method (this is not verified).
     */
    private List<String> getVariableNames() {
        if (variableNames == null) {
            /*
             * Gets a list of every variables found in the NetcdfDataset and copies the names
             * in a filtered list which exclude every variable that are dimension of an other
             * variable. For example "longitude" may be a variable found in the NetcdfDataset,
             * but is declared only because it is needed as a dimension for the "temperature"
             * variable. The "longitude" variable is usually not of direct interest to the user
             * (the interresting variable is "temperature"), so we exclude it.
             */
            final List<Variable> variables = dataset.getVariables();
            final String[] filtered = new String[variables.size()];
            int count = 0;
            for (int i=0; i<filtered.length; i++) {
                final VariableIF candidate = variables.get(i);
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
                 *   are often used for annotations and we don't wan't to confuse them
                 *   with images.
                 */
                if (candidate.getRank() >= 2 && !isAxis(candidate, variables) &&
                        NetcdfVariable.VALID_TYPES.contains(candidate.getDataType()))
                {
                    filtered[count++] = candidate.getName();
                }
            }
            variableNames = UnmodifiableArrayList.wrap(XArrays.resize(filtered, count));
        }
        return variableNames;
    }

    /**
     * Ensures that data are loaded in the NetCDF {@linkplain #variable}. If data are already
     * loaded, then this method does nothing.
     * <p>
     * This method is invoked automatically before any operation requirying the NetCDF
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
        if (variable == null || variableIndex != imageIndex) {
            checkImageIndex(imageIndex);
            ensureFileOpen();
            final String name = selectedNames.get(imageIndex);
            final Variable candidate = findVariable(name);
            final int rank = candidate.getRank();
            if (rank < 2) {
                throw new IIOException(errors().getString(Errors.Keys.NOT_TWO_DIMENSIONAL_$1, rank));
            }
            variable      = candidate;
            variableIndex = imageIndex;
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
                if (variable!=null && name.equalsIgnoreCase(variable.getName())) {
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
         * Fetchs the parameters that are not already processed by utility
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
        final int oldZ = initZDimension(param, rank);
        final int bandDimension = rank - zDimension;
        boolean hasBandDimension = (bandDimension >= 0 && bandDimension < rank);
        /*
         * Gets the number of source bands, in preference order (must be consistent with the
         * getNumBands(int) method): from the explicit list of band names, from the zDimension,
         * or 1. Then check that the number of source and target bands are consistent.
         */
        int numSrcBands = hasBandDimension ? variable.getDimension(bandDimension).getLength() : 1;
        String[] srcBandNames = null; // Will be used later if non-null.
        if (bandNames != null) {
            final List<String> names = bandNames.get(imageIndex);
            if (names != null) {
                numSrcBands  = names.size();
                srcBandNames = names.toArray(new String[numSrcBands]);
                hasBandDimension = false;
            }
        }
        final int numDstBands = (dstBands != null) ? dstBands.length : numSrcBands;
        checkReadParamBandSettings(param, numSrcBands, numDstBands);
        /*
         * Gets the destination image of appropriate size.
         */
        final int width  = variable.getDimension(rank - X_DIMENSION).getLength();
        final int height = variable.getDimension(rank - Y_DIMENSION).getLength();
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
        flipVertically(param, height, srcRegion);
        final int[] dimensionSlices = getSourceIndices(param, rank);
        final Range[] ranges = new Range[rank];
        for (int i=0; i<ranges.length; i++) {
            final int first, length, stride;
            switch (rank - i) {
                case X_DIMENSION: {
                    first  = srcRegion.x;
                    length = srcRegion.width;
                    stride = strideX;
                    break;
                }
                case Y_DIMENSION: {
                    first  = srcRegion.y;
                    length = srcRegion.height;
                    stride = strideY;
                    break;
                }
                default: {
                    first  = dimensionSlices[i];
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
        zDimension = oldZ;
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
            if (srcBandNames != null) {
                final String name = srcBandNames[srcBand];
                if (!name.equals(selectedNames.get(imageIndex))) {
                    bandVariable = findVariable(name);
                }
            }
            final Array array;
            try {
                if (hasBandDimension) {
                    /*
                     * Update the Section instance with the index of the slice to read. This code
                     * is executed only if the band API is used for one of the variable dimension,
                     * and the bands are not different variables (i.e. srcBandNames == null). Note
                     * that there is no need to update 'sections' directly since it wraps directly
                     * the 'ranges' array.
                     */
                    ranges[bandDimension] = new Range(srcBand, srcBand, 1);
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
            for (int y=ymax; --y>=ymin;) {
                for (int x=xmin; x<xmax; x++) {
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
     * Wraps a generic exception into a {@link IIOException}.
     */
    private IIOException netcdfFailure(final Exception e) throws IOException {
        return new IIOException(errors().getString(Errors.Keys.CANT_READ_$1, dataset.getLocation()), e);
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
        lastError      = null;
        variable       = null;
        variableNames  = null;
        selectedNames  = null;
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
     *   <tr><td>&nbsp;{@link #version}         &nbsp;</td><td>&nbsp;{@link Version#GEOTOOLKIT}&nbsp;</td></tr>
     *   <tr><td colspan="2" align="center">See super-class javadoc for remaining fields</td></tr>
     * </table>
     *
     * @author Martin Desruisseaux (Geomatys)
     * @author Antoine Hnawia (IRD)
     * @version 3.08
     *
     * @since 3.08 (derived from 2.4)
     * @module
     */
    public static class Spi extends FileImageReader.Spi {
        /**
         * List of legal names for NetCDF readers.
         */
        private static final String[] NAMES = new String[] {"NetCDF", "netcdf"};

        /**
         * The mime types for the default {@link NetcdfImageReader} configuration.
         */
        private static final String[] MIME_TYPES = new String[] {"application/netcdf", "application/x-netcdf"};

        /**
         * Default list of file's extensions.
         */
        private static final String[] SUFFIXES = new String[] {"nc", "cdf", "grib", "grb", "grb1", "grb2"};

        /**
         * Constructs a default {@code NetcdfImageReader.Spi}. The fields are initialized as
         * documented in the <a href="#skip-navbar_top">class javadoc</a>. Subclasses can
         * modify those values if desired.
         * <p>
         * For efficienty reasons, the fields are initialized to shared arrays.
         * Subclasses can assign new arrays, but should not modify the default array content.
         */
        public Spi() {
            names           = NAMES;
            MIMETypes       = MIME_TYPES;
            suffixes        = SUFFIXES;
            pluginClassName = "org.geotoolkit.image.io.plugin.NetcdfImageReader";
            vendorName      = "Geotoolkit.org";
            version         = Version.GEOTOOLKIT.toString();
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
         * Checks if the specified input seems to be a readeable NetCDF file. If the given
         * input is a stream like {@link javax.imageio.stream.ImageInputStream}, then this
         * method conservatively returns {@code false} because testing this stream would
         * require copying it to a temporary file.
         *
         * @param  source the object (typically a {@link File}) to be decoded.
         * @return {@code true} if it is likely that the given source can be decoded.
         * @throws IOException If an error occurred while opening the file.
         */
        @Override
        public boolean canDecodeInput(final Object source) throws IOException {
            if (source instanceof CharSequence || source instanceof File ||
                source instanceof URL || source instanceof URI)
            {
                try {
                    return NetcdfFile.canOpen(source.toString());
                } catch (NoClassDefFoundError e) {
                    // May happen if an optional JAR file (e.g. the VisAD library) is not present.
                    Logging.unexpectedException(LOGGER, Spi.class, "canDecodeInput", e);
                }
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
