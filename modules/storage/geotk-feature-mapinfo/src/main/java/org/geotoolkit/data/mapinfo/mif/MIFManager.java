/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.data.mapinfo.mif;

import com.vividsolutions.jts.geom.Geometry;
import org.geotoolkit.nio.IOUtilities;
import org.opengis.util.GenericName;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.mapinfo.ProjectionUtils;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.NullArgumentException;
import org.apache.sis.util.logging.Logging;

import org.apache.sis.util.Utilities;
import static java.nio.file.StandardOpenOption.*;
import java.util.stream.Collectors;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.AttributeTypeBuilder;
import org.geotoolkit.feature.FeatureExt;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.feature.builder.PropertyTypeBuilder;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.storage.IllegalNameException;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.util.FactoryException;

/**
 * Read types of .mif file, and manage readers / writers (from mif/mid mapinfo exchange format).
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 20/02/13
 */
public class MIFManager {

    public static final Logger LOGGER = Logging.getLogger("org.geotoolkit.data.mapinfo.mif");

    /**
     * A pattern frequently used to find MIF categories (for words without digit).
     */
    public static final Pattern ALPHA_PATTERN = Pattern.compile("[\\p{L}_][\\p{javaLetterOrDigit}_]*");

    /** To manage accesses to file. */
    private final ReadWriteLock RWLock = new ReentrantReadWriteLock();

    /**
     * Mif file access
     */
    private String mifName;
    private final URI mifPath;
    private Scanner mifScanner;

    /** Path to the MID file. */
    private URI midPath;

    /**
     * Header tag values. See {@link MIFHeaderCategory} for tag description.
     */
    private short mifVersion = 300;
    private String mifCharset = "Neutral";
    public char mifDelimiter = '\t';
    private final ArrayList<Short> mifUnique = new ArrayList<>();
    private final ArrayList<Short> mifIndex = new ArrayList<>();
    private CoordinateReferenceSystem mifCRS = CommonCRS.WGS84.normalizedGeographic();
    private MathTransform mifTransform = null;

    /**
     * The mif crs as it will be defined in final MIF file. We need it because it could be some differences between the
     * CRS of features added by user, and the one that will be written (Ex : written crs first axis have to be east).
     */
    private CoordinateReferenceSystem writtenCRS = null;

    /**
     * All geometries in a MIF file must get the same CRS. This trigger will serve to know if user add multiple
     * geometries with different CRS,
     */
    private boolean crsSet = false;

    /**
     * Feature type corresponding to MID file content. It contains all properties
     * except geometries and related style rules. Needed to isolate feature properties
     * and building / reading more easily.
     */
    private FeatureType midType;

    /**
     * Base mif data type. Inherits from {@link #midType}, adding a generic
     * geometry property. Ignores styling rules. This data type is needed to
     * allow end users to read all features of the datasource indifferently.
     */
    private FeatureType mifBaseType;

    /**
     *
     * @param mifFile MIF File to read or modify.
     * @throws NullArgumentException If the given file is null.
     * @throws DataStoreException If there's a problem with file naming.
     * @throws IOException If we cannot reach input file.
     * @throws URISyntaxException If we cannot make an URI from the file.
     * @deprecated Use {@link #MIFManager(java.net.URI) } instead.
     */
    public MIFManager(File mifFile) throws NullArgumentException, DataStoreException, IOException, URISyntaxException {
        ArgumentChecks.ensureNonNull("Input file", mifFile);
        mifPath = mifFile.toURI();
        init();
    }

    public MIFManager(URI mifFilePath) throws NullArgumentException, DataStoreException, IOException, URISyntaxException {
        ArgumentChecks.ensureNonNull("Input file path", mifFilePath);
        mifPath = mifFilePath;
        init();
    }

    /**
     * Basic operations needed in both constructors.
     */
    private void init() throws DataStoreException, IOException, URISyntaxException {
        final String mifStr = mifPath.getPath();
        int lastSeparatorIndex = mifStr.lastIndexOf(System.getProperty("file.separator"));
        mifName = mifStr.substring(lastSeparatorIndex+1);
        if (mifName.toLowerCase().endsWith(".mif")) {
            mifName = mifName.substring(0, mifName.length() - 4);
        }
        buildMIDPath();
    }

    /**
     *
     * @return Coordinate reference system described in underlying mif file. Can
     * be null.
     */
    public CoordinateReferenceSystem getMifCRS() {
        return mifCRS;
    }

    /**
     * Note : this transform is already used at read time. Therefore, it's only
     * provided for information purpose.
     *
     * @return The math transform which must be applied to the geometries in the
     * MIF file before being used.
     */
    public MathTransform getTransform() {
        return mifTransform;
    }


    /**
     * Return the different type names specified by this document.
     *
     * If the scanner did not already read them, we catch them all by parsing the file with {@link MIFManager#buildDataTypes()}.
     *
     * @return a list ({@link HashSet}) of available feature types in that document.
     * @throws DataStoreException if we get a problem parsing the file.
     */
    public Set<GenericName> getTypeNames() throws DataStoreException {
        checkDataTypes();

        if (mifBaseType == null) {
            return Collections.EMPTY_SET;
        } else {
            return Collections.singleton(mifBaseType.getName());
        }
    }


    /**
     * Try to add a new Feature type to the current store.
     * @param typeName The name of the type to add.
     * @param toAdd The type to add.
     * @throws DataStoreException If an unexpected error occurs while referencing given type.
     * @throws URISyntaxException If the URL specified at store creation is invalid.
     * @throws java.io.IOException If an error occurs while writing given schema on underlying storage.
     */
    public void addSchema(GenericName typeName, FeatureType toAdd) throws DataStoreException, URISyntaxException, IOException {
        ArgumentChecks.ensureNonNull("New feature type", toAdd);

        /*
         * We'll try to get the available types from datastore. If an exception raises while this operation, the source
         * file is invalid, so we try to delete it before going on.
         */
        try {
            getTypeNames();
        } catch (Exception e) {
            // Try to clear files before rewriting in it.
            try {
                Path mifPathObj = IOUtilities.toPath(mifPath);
                Path midPathObj = IOUtilities.toPath(midPath);

                Files.deleteIfExists(mifPathObj);
                Files.deleteIfExists(midPathObj);
            } catch (IOException e1) {
                throw new DataStoreException("Unable to erase MIF and MID files.", e1);
            }
            refreshMetaModel();
        }

        if(!toAdd.isSimple()){
            throw new DataStoreException("Only Simple Features, or features with a Simple Feature as parent can be added.");
        }

        //We check for the crs first
        checkTypeCRS(toAdd);

        // If we're on a new store, we must set the base type and write the header.
       deriveBaseTypes(toAdd);
       flushHeader();
    }

    /**
     * Try to compute {@link #midType} and {@link #mifBaseType} from input feature type.
     * Implementation note : We'll try to remove reserved attributes (style rules), then we'll remove geometry to build mid type.
     * Finally, if input type contained a geometry, we use it to build the mif base type.
     *
     * @param ft The data type to analyze and decompose.
     */
    private void deriveBaseTypes(final FeatureType ft) {
        FeatureTypeBuilder ftb = new FeatureTypeBuilder(ft);
        ftb.setName(ft.getName().toString()+"_properties");
        final Iterator<PropertyTypeBuilder> it = ftb.properties().iterator();
        AttributeType geometry = null;
        boolean sisGeometry = false;
        while (it.hasNext()) {
            final PropertyTypeBuilder next = it.next();
            /*  We have to determine if it's a geometry we should override (generify).
             * We'll also check if it's a supported type.
             */
            if (next instanceof AttributeTypeBuilder) {
                final AttributeTypeBuilder builder = (AttributeTypeBuilder) next;
                final Class valueClass = builder.getValueClass();
                if (Geometry.class.isAssignableFrom(valueClass)) {
                    if (geometry != null) {
                        throw new IllegalArgumentException("Only one geometry is accepted for mif-mid, but given type contains multiple : " + System.lineSeparator() + ft);
                    } else if (!Geometry.class.equals(valueClass)) {
                        builder.setValueClass(Geometry.class);
                    }
                    geometry = builder.build();
                    // Geometry property is set aside, because we'll build mid type without geometry, then use it to build mif-type with geometry.
                    it.remove();

                } else if (MIFUtils.getColumnMIFType(valueClass) == null) {
                    // not supported
                    throw new IllegalArgumentException("MIF-MID format cannot write elements of type " + valueClass);
                }
            } else if (AttributeConvention.GEOMETRY_PROPERTY.equals(next.getName())) {
                sisGeometry = true;
                it.remove();
            }
        }

        if (ftb.properties().size() < 1 && ftb.getSuperTypes().length == 1) {
            midType = ft.getSuperTypes().iterator().next();
            mifBaseType = ft;
        } else {
            midType = ftb.build();
            if (geometry != null || sisGeometry) {
                ftb = new FeatureTypeBuilder();
                ftb.setSuperTypes(midType);
                ftb.setName(ft.getName());
                if (geometry != null) {
                    final AttributeTypeBuilder geomBuilder = ftb.addAttribute(geometry);
                    if (sisGeometry) {
                        geomBuilder.addRole(AttributeRole.DEFAULT_GEOMETRY);
                    }
                } else {
                    ftb.addProperty(ft.getProperty(AttributeConvention.GEOMETRY_PROPERTY.toString()));
                }
                mifBaseType = ftb.build();
            }
        }
    }

    private void checkTypeCRS(FeatureType toCheck) throws DataStoreException {
        CoordinateReferenceSystem crs = FeatureExt.getCRS(toCheck);
        if (crs == null) return;

        if (!crsSet) {
            mifCRS = crs;
            crsSet = true;
            /**
             * We check if mif conversion will modify the defined CRS. If it is the case, we store the modified CRS.
             * This CRS will serve us as file writing, as we will have to reproject our features to fit the final system.
             */
            if (!Utilities.equalsIgnoreMetadata(mifCRS, CommonCRS.WGS84.normalizedGeographic())) {
                try {
                    final String mifCRSDefinition = ProjectionUtils.crsToMIFSyntax(mifCRS);
                    if (mifCRSDefinition != null && !mifCRSDefinition.isEmpty()) {
                        writtenCRS = ProjectionUtils.buildCRSFromMIF(mifCRSDefinition);
                        if (Utilities.equalsIgnoreMetadata(mifCRS, writtenCRS)) {
                            writtenCRS = null;
                        }
                    }
                } catch (Exception e) {
                    // Nothing to do here, if a CRS incompatibility has been raised, it will be well raise at MIF file flushing.
                }
            }
        } else if (!mifCRS.equals(crs)) {
            throw new DataStoreException("Given type CRS is not compatible with the one previously specified." +
                    "\nExpected : " + mifCRS + "\nFound : " + crs);
        }
    }


    public void deleteSchema(String typeName) throws DataStoreException {
        getTypeNames();

        if (mifBaseType != null) {
            final GenericName name = mifBaseType.getName();
            if (name.tip().toString().equals(typeName) || name.toString().equals(typeName)) {
                mifBaseType = null;
                midType = null;
                return;
            }
        }

        throw new DataStoreException("Unable to delete the feature type named " + typeName + "because it does not exists in this data store.");
    }



    public FeatureType getType(String typeName) throws DataStoreException {
        getTypeNames();

        if (mifBaseType != null) {
            final GenericName name = mifBaseType.getName();
            if (name.tip().toString().equals(typeName) || name.toString().equals(typeName)) {
                return mifBaseType;
            }
        }

        throw new DataStoreException("No type matching the given name have been found.");
    }


    FeatureType getBaseType() throws DataStoreException {
        checkDataTypes();
        return mifBaseType;
    }

    FeatureType getMIDType() throws DataStoreException {
        checkDataTypes();
        return midType;
    }

    public URI getMIFPath() {
        return mifPath;
    }

    public URI getMIDPath() {
        return midPath;
    }

    /**
     * Initialize the path to MID file. It can set midPath if, and ONLY if the mif header have been successfully parsed.
     * If column count is 0, MID file won't contain any content, so we don't care about it.
     *
     * @throws DataStoreException if the MIF path is malformed (because we use it to build MID path), or if there's no
     * valid file at built location.
     */
    private void buildMIDPath() throws DataStoreException, IOException, URISyntaxException {

        // We try to retrieve the mid path using files, so we can use filter which don't care about case.
        if (IOUtilities.canProcessAsPath(mifPath)) {
            final Path mif = IOUtilities.toPath(mifPath);
            final String mifName = mif.getFileName().toString();

            final String midCandidate = mifName.replaceFirst("\\.(?i)mif$", "") + "\\.mid";
            if (Files.exists(mif)) {

                DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
                    @Override
                    public boolean accept(Path entry) throws IOException {
                        Pattern pattern = Pattern.compile(midCandidate, Pattern.CASE_INSENSITIVE);
                        Matcher match = pattern.matcher(entry.getFileName().toString());
                        return match.matches() && Files.isRegularFile(entry);
                    }
                };

                try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(mif.getParent(), filter)) {
                    Iterator<Path> iterator = dirStream.iterator();
                    if (iterator.hasNext()) {
                        midPath = iterator.next().toUri();
                    }
                }
            }

            if (midPath == null) {
                midPath = IOUtilities.changeExtension(mif, "mid").toUri();
            }

        } else {
            final String mifStr = mifPath.getPath();
            String midStr = null;
            if (mifStr.endsWith(".mif") || mifStr.endsWith(".MIF")) {
                midStr = mifStr.substring(0, mifStr.length() - 4);
            } else {
                throw new DataStoreException("There's an extension problem with Mif file. A correct extension is needed in order to retrieve the associated mid file.");
            }

            // We have to check if the extension is upper or lower case, since unix file systems are case sensitive.
            if (mifStr.endsWith(".mif")) {
                midStr = midStr.concat(".mid");
            } else if (mifStr.endsWith(".MIF")) {
                midStr = midStr.concat(".MID");
            }
            midPath = URI.create(midStr);
        }
    }


    /**********************************************
     *          Methods with file access
     **********************************************/

    /**
     * Read .MIF file header and get needed information for data reading.
     *
     * @throws DataStoreException If all mandatory data can't be read.
     */
    private void parseHeader() throws DataStoreException {
        //Reset the file scanner to ensure we'll start on file top position.
        InputStream mifStream = null;
        try {
            if (mifScanner != null) {
                mifScanner.close();
            }

            RWLock.readLock().lock();
            mifStream = IOUtilities.open(mifPath);
            mifScanner = new Scanner(mifStream, getCharset().name());

            // A trigger to tell us if all mandatory categories have been parsed.
            boolean columnsParsed = false;
            while (mifScanner.hasNextLine()) {
                final String matched = mifScanner.findInLine(ALPHA_PATTERN);

                if (matched == null && !columnsParsed) {
                    // maybe we missed a line ?
                    mifScanner.nextLine();
                    continue;
                }

                if (matched.equalsIgnoreCase(MIFUtils.HeaderCategory.VERSION.name())) {
                    if (mifScanner.hasNextShort()) {
                        mifVersion = mifScanner.nextShort();
                    } else {
                        throw new DataStoreException("MIF Version can't be read.");
                    }

                } else if (matched.equalsIgnoreCase(MIFUtils.HeaderCategory.CHARSET.name())) {
                    final String tmpCharset = mifScanner.findInLine(ALPHA_PATTERN);
                    if (tmpCharset != null && !tmpCharset.equalsIgnoreCase(mifCharset)) {
                        mifCharset = tmpCharset;
                        parseHeader();
                        return;
                    }

                } else if (matched.equalsIgnoreCase(MIFUtils.HeaderCategory.DELIMITER.name())) {
                    final String tmpStr = mifScanner.findInLine("(\"|\')[^\"](\"|\')");
                    if (tmpStr == null || tmpStr.length() != 3) {
                        throw new DataStoreException(MIFHeaderCategory.DELIMITER.name() +
                                " tag value is not formatted as it should (must be \"C\" with C the wanted delimiter character).");
                    }
                    mifDelimiter = (char) tmpStr.getBytes()[1];

                } else if (matched.equalsIgnoreCase(MIFUtils.HeaderCategory.UNIQUE.name())) {
                    while (mifScanner.hasNextShort()) {
                        mifUnique.add(mifScanner.nextShort());
                    }

                } else if (matched.equalsIgnoreCase(MIFUtils.HeaderCategory.INDEX.name())) {
                    while (mifScanner.hasNextShort()) {
                        mifIndex.add(mifScanner.nextShort());
                    }

                } else if (matched.equalsIgnoreCase(MIFUtils.HeaderCategory.COORDSYS.name())) {
                    /*
                     * Don't know how many coefficients will be defined in the CRS, nor if it's written on a single
                     * line,so we iterate until the next header category clause, storing encountered data.
                     */
                    final StringBuilder crsStr = new StringBuilder();
                    boolean coordSysCase = true;
                    while(coordSysCase) {
                        crsStr.append(mifScanner.next());
                        for(MIFUtils.HeaderCategory category : MIFUtils.HeaderCategory.values()) {
                            Pattern pat = Pattern.compile(category.name(), Pattern.CASE_INSENSITIVE);
                            if(mifScanner.hasNext(pat)) {
                                coordSysCase = false;
                                break;
                            }
                        }
                    }

                    final CoordinateReferenceSystem crs = ProjectionUtils.buildCRSFromMIF(crsStr.toString());
                    if(crs != null) {
                        mifCRS = crs;
                    }
                } else if (matched.equalsIgnoreCase(MIFUtils.HeaderCategory.TRANSFORM.name())) {
                    double xResample, yResample, xTranslate, yTranslate;
                    xResample = mifScanner.nextDouble();
                    yResample = mifScanner.nextDouble();
                    xTranslate = mifScanner.nextDouble();
                    yTranslate = mifScanner.nextDouble();
                    mifTransform = new AffineTransform2D(xResample, 0, 0, yResample, xTranslate, yTranslate);
                    //Build the parent feature type for data contained in this MIF.
                } else if (matched.equalsIgnoreCase(MIFUtils.HeaderCategory.COLUMNS.name())) {
                    final int count;
                    if (mifScanner.hasNextShort()) {
                        count = mifScanner.nextShort();
                    } else {
                        throw new DataStoreException("MIF Columns has no attribute count specified.");
                    }
                    // If there's no defined column, there will not be any base type, only pure geometry features.
                    if (count > 0) {
                        parseColumns(count);
                    }
                    columnsParsed = true;
                } else if (matched.equalsIgnoreCase(MIFUtils.HeaderCategory.DATA.name())) {
                    if (!columnsParsed) {
                        throw new DataStoreException("File header can't be read (Columns mark is missing)");
                    } else {
                        break;
                    }
                }
                mifScanner.nextLine();
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "File header can't be read (creation mode ?).");
        } catch (Exception e) {
            throw new DataStoreException("MIF file header can't be read.", e);
        } finally {
            if(mifStream != null) {
                try {
                    mifStream.close();
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Input connection to MIF data can't be closed.", e);
                }
            }
            RWLock.readLock().unlock();
        }
    }

    /**
     * Ensure that dataTypes are built. If not, call {@link MIFManager#buildDataTypes()}.
     * @throws DataStoreException If we cannot read header to build data types.
     */
    public void checkDataTypes() throws DataStoreException {
        if (mifBaseType == null) {
            parseHeader();
        }
    }

    /**
     * Parse Column section of MIF file header.
     *
     * @param count Expected number of columns.
     */
    private void parseColumns(final int count) throws DataStoreException {
        FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(mifName+"_properties");

        // Check the attributes
        final List<String> cols = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            mifScanner.nextLine();
            final String attName = mifScanner.findInLine(ALPHA_PATTERN);
            final String tmpType = mifScanner.findInLine(ALPHA_PATTERN);
            // Since scanner doesn't move if no matching pattern is found, we can test only the second string.
            if (tmpType == null) {
                throw new DataStoreException("A problem occured while reading columns tag from .MIF header.");
            }
            final Class binding = MIFUtils.getColumnJavaType(tmpType);
            if (binding == null) {
                throw new DataStoreException(
                        "The typename " + tmpType + "(from " + attName + " attribute) is an unknown attribute type.");
            }
            /** todo : instantiate filters for String & Double type (length limitations). */
            ftb.addAttribute(binding).setName(attName);
            cols.add(attName);
        }

        midType = ftb.build();

        ftb = new FeatureTypeBuilder();
        ftb.setName(mifName);
        ftb.setSuperTypes(midType);
        ftb.addAttribute(Geometry.class)
                .setName("geometry")
                .setCRS(mifCRS)
                .addRole(AttributeRole.DEFAULT_GEOMETRY);

        mifBaseType = ftb.build();
    }

    /**
     * delete the MIF/MID files currently pointed by this manager.
     *
     * @return true if the files have successfully been deleted, false otherwise.
     */
    private boolean delete() throws DataStoreException {
        int deleteCounter = 0;

        RWLock.writeLock().lock();
        try {
            File mifFile = new File(mifPath);
            File midFile = new File(midPath);

            if (mifFile.exists()) {
                if (mifFile.delete()) {
                    deleteCounter++;
                }
            } else {
                deleteCounter++;
            }

            if (midFile.exists()) {
                if (midFile.delete()) {
                    deleteCounter++;
                }
            } else {
                deleteCounter++;
            }

        } catch (Exception ex) {
            throw new DataStoreException("MIF/MID data files can't be removed.", ex);
        } finally {
            RWLock.writeLock().unlock();
        }

        return (deleteCounter > 1);
    }

    /**
     * Write the MIF file header(Version, MID columns and other stuff).
     *
     * @return MIF formatted header.
     * @throws DataStoreException If the current FeatureType is not fully compliant with MIF constraints. If there's a
     * problem while writing the featureType in MIF header.
     */
    private String buildHeader() throws DataStoreException {
        if (midType == null) {
            throw new DataStoreException("No schema has been created yet !");
        }

        final StringBuilder headBuilder = new StringBuilder();
        try {
            headBuilder.append(MIFUtils.HeaderCategory.VERSION).append(' ').append(mifVersion).append('\n');
            headBuilder.append(MIFUtils.HeaderCategory.CHARSET).append(' ').append(mifCharset).append('\n');
            headBuilder.append(MIFUtils.HeaderCategory.DELIMITER).append(' ').append('\"').append(mifDelimiter).append('\"').append('\n');

            if (mifCRS != null && mifCRS != CommonCRS.WGS84.normalizedGeographic()) {
                String strCRS = ProjectionUtils.crsToMIFSyntax(mifCRS);
                if (!strCRS.isEmpty()) {
                    headBuilder.append(strCRS).append('\n');
                } else {
                    throw new DataStoreException("Given CRS can't be written in MIF file.");
                }
            }

            headBuilder.append(MIFUtils.HeaderCategory.COLUMNS).append(' ').append(midType.getProperties(true).size()).append('\n');
            MIFUtils.featureTypeToMIFSyntax(midType, headBuilder);

            headBuilder.append(MIFUtils.HeaderCategory.DATA).append('\n');

        } catch (FactoryException e) {
            throw new DataStoreException("Datastore can't write MIF file header.", e);
        }

        return headBuilder.toString();
    }

    private void flushHeader() throws DataStoreException, IOException {
        // Cache the header in memory.
        final String head = buildHeader();

        RWLock.writeLock().lock();
        try (OutputStream out = IOUtilities.openWrite(mifPath, CREATE, WRITE, TRUNCATE_EXISTING);
                final OutputStreamWriter stream = new OutputStreamWriter(out, getCharset())) {
            // writing MIF header and geometries.
            stream.write(head);
        } finally {
            RWLock.writeLock().unlock();
        }
    }

    /**
     * When opening MIF file in writing mode, we write all data in tmp file. This function is used for writing tmp file
     * data at the end of the the real file.
     * @param mifToFlush Geometries to append in MIF file.
     * @param midToFlush properties to append in MID file.
     * @throws java.io.IOException If an error occurs while replacing files.
     */
    public void flushData(final Path mifToFlush, final Path midToFlush) throws IOException {
        RWLock.writeLock().lock();
        try {
            // writing MIF header and geometries.
            try (final OutputStream out = IOUtilities.openWrite(mifPath, CREATE, WRITE, APPEND)) {
                // writing MIF header and geometries.
                Files.copy(mifToFlush, out);
            }

            // MID writing
            try (final OutputStream out = IOUtilities.openWrite(midPath, CREATE, WRITE, APPEND)) {
                Files.copy(midToFlush, out);
            }

        } finally {
            RWLock.writeLock().unlock();
        }
    }

    /**
     * Build a string representation of the given feature attributes for MID file writing.
     * @param toParse The feature to convert into MID syntax.
     * @return A string representation of the given feature. Never null, but empty string is possible.
     */
    public String buildMIDAttributes(Feature toParse) {
        final FeatureType fType = toParse.getType();

        if(midType.isAssignableFrom(fType)) {
            return midType.getProperties(true).stream()
                    .map(PropertyType::getName)
                    .map(Object::toString)
                    .map(toParse::getPropertyValue)
                    .map(MIFUtils::getStringValue)
                    .collect(Collectors.joining(String.valueOf(mifDelimiter), "", "\n"));
        }

        return "";
    }

    /**
     * {@inheritDoc}
     */
    public void refreshMetaModel() throws IllegalNameException {
        midType = null;
        mifBaseType = null;
    }

    public void setDelimiter(char delimiter) {
        this.mifDelimiter = delimiter;
    }

    public CoordinateReferenceSystem getWrittenCRS() {
        return writtenCRS;
    }

    public Charset getCharset() {
        try {
            return Charset.forName(mifCharset);
        } catch (NullPointerException | IllegalArgumentException e) {
            return StandardCharsets.ISO_8859_1;
        }
    }
}
