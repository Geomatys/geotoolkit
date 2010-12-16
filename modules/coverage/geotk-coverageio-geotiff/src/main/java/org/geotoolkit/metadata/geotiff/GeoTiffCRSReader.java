/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
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
/*
 * NOTICE OF RELEASE TO THE PUBLIC DOMAIN
 *
 * This work was created by employees of the USDA Forest Service's
 * Fire Science Lab for internal use.  It is therefore ineligible for
 * copyright under title 17, section 105 of the United States Code.  You
 * may treat it as you would treat any public domain work: it may be used,
 * changed, copied, or redistributed, with or without permission of the
 * authors, for free or for compensation.  You may not claim exclusive
 * ownership of this code because it is already owned by everyone.  Use this
 * software entirely at your own risk.  No warranty of any kind is given.
 *
 * A copy of 17-USC-105 should have accompanied this distribution in the file
 * 17USC105.html.  If not, you may access the law via the US Government's
 * public websites:
 *   - http://www.copyright.gov/title17/92chap1.html#105
 *   - http://www.gpoaccess.gov/uscode/  (enter "17USC105" in the search box.)
 */

package org.geotoolkit.metadata.geotiff;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import javax.measure.unit.SI;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;

import org.geotoolkit.referencing.factory.ReferencingFactoryContainer;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.image.io.metadata.ReferencingBuilder;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.referencing.cs.DefaultCartesianCS;
import org.geotoolkit.referencing.cs.DefaultCoordinateSystemAxis;
import org.geotoolkit.referencing.cs.DefaultEllipsoidalCS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.crs.DefaultProjectedCRS;
import org.geotoolkit.referencing.datum.DefaultEllipsoid;
import org.geotoolkit.referencing.datum.DefaultGeodeticDatum;
import org.geotoolkit.referencing.datum.DefaultPrimeMeridian;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.factory.AllAuthoritiesFactory;
import org.geotoolkit.referencing.operation.DefiningConversion;
import org.geotoolkit.resources.Vocabulary;

import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.datum.PrimeMeridian;
import org.opengis.referencing.datum.DatumFactory;
import org.opengis.referencing.datum.GeodeticDatum;
import org.opengis.util.FactoryException;
import org.opengis.util.NoSuchIdentifierException;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.cs.CSFactory;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.Conversion;
import org.opengis.referencing.crs.ProjectedCRS;

import static org.geotoolkit.metadata.geotiff.GeoTiffConstants.*;
import static org.geotoolkit.metadata.geotiff.GeoTiffMetaDataReader.*;

/**
 * TODO this class must be rewritten, redundant code is used here and all geotiff
 * crs codes are not supported.
 *
 * @author Bryce Nordgren / USDA Forest Service
 * @author Simone Giannecchini
 * @author Daniele Romagnoli
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
final class GeoTiffCRSReader {

    /**
     * Cached {@link MathTransformFactory} for building {@link MathTransform}
     * objects.
     */
    private final static MathTransformFactory mtFactory = FactoryFinder.getMathTransformFactory(null);

    /** Default hints for axis order management */
    private static final Hints DEFAULT_HINTS = new Hints(
        Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);


    /** Default factories for various purposes. */
    private static final AllAuthoritiesFactory DEFAULT_ALLAUTHORITIES_FACTORY =
            AllAuthoritiesFactory.getInstance(DEFAULT_HINTS);

    /** EPSG factories for various purposes. */
    private final AllAuthoritiesFactory allAuthoritiesFactory;

    /** EPSG Factory for creating {@link GeodeticDatum}objects. */
    private final DatumFactory datumObjFactory;
    /** CRS Factory for creating CRS objects. */
    private final CRSFactory crsFactory;

    /** Group Factory for creating {@link ProjectedCRS} objects. */
    private final ReferencingFactoryContainer factories;

    /** CS Factory for creating {@link CoordinateSystem} objects. */
    private final CSFactory csFactory;

    /**
     * {@link Hints} to control the creation of the factories for this
     * {@link GeoTiffMetadata2CRSAdapter} object.
     */
    private Hints hints;


    public GeoTiffCRSReader(Hints hints){
        final Hints tempHints = hints != null ? new Hints(hints) : DEFAULT_HINTS;

        this.hints = (Hints) tempHints.clone();
        allAuthoritiesFactory = hints != null ?  AllAuthoritiesFactory.getInstance(this.hints):
            DEFAULT_ALLAUTHORITIES_FACTORY;

        // factory = new ThreadedEpsgFactory(hints);
        datumObjFactory = FactoryFinder.getDatumFactory(this.hints);
        crsFactory = FactoryFinder.getCRSFactory(this.hints);
        csFactory = FactoryFinder.getCSFactory(this.hints);
        tempHints.put(Hints.DATUM_AUTHORITY_FACTORY, allAuthoritiesFactory);
        tempHints.put(Hints.CS_FACTORY, csFactory);
        tempHints.put(Hints.CRS_FACTORY, crsFactory);
        tempHints.put(Hints.MATH_TRANSFORM_FACTORY, mtFactory);
        factories = ReferencingFactoryContainer.instance(tempHints);
    }

    /**
     * Fill the CRS metadatas with the values available in the geotiff tags.
     */
    public void fillCRSMetaDatas(SpatialMetadata metadatas, ValueMap entries) throws IOException, FactoryException{

        final Object type = entries.get(GTModelTypeGeoKey);

        if(type == null){
            throw new IOException("GTModelTypeGeoKey is not defined in tags.");
        }

        switch( (Integer)type ){
            case ModelTypeProjected:  fillProjectedCRSMetaDatas(metadatas,entries);break;
            case ModelTypeGeographic: fillGeographicCRSMetaDatas(metadatas,entries);break;
            case ModelTypeGeocentric: fillGeocentricCRSMetaDatas(metadatas,entries);break;
            default: throw new IOException("Unexpected crs model type : "+(Integer)type);
        }
    }

    /**
     * Fill a projected CRS metadatas with the values available in the geotiff tags.
     */
    private void fillProjectedCRSMetaDatas(SpatialMetadata metadatas, ValueMap entries) throws IOException, FactoryException {

        final ReferencingBuilder rb = new ReferencingBuilder(metadatas);
        final CoordinateReferenceSystem crs;

        // //
        // Get the projection reference system code in case we have one by
        // lookig for the ProjectedCSTypeGeoKey key
        // //
        String tempCode = entries.getAsString(ProjectedCSTypeGeoKey);
        if (tempCode == null) {
            tempCode = "unnamed";
        }
        final StringBuffer projCode = new StringBuffer(tempCode.trim().intern());

        // //
        // getting the linear unit used by this coordinate reference system
        // since we will use it anyway.
        // //
        Unit linearUnit;
        try {
            linearUnit = createUnit(ProjLinearUnitsGeoKey,
                    ProjLinearUnitSizeGeoKey, SI.METRE,
                    SI.METRE, entries);
        } catch (IOException e) {
            linearUnit = null;
        }
        // //
        // if it's user defined, there's a lot of work to do, we have to parse
        // many information.
        // //
        if (tempCode.equalsIgnoreCase("unnamed")
                || tempCode.equals(GTUserDefinedGeoKey_String)) {
            crs = createUserDefinedPCS(entries, linearUnit);

        }else{
            // //
            // if it's not user defined, just use the EPSG factory to create the
            // coordinate system
            // //
            try {
                if (!tempCode.startsWith("EPSG") && !tempCode.startsWith("epsg")) {
                    projCode.insert(0, "EPSG:");
                }
                // it is an EPSG crs let's create it.
                final ProjectedCRS pcrs = (ProjectedCRS) CRS.decode(projCode.toString(), true);
                // //
                // We have nothing to do with the unit of measure
                // //
                if (linearUnit == null || linearUnit.equals(pcrs.getCoordinateSystem().getAxis(0).getUnit())) {
                    crs = pcrs;
                }else{
                    // //
                    // Creating anew projected CRS
                    // //
                    crs = new DefaultProjectedCRS(
                            java.util.Collections.singletonMap("name",DefaultEllipsoidalCS.getName(pcrs, new DefaultCitation("EPSG"))),
                            pcrs.getConversionFromBase(),
                            (GeographicCRS) pcrs.getBaseCRS(),
                            pcrs.getConversionFromBase().getMathTransform(),
                            createProjectedCS(linearUnit));
                }
            } catch (FactoryException fe) {
                throw new IOException(fe);
            }
        }

        rb.setCoordinateReferenceSystem(crs);
    }

    /**
     * Fill a geographic CRS metadatas with the values available in the geotiff tags.
     */
    private void fillGeographicCRSMetaDatas(SpatialMetadata metadatas, ValueMap entries) throws IOException, FactoryException{
        GeographicCRS gcs = null;

        // ////////////////////////////////////////////////////////////////////
        // Get the crs code
        // ////////////////////////////////////////////////////////////////////
        final String tempCode = entries.getAsString(GeographicTypeGeoKey);
        // lookup the angular units used in this geotiff image
        Unit angularUnit = null;
        try {
            angularUnit = createUnit(GeogAngularUnitsGeoKey,
                    GeogAngularUnitSizeGeoKey, SI.RADIAN,
                    NonSI.DEGREE_ANGLE, entries);
        } catch (IOException e) {
            angularUnit = null;
        }
        // linear unit
        Unit linearUnit = null;
        try {
            linearUnit = createUnit(GeogLinearUnitsGeoKey,
                    GeogLinearUnitSizeGeoKey, SI.METRE,
                    SI.METRE, entries);
        } catch (IOException e) {
            linearUnit = null;
        }
        // if it's user defined, there's a lot of work to do
        if (tempCode == null
                || tempCode.equals(GeoTiffConstants.GTUserDefinedGeoKey_String)) {
            // ////////////////////////////////////////////////////////////////////
            // it is user-defined we have to parse a lot of information in order
            // to built it.
            // ////////////////////////////////////////////////////////////////////
            gcs = createUserDefinedGCS(entries, linearUnit, angularUnit);

        } else {
            try {

                // ////////////////////////////////////////////////////////////////////
                // If it's not user defined, just use the EPSG factory to create
                // the coordinate system but check if the user specified a
                // different angular unit. In this case we need to create a
                // user-defined GCRS.
                // ////////////////////////////////////////////////////////////////////
                final StringBuffer geogCode = new StringBuffer(tempCode);
                if (!tempCode.startsWith("EPSG") && !tempCode.startsWith("epsg")) {
                    geogCode.insert(0, "EPSG:");
                }
                gcs = (GeographicCRS) CRS.decode(geogCode.toString(), true);
                if (angularUnit != null
                        && !angularUnit.equals(gcs.getCoordinateSystem().getAxis(0).getUnit())) {
                    // //
                    // Create a user-defined GCRS using the provided angular
                    // unit.
                    // //
                    gcs = new DefaultGeographicCRS(DefaultEllipsoidalCS.getName(gcs, new DefaultCitation("EPSG")),
                            (GeodeticDatum) gcs.getDatum(),
                            DefaultEllipsoidalCS.GEODETIC_2D.usingUnit(angularUnit));
                }
            } catch (FactoryException ex) {
                throw new IOException(ex);
            }
        }

        ReferencingBuilder rb = new ReferencingBuilder(metadatas);
        rb.setCoordinateReferenceSystem(gcs);
    }

    /**
     * Fill a geocentric CRS metadatas with the values available in the geotiff tags.
     */
    private void fillGeocentricCRSMetaDatas(SpatialMetadata metadatas, ValueMap entries) throws IOException{
        throw new IOException("Not done yet.");
    }

    /**
     * The GeoTIFFWritingUtilities spec requires that a user defined GCS be
     * comprised of the following:
     *
     * <ul>
     * <li> a citation </li>
     * <li> a datum definition </li>
     * <li> a prime meridian definition (if not Greenwich) </li>
     * <li> an angular unit definition (if not degrees) </li>
     * </ul>
     *
     * @param metadata to use for building this {@link GeographicCRS}.
     * @param linearUnit
     * @param angularUnit
     * @return a {@link GeographicCRS}.
     *
     * @throws IOException
     */
    private GeographicCRS createUserDefinedGCS(final ValueMap metadata, Unit linearUnit,
            Unit angularUnit) throws IOException, FactoryException {
        // //
        // coordinate reference system name (GeogCitationGeoKey)
        // //
        String name = metadata.getAsString(GeogCitationGeoKey);
        if (name == null) {
            name = "unnamed";
        }

        // lookup the Geodetic datum
        final GeodeticDatum datum = createGeodeticDatum(linearUnit, metadata);

        // coordinate reference system
        GeographicCRS gcs = null;

        // property map is reused
        final Map props = new HashMap();
        // make the user defined GCS from all the components...
        props.put("name", name);
        return crsFactory.createGeographicCRS(props, datum,
                DefaultEllipsoidalCS.GEODETIC_2D.usingUnit(angularUnit));
    }

    /**
     * We have a user defined {@link ProjectedCRS}, let's try to parse it.
     *
     * @param linearUnit
     *            is the UoM that this {@link ProjectedCRS} will use. It could
     *            be null.
     *
     * @return a user-defined {@link ProjectedCRS}.
     * @throws IOException
     * @throws FactoryException
     */
    private ProjectedCRS createUserDefinedPCS(
            final ValueMap metadata, Unit linearUnit)
            throws IOException, FactoryException {

        // /////////////////////////////////////////////////////////////////
        // At the top level a user-defined PCRS is made by
        // <ol>
        // <li>PCSCitationGeoKey (NAME)
        // <li>ProjectionGeoKey
        // <li>GeographicTypeGeoKey
        // </ol>
        // /////////////////////////////////////////////////////////////////
        // //
        // NAME of the user defined projected coordinate reference system.
        // //
        String projectedCrsName = metadata.getAsString(PCSCitationGeoKey);
        if (projectedCrsName == null) {
            projectedCrsName = "unnamed".intern();
        }

        // /////////////////////////////////////////////////////////////////////
        // PROJECTION geo key for this projected coordinate reference system.
        // get the projection code for this PCRS to build it from the GCS.
        //
        // In case i is user defined it requires:
        // PCSCitationGeoKey
        // ProjCoordTransGeoKey
        // ProjLinearUnitsGeoKey
        // /////////////////////////////////////////////////////////////////////
        final String projCode = metadata.getAsString(ProjectionGeoKey);
        boolean projUserDefined = false;
        if (projCode == null
                || projCode.equals(GeoTiffConstants.GTUserDefinedGeoKey_String)) {
            projUserDefined = true;
        }

        // is it user defined?
        Conversion projection = null;
        final ParameterValueGroup parameters;
        if (projUserDefined) {
            // /////////////////////////////////////////////////////////////////
            // A user defined projection is made up by
            // <ol>
            // <li>PCSCitationGeoKey (NAME)
            // <li>ProjCoordTransGeoKey
            // <li>ProjLinearUnitsGeoKey
            // </ol>
            // /////////////////////////////////////////////////////////////////
            // NAME of this projection coordinate transformation
            // getting user defined parameters
            String projectionName = metadata.getAsString(PCSCitationGeoKey);
            if (projectionName == null) {
                projectionName = "unnamed";
            }

            // //
            // getting default parameters for this projection and filling them
            // with the values found
            // inside the geokeys list.
            // //
            parameters = createUserDefinedProjectionParameter(projectionName,metadata);
            if (parameters == null) {
                throw new IOException("Projection is not supported.");
            }

            projection = new DefiningConversion(projectionName, parameters);

        } else {
            parameters = null;
            projection = (Conversion) allAuthoritiesFactory.createCoordinateOperation("EPSG:"+projCode);

        }

        // /////////////////////////////////////////////////////////////////////
        // GEOGRAPHIC CRS
        // /////////////////////////////////////////////////////////////////////
        final GeographicCRS gcs = createGeographicCoordinateSystem(metadata);

        // was the projection user defined?
        // in such case we need to set the remaining parameters.
        if (projUserDefined) {
            final GeodeticDatum tempDatum = ((GeodeticDatum) gcs.getDatum());
            final DefaultEllipsoid tempEll = (DefaultEllipsoid) tempDatum.getEllipsoid();
            double inverseFlattening = tempEll.getInverseFlattening();
            double semiMajorAxis = tempEll.getSemiMajorAxis();
            // setting missing parameters
            parameters.parameter("semi_minor").setValue(
                    semiMajorAxis * (1 - (1 / inverseFlattening)));
            parameters.parameter("semi_major").setValue(semiMajorAxis);

        }

        // /////////////////////////////////////////////////////////////////////
        // PROJECTED CRS
        // /////////////////////////////////////////////////////////////////////
        // //
        //
        // I am putting particular attention on the management of the unit
        // of measure since it seems that very often people change the unit
        // of measure to feet even if the standard UoM for the request
        // projection is M.
        //
        // ///
        if (projUserDefined) {            
            DefaultCartesianCS cs = DefaultCartesianCS.PROJECTED;
            if(linearUnit != null && !linearUnit.equals(SI.METRE)){
                cs = cs.usingUnit(linearUnit);
            }
            
            return this.factories.getCRSFactory().createProjectedCRS(
                    Collections.singletonMap("name", projectedCrsName),
                    gcs, projection, cs);
        }
        // standard projection
        if (linearUnit != null && !linearUnit.equals(SI.METRE)) {
            return factories.getCRSFactory().createProjectedCRS(Collections.singletonMap(
                    "name", projectedCrsName), gcs, projection,
                    DefaultCartesianCS.PROJECTED.usingUnit(linearUnit));
        }
        return factories.getCRSFactory().createProjectedCRS(Collections.singletonMap("name",
                projectedCrsName), gcs, projection,
                DefaultCartesianCS.PROJECTED);
    }


    ////////////////////////////////////////////////////////////////////////////
    // UTILS ///////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Retrieve the scale factor parameter as defined by the geotiff
     * specification.
     *
     * @param metadata to use for searching the scale factor.
     * @return the scale factor
     */
    private double getScaleFactor(final ValueMap metadata) {
        String scale = metadata.getAsString(ProjScaleAtCenterGeoKey);
        if (scale == null) {
            scale = metadata.getAsString(ProjScaleAtNatOriginGeoKey);
        }
        if (scale == null) {
            return 1.0;
        }
        return Double.parseDouble(scale);
    }

    /**
     * Getting the false easting with a minimum of tolerance with respect to the
     * parameters name. I saw that often people use the wrong geokey to store
     * the false easting, we cannot be too picky we need to get going pretty
     * smoothly.
     *
     * @param metadata to use for searching the false easting.
     * @return double False easting.
     */
    private double getFalseEasting(final ValueMap metadata) {
        String easting = metadata.getAsString(ProjFalseEastingGeoKey);
        if (easting == null) {
            easting = metadata.getAsString(ProjFalseOriginEastingGeoKey);
        }
        if (easting == null) {
            return 0.0;
        }
        return Double.parseDouble(easting);

    }

    /**
     * Getting the false northing with a minimum of tolerance with respect to
     * the parameters name. I saw that often people use the wrong geokey to
     * store the false easting, we cannot be too picky we need to get going
     * pretty smoothly.
     *
     * @param metadata to use for searching the false northing.
     * @return double False northing.
     */
    private double getFalseNorthing(final ValueMap metadata) {
        String northing = metadata.getAsString(ProjFalseNorthingGeoKey);
        if (northing == null) {
            northing = metadata.getAsString(ProjFalseOriginNorthingGeoKey);
        }
        if (northing == null) {
            return 0.0;
        }
        return Double.parseDouble(northing);

    }

    /**
     * Getting the origin long with a minimum of tolerance with respect to the
     * parameters name. I saw that often people use the wrong geokey to store
     * the false easting, we cannot be too picky we need to get going pretty
     * smoothly.
     *
     * @param metadata
     *            to use for searching the originating longitude.
     * @return double origin longitude.
     */
    private double getOriginLong(final ValueMap metadata) {
        String origin = metadata.getAsString(ProjCenterLongGeoKey);
        if (origin == null) {
            origin = metadata.getAsString(ProjNatOriginLongGeoKey);
        }
        if (origin == null) {
            origin = metadata.getAsString(ProjFalseOriginLongGeoKey);
        }
        if (origin == null) {
            origin = metadata.getAsString(ProjFalseNorthingGeoKey);
        }
        if (origin == null) {
            return 0.0;
        }
        return Double.parseDouble(origin);
    }

    /**
     * Getting the origin lat with a minimum of tolerance with respect to the
     * parameters name. I saw that often people use the wrong geokey to store
     * the false easting, we cannot be too picky we need to get going pretty
     * smoothly.
     *
     * @param metadata to use for searching the origin latitude.
     * @return double origin latitude.
     */
    private double getOriginLat(final ValueMap metadata) {
        String origin = metadata.getAsString(ProjCenterLatGeoKey);
        if (origin == null) {
            origin = metadata.getAsString(ProjNatOriginLatGeoKey);
        }
        if (origin == null) {
            origin = metadata.getAsString(ProjFalseOriginLatGeoKey);
        }
        if (origin == null) {
            return 0.0;
        }

        return Double.parseDouble(origin);
    }




    /**
     * @todo we should somehow try to to support user defined coordinate
     *       transformation even if for the moment is not so clear to me how we
     *       could achieve that since if we have no clue about the coordinate
     *       transform what we are supposed to do in order to build a
     *       conversion, guess it? How could we pick up the parameters, should
     *       look for all and then guess the right transformation?
     *
     * @param name indicates the name for the projection.
     * @param metadata to use for building this {@link ParameterValueGroup}.
     * @return a {@link ParameterValueGroup} that can be used to trigger this
     *         projection.
     * @throws IOException
     * @throws FactoryException
     */
    private ParameterValueGroup createUserDefinedProjectionParameter(
            String name, final ValueMap metadata)
            throws IOException, FactoryException {
        // //
        //
        // Trying to get the name for the coordinate transformation involved.
        //
        // ///
        final String coordTrans = metadata.getAsString(ProjCoordTransGeoKey);

        // throw descriptive exception if ProjCoordTransGeoKey not defined
        if ((coordTrans == null)
                || coordTrans.equalsIgnoreCase(GeoTiffConstants.GTUserDefinedGeoKey_String)) {
            throw new IOException(
                    "User defined projections must specify coordinate transformation code in ProjCoordTransGeoKey");
        }

        // getting math transform factory
        return setParametersForProjection(name, coordTrans, metadata);
    }

    /**
     * Set the projection parameters basing its decision on the projection name.
     * I found a complete list of projections on the geotiff website at address
     * http://www.remotesensing.org/geotiff/proj_list.
     *
     * I had no time to implement support for all of them therefore you will not
     * find all of them. If you want go ahead and add support for the missing
     * ones. I have tested this code against some geotiff files you can find on
     * the geotiff website under the ftp sample directory but I can say that
     * they are a real mess! I am respecting the specification strictly while
     * many of those fields do not! I could make this method trickier and use
     * workarounds in order to be less strict but I will not do this, since I
     * believe it is may lead us just on a very dangerous path.
     *
     *
     * @param name
     * @param metadata to use fo building this {@link ParameterValueGroup}.
     * @param coordTrans
     *            a {@link ParameterValueGroup} that can be used to trigger this
     *            projection.
     *
     * @return
     * @throws GeoTiffException
     */
    private ParameterValueGroup setParametersForProjection(String name,
            final String coordTransCode,
            final ValueMap metadata) throws IOException {
        ParameterValueGroup parameters = null;
        try {
            int code = 0;
            if (coordTransCode != null) {
                code = Integer.parseInt(coordTransCode);
            }
            if (name == null) {
                name = "unnamed";
            }

            /**
             * Transverse Mercator
             */
            if (name.equalsIgnoreCase("transverse_mercator")
                    || code == CT_TransverseMercator) {
                parameters = mtFactory.getDefaultParameters("transverse_mercator");
                parameters.parameter("central_meridian").setValue(getOriginLong(metadata));
                parameters.parameter("latitude_of_origin").setValue(getOriginLat(metadata));
                parameters.parameter("scale_factor").setValue(metadata.getAsDouble(ProjScaleAtNatOriginGeoKey));
                parameters.parameter("false_easting").setValue(getFalseEasting(metadata));
                parameters.parameter("false_northing").setValue(getFalseNorthing(metadata));
                return parameters;
            }

            /**
             * Equidistant Cylindrical - Plate Caree - Equirectangular
             */
            if (name.equalsIgnoreCase("Equidistant_Cylindrical")
                    || name.equalsIgnoreCase("Plate_Carree")
                    || name.equalsIgnoreCase("Equidistant_Cylindrical")
                    || code == CT_Equirectangular) {
                parameters = mtFactory.getDefaultParameters("Equidistant_Cylindrical");
                parameters.parameter("latitude_of_origin").setValue(getOriginLat(metadata));
                parameters.parameter("central_meridian").setValue(getOriginLong(metadata));
                parameters.parameter("false_easting").setValue(getFalseEasting(metadata));
                parameters.parameter("false_northing").setValue(getFalseNorthing(metadata));
                return parameters;
            }
            /**
             * Mercator_1SP
             * Mercator_2SP
             */
            if (name.equalsIgnoreCase("mercator_1SP")
                    || name.equalsIgnoreCase("Mercator_2SP")
                    || code == CT_Mercator) {

                final double standard_parallel_1 = metadata.getAsDouble(ProjStdParallel1GeoKey);
                boolean isMercator2SP = false;
                if (!Double.isNaN(standard_parallel_1)) {
                    parameters = mtFactory.getDefaultParameters("Mercator_2SP");
                    isMercator2SP = true;
                } else {
                    parameters = mtFactory.getDefaultParameters("Mercator_1SP");
                }

                parameters.parameter("central_meridian").setValue(getOriginLong(metadata));
                parameters.parameter("latitude_of_origin").setValue(getOriginLat(metadata));
                parameters.parameter("false_easting").setValue(getFalseEasting(metadata));
                parameters.parameter("false_northing").setValue(getFalseNorthing(metadata));
                if (isMercator2SP) {
                    parameters.parameter("standard_parallel_1").setValue(standard_parallel_1);
                } else {
                    parameters.parameter("scale_factor").setValue(getScaleFactor(metadata));
                }
                return parameters;
            }

            /**
             * Lambert_conformal_conic_1SP
             */
            if (name.equalsIgnoreCase("lambert_conformal_conic_1SP")
                    || code == CT_LambertConfConic_Helmert) {
                parameters = mtFactory.getDefaultParameters("lambert_conformal_conic_1SP");
                parameters.parameter("central_meridian").setValue(getOriginLong(metadata));
                parameters.parameter("latitude_of_origin").setValue(getOriginLat(metadata));
                parameters.parameter("scale_factor").setValue(metadata.getAsDouble(ProjScaleAtNatOriginGeoKey));
                parameters.parameter("false_easting").setValue(getFalseEasting(metadata));
                parameters.parameter("false_northing").setValue(getFalseNorthing(metadata));
                return parameters;
            }

            /**
             * Lambert_conformal_conic_2SP
             */
            if (name.equalsIgnoreCase("lambert_conformal_conic_2SP")
                    || name.equalsIgnoreCase("lambert_conformal_conic_2SP_Belgium")
                    || code == CT_LambertConfConic_2SP) {
                parameters = mtFactory.getDefaultParameters("lambert_conformal_conic_2SP");
                parameters.parameter("central_meridian").setValue(getOriginLong(metadata));
                parameters.parameter("latitude_of_origin").setValue(getOriginLat(metadata));
                parameters.parameter("standard_parallel_1").setValue(metadata.getAsDouble(ProjStdParallel1GeoKey));
                parameters.parameter("standard_parallel_2").setValue(metadata.getAsDouble(ProjStdParallel2GeoKey));
                parameters.parameter("false_easting").setValue(getFalseEasting(metadata));
                parameters.parameter("false_northing").setValue(getFalseNorthing(metadata));
                return parameters;
            }

            /**
             * Krovak
             */
            if (name.equalsIgnoreCase("Krovak")) {
                parameters = mtFactory.getDefaultParameters("Krovak");
                parameters.parameter("longitude_of_center").setValue(getOriginLong(metadata));
                parameters.parameter("latitude_of_center").setValue(getOriginLat(metadata));
                parameters.parameter("azimuth").setValue(metadata.getAsDouble(ProjStdParallel1GeoKey));
                parameters.parameter("pseudo_standard_parallel_1").setValue(metadata.getAsDouble(ProjStdParallel2GeoKey));
                parameters.parameter("scale_factor").setValue(getFalseEasting(metadata));
                return parameters;
            }

            // if (name.equalsIgnoreCase("equidistant_conic")
            // || code == GeoTiffMetadata2CRSAdapter.CT_EquidistantConic) {
            // parameters = mtFactory
            // .getDefaultParameters("equidistant_conic");
            // parameters.parameter("central_meridian").setValue(
            // getOriginLong());
            // parameters.parameter("latitude_of_origin").setValue(
            // getOriginLat());
            // parameters
            // .parameter("standard_parallel_1")
            // .setValue(
            // this
            // .getGeoKeyAsDouble(GeoTiffIIOMetadataDecoder.ProjStdParallel1GeoKey));
            // parameters
            // .parameter("standard_parallel_2")
            // .setValue(
            // this
            // .getGeoKeyAsDouble(GeoTiffIIOMetadataDecoder.ProjStdParallel2GeoKey));
            // parameters.parameter("false_easting").setValue(
            // getFalseEasting());
            // parameters.parameter("false_northing").setValue(
            // getFalseNorthing());
            //
            // return parameters;
            // }

            /**
             * STEREOGRAPHIC
             */
            if (name.equalsIgnoreCase("stereographic")
                    || code == CT_Stereographic) {
                parameters = mtFactory.getDefaultParameters("stereographic");
                parameters.parameter("central_meridian").setValue(this.getOriginLong(metadata));
                parameters.parameter("latitude_of_origin").setValue(this.getOriginLat(metadata));
                parameters.parameter("scale_factor").setValue(metadata.getAsDouble(ProjScaleAtNatOriginGeoKey));
                parameters.parameter("false_easting").setValue(getFalseEasting(metadata));
                parameters.parameter("false_northing").setValue(getFalseNorthing(metadata));
                return parameters;
            }

            /**
             * POLAR_STEREOGRAPHIC.
             */
            if (name.equalsIgnoreCase("polar_stereographic")
                    || code == CT_PolarStereographic) {
                parameters = mtFactory.getDefaultParameters("polar_stereographic");
                parameters.parameter("latitude_of_origin").setValue(this.getOriginLat(metadata));
                parameters.parameter("scale_factor").setValue(metadata.getAsDouble(ProjScaleAtNatOriginGeoKey));
                parameters.parameter("false_easting").setValue(getFalseEasting(metadata));
                parameters.parameter("false_northing").setValue(getFalseNorthing(metadata));
                parameters.parameter("central_meridian").setValue(getOriginLong(metadata));
                return parameters;
            }

            /**
             * Oblique Stereographic
             */
            if (name.equalsIgnoreCase("oblique_stereographic")
                    || code == CT_ObliqueStereographic) {
                parameters = mtFactory.getDefaultParameters("Oblique_Stereographic");
                parameters.parameter("central_meridian").setValue(getOriginLong(metadata));
                parameters.parameter("latitude_of_origin").setValue(getOriginLat(metadata));
                parameters.parameter("scale_factor").setValue(metadata.getAsDouble(ProjScaleAtNatOriginGeoKey));
                parameters.parameter("false_easting").setValue(getFalseEasting(metadata));
                parameters.parameter("false_northing").setValue(getFalseNorthing(metadata));
                return parameters;
            }

            /**
             * OBLIQUE_MERCATOR.
             */
            if (name.equalsIgnoreCase("oblique_mercator")
                    || name.equalsIgnoreCase("hotine_oblique_mercator")
                    || code == CT_ObliqueMercator) {
                parameters = mtFactory.getDefaultParameters("oblique_mercator");
                parameters.parameter("scale_factor").setValue(getScaleFactor(metadata));
                parameters.parameter("azimuth").setValue(metadata.getAsDouble(ProjAzimuthAngleGeoKey));
                parameters.parameter("false_easting").setValue(getFalseEasting(metadata));
                parameters.parameter("false_northing").setValue(getFalseNorthing(metadata));
                parameters.parameter("longitude_of_center").setValue(getOriginLong(metadata));
                parameters.parameter("latitude_of_center").setValue(getOriginLat(metadata));
                return parameters;
            }

            /**
             * albers_Conic_Equal_Area
             */
            if (name.equalsIgnoreCase("albers_Conic_Equal_Area")
                    || code == CT_AlbersEqualArea) {
                parameters = mtFactory.getDefaultParameters("Albers_Conic_Equal_Area");
                parameters.parameter("standard_parallel_1").setValue(metadata.getAsDouble(ProjStdParallel1GeoKey));
                parameters.parameter("standard_parallel_2").setValue(metadata.getAsDouble(ProjStdParallel2GeoKey));
                parameters.parameter("latitude_of_center").setValue(getOriginLat(metadata));
                parameters.parameter("longitude_of_center").setValue(getOriginLong(metadata));
                parameters.parameter("false_easting").setValue(getFalseEasting(metadata));
                parameters.parameter("false_northing").setValue(getFalseNorthing(metadata));
                return parameters;
            }

            /**
             * Orthographic
             */
            if (name.equalsIgnoreCase("Orthographic")
                    || code == CT_Orthographic) {
                parameters = mtFactory.getDefaultParameters("orthographic");
                parameters.parameter("latitude_of_origin").setValue(getOriginLat(metadata));
                parameters.parameter("longitude_of_origin").setValue(getOriginLong(metadata));
                parameters.parameter("false_easting").setValue(getFalseEasting(metadata));
                parameters.parameter("false_northing").setValue(getFalseNorthing(metadata));
                return parameters;
            }

            /**
             * Lambert Azimuthal Equal Area
             */
            if (name.equalsIgnoreCase("Lambert_Azimuthal_Equal_Area")
                    || code == CT_LambertAzimEqualArea) {
                parameters = mtFactory.getDefaultParameters("Lambert_Azimuthal_Equal_Area");
                parameters.parameter("latitude_of_center").setValue(getOriginLat(metadata));
                parameters.parameter("longitude_of_center").setValue(getOriginLong(metadata));
                parameters.parameter("false_easting").setValue(getFalseEasting(metadata));
                parameters.parameter("false_northing").setValue(getFalseNorthing(metadata));
                return parameters;
            }

            /**
             * New Zealand Map Grid
             */
            if (name.equalsIgnoreCase("New_Zealand_Map_Grid")
                    || code == CT_NewZealandMapGrid) {
                parameters = mtFactory.getDefaultParameters("New_Zealand_Map_Grid");
                parameters.parameter("latitude_of_origin").setValue(this.getOriginLat(metadata));
                parameters.parameter("central_meridian").setValue(getOriginLong(metadata));
                parameters.parameter("false_easting").setValue(getFalseEasting(metadata));
                parameters.parameter("false_northing").setValue(getFalseNorthing(metadata));
                return parameters;
            }

        } catch (NoSuchIdentifierException e) {
            throw new IOException(e.getLocalizedMessage(), e);
        }

        return parameters;
    }

    /**
     * Creation of a geographic coordinate reference system as specified in the
     * GeoTiff specification. User defined values are supported for all the
     * possible levels of the above mentioned specification.
     *
     * @param metadata
     *            to use for building a {@link GeographicCRS}.
     *
     * @return
     * @throws IOException
     */
    private GeographicCRS createGeographicCoordinateSystem(
            final ValueMap metadata) throws IOException, FactoryException {
        GeographicCRS gcs = null;

        // ////////////////////////////////////////////////////////////////////
        // Get the crs code
        // ////////////////////////////////////////////////////////////////////
        final String tempCode = metadata.getAsString(GeographicTypeGeoKey);
        // lookup the angular units used in this geotiff image
        Unit angularUnit = null;
        try {
            angularUnit = createUnit(GeogAngularUnitsGeoKey,
                    GeogAngularUnitSizeGeoKey, SI.RADIAN,
                    NonSI.DEGREE_ANGLE, metadata);
        } catch (IOException e) {
            angularUnit = null;
        }
        // linear unit
        Unit linearUnit = null;
        try {
            linearUnit = createUnit(GeogLinearUnitsGeoKey,
                    GeogLinearUnitSizeGeoKey, SI.METRE,
                    SI.METRE, metadata);
        } catch (IOException e) {
            linearUnit = null;
        }
        // if it's user defined, there's a lot of work to do
        if (tempCode == null
                || tempCode.equals(GeoTiffConstants.GTUserDefinedGeoKey_String)) {
            // ////////////////////////////////////////////////////////////////////
            // it is user-defined we have to parse a lot of information in order
            // to built it.
            // ////////////////////////////////////////////////////////////////////
            gcs = createUserDefinedGCS(metadata, linearUnit, angularUnit);

        } else {
            try {

                // ////////////////////////////////////////////////////////////////////
                // If it's not user defined, just use the EPSG factory to create
                // the coordinate system but check if the user specified a
                // different angular unit. In this case we need to create a
                // user-defined GCRS.
                // ////////////////////////////////////////////////////////////////////
                final StringBuffer geogCode = new StringBuffer(tempCode);
                if (!tempCode.startsWith("EPSG")
                        && !tempCode.startsWith("epsg")) {
                    geogCode.insert(0, "EPSG:");
                }
                gcs = (GeographicCRS) CRS.decode(geogCode.toString(), true);
                if (angularUnit != null
                        && !angularUnit.equals(gcs.getCoordinateSystem().getAxis(0).getUnit())) {
                    // //
                    // Create a user-defined GCRS using the provided angular
                    // unit.
                    // //
                    gcs = new DefaultGeographicCRS(DefaultEllipsoidalCS.getName(gcs, new DefaultCitation("EPSG")),
                            (GeodeticDatum) gcs.getDatum(),
                            DefaultEllipsoidalCS.GEODETIC_2D.usingUnit(angularUnit));
                }
            } catch (FactoryException fe) {
                throw new IOException(fe);
            }
        }

        return gcs;
    }

    /**
     * Creates a {@link CartesianCS} for a {@link ProjectedCRS} given the
     * provided {@link Unit}.
     *
     * @todo consider caching this items
     * @param linearUnit
     *            to be used for building this {@link CartesianCS}.
     * @return an instance of {@link CartesianCS} using the provided
     *         {@link Unit},
     */
    private DefaultCartesianCS createProjectedCS(Unit linearUnit) {
        if (linearUnit == null) {
            throw new NullPointerException(
                    "Error when trying to create a PCS using this linear UoM ");
        }
        if (!linearUnit.isCompatible(SI.METRE)) {
            throw new IllegalArgumentException(
                    "Error when trying to create a PCS using this linear UoM "
                    + linearUnit.toString());
        }
        return new DefaultCartesianCS(Vocabulary.formatInternational(
                Vocabulary.Keys.PROJECTED).toString(),
                new DefaultCoordinateSystemAxis(Vocabulary.formatInternational(Vocabulary.Keys.EASTING), "E",
                AxisDirection.EAST, linearUnit),
                new DefaultCoordinateSystemAxis(Vocabulary.formatInternational(Vocabulary.Keys.NORTHING), "N",
                AxisDirection.NORTH, linearUnit));
    }

    /**
     * Creating a prime meridian for the gcs we are creating at an higher level.
     * As usual this method tries to follow the geotiff specification.
     *
     * @param linearUnit
     *            to use for building this {@link PrimeMeridian}.
     * @return a {@link PrimeMeridian} built using the provided {@link Unit} and
     *         the provided metadata.
     * @throws IOException
     */
    private PrimeMeridian createPrimeMeridian(
            final ValueMap metadata, Unit linearUnit)
            throws IOException {
        // look up the prime meridian:
        // + could be an EPSG code
        // + could be user defined
        // + not defined = greenwich
        final String pmCode = metadata.getAsString(GeogPrimeMeridianGeoKey);
        PrimeMeridian pm = null;

        try {
            if (pmCode != null) {
                if (pmCode.equals(GeoTiffConstants.GTUserDefinedGeoKey_String)) {
                    try {
                        final String name = metadata.getAsString(GeogCitationGeoKey);
                        final String pmValue = metadata.getAsString(GeogPrimeMeridianLongGeoKey);
                        final double pmNumeric = Double.parseDouble(pmValue);
                        // is it Greenwich?
                        if (pmNumeric == 0) {
                            return DefaultPrimeMeridian.GREENWICH;
                        }
                        final Map props = new HashMap();
                        props.put("name", (name != null) ? name
                                : "User Defined GEOTIFF Prime Meridian");
                        pm = datumObjFactory.createPrimeMeridian(props,
                                pmNumeric, linearUnit);
                    } catch (NumberFormatException nfe) {
                        throw new IOException("Invalid user-defined prime meridian spec.",nfe);
                    }
                } else {
                    pm = allAuthoritiesFactory.createPrimeMeridian("EPSG:"+ pmCode);
                }
            } else {
                pm = DefaultPrimeMeridian.GREENWICH;
            }
        } catch (FactoryException fe) {
            throw new IOException(fe);
        }

        return pm;
    }

    /**
     * Looks up the Geodetic Datum as specified in the GeoTIFFWritingUtilities
     * file. The geotools definition of the geodetic datum includes both an
     * ellipsoid and a prime meridian, but the code in the
     * GeoTIFFWritingUtilities file does NOT include the prime meridian, as it
     * is specified separately. This code currently does not support user
     * defined datum.
     *
     * @param unit to use for building this {@link GeodeticDatum}.
     * @return a {@link GeodeticDatum}.
     * @throws IOException
     * @throws GeoTiffException
     *
     */
    private GeodeticDatum createGeodeticDatum(final Unit unit,
            final ValueMap metadata) throws IOException {
        // lookup the datum (w/o PrimeMeridian), error if "user defined"
        GeodeticDatum datum = null;
        final String datumCode = metadata.getAsString(GeogGeodeticDatumGeoKey);

        if (datumCode == null) {
            throw new IOException("A user defined Geographic Coordinate system must include a predefined datum!");
        }

        if (datumCode.equals(GeoTiffConstants.GTUserDefinedGeoKey_String)) {
            /**
             * USER DEFINED DATUM
             */
            // datum name
            final String datumName = (metadata.getAsString(GeogCitationGeoKey) != null ? metadata.getAsString(GeogCitationGeoKey)
                    : "unnamed");

            // is it WGS84?
            if (datumName.trim().equalsIgnoreCase("WGS84")) {
                return DefaultGeodeticDatum.WGS84;
            }

            // ELLIPSOID
            final Ellipsoid ellipsoid = createEllipsoid(unit, metadata);

            // PRIME MERIDIAN
            // lookup the Prime Meridian.
            final PrimeMeridian primeMeridian = createPrimeMeridian(metadata,
                    unit);

            // DATUM
            datum = new DefaultGeodeticDatum(datumName, ellipsoid,
                    primeMeridian);
        } else {
            /**
             * NOT USER DEFINED DATUM
             */
            // we are going to use the provided EPSG code
            try {
                datum = (GeodeticDatum) (allAuthoritiesFactory.createDatum("EPSG:"+datumCode));
            } catch (FactoryException fe) {
                throw new IOException(fe.getLocalizedMessage(), fe);
            } catch (ClassCastException cce) {
                throw new IOException(cce.getLocalizedMessage(), cce);
            }
        }

        return datum;
    }

    /**
     * Creating an ellipsoid following the GeoTiff spec.
     *
     * @param unit to build this {@link Ellipsoid}..
     * @return an {@link Ellipsoid}.
     * @throws GeoTiffException
     */
    private Ellipsoid createEllipsoid(final Unit unit,
            final ValueMap metadata) throws IOException {
        // /////////////////////////////////////////////////////////////////////
        // Getting the ellipsoid key in order to understand if we are working
        // against a common ellipsoid or a user defined one.
        // /////////////////////////////////////////////////////////////////////
        // ellipsoid key
        final String ellipsoidKey = metadata.getAsString(GeogEllipsoidGeoKey);
        String temp = null;
        // is the ellipsoid user defined?
        if (ellipsoidKey.equalsIgnoreCase(GeoTiffConstants.GTUserDefinedGeoKey_String)) {
            // /////////////////////////////////////////////////////////////////////
            // USER DEFINED ELLIPSOID
            // /////////////////////////////////////////////////////////////////////
            String nameEllipsoid = metadata.getAsString(GeogCitationGeoKey);
            if (nameEllipsoid == null) {
                nameEllipsoid = "unnamed";
            }
            // is it the default for WGS84?
            if (nameEllipsoid.trim().equalsIgnoreCase("WGS84")) {
                return DefaultEllipsoid.WGS84;
            }

            // //
            // It is worth to point out that I ALWAYS use the inverse flattening
            // along with the semi-major axis to builde the Flattened Sphere.
            // This
            // has to be done in order to comply with the opposite process of
            // going from CRS to metadata where this coupls is always used.
            // //
            // getting temporary parameters
            temp = metadata.getAsString(GeogSemiMajorAxisGeoKey);
            final double semiMajorAxis = (temp != null ? Double.parseDouble(temp) : Double.NaN);
            temp = metadata.getAsString(GeogInvFlatteningGeoKey);
            final double inverseFlattening;
            if (temp != null) {
                inverseFlattening = (temp != null ? Double.parseDouble(temp)
                        : Double.NaN);
            } else {
                temp = metadata.getAsString(GeogSemiMinorAxisGeoKey);
                final double semiMinorAxis = (temp != null ? Double.parseDouble(temp) : Double.NaN);
                inverseFlattening = semiMajorAxis
                        / (semiMajorAxis - semiMinorAxis);

            }
            // look for the Ellipsoid first then build the datum
            return DefaultEllipsoid.createFlattenedSphere(nameEllipsoid,
                    semiMajorAxis, inverseFlattening, unit);
        }

        try {
            // /////////////////////////////////////////////////////////////////////
            // EPSG STANDARD ELLIPSOID
            // /////////////////////////////////////////////////////////////////////
            return allAuthoritiesFactory.createEllipsoid("EPSG:"+ellipsoidKey);
        } catch (FactoryException fe) {
            throw new IOException(fe.getLocalizedMessage(), fe);
        }
    }

    /**
     * This code creates an <code>javax.Units.Unit</code> object out of the
     * <code>ProjLinearUnitsGeoKey</code> and the
     * <code>ProjLinearUnitSizeGeoKey</code>. The unit may either be
     * specified as a standard EPSG recognized unit, or may be user defined.
     *
     * @param key
     * @param userDefinedKey
     * @param base
     * @param def
     * @return <code>Unit</code> object representative of the tags in the file.
     * @throws IOException
     *             if the<code>ProjLinearUnitsGeoKey</code> is not specified
     *             or if unit is user defined and
     *             <code>ProjLinearUnitSizeGeoKey</code> is either not defined
     *             or does not contain a number.
     */
    private Unit createUnit(int key, int userDefinedKey, Unit base, Unit def,
            final ValueMap metadata) throws IOException {
        final String unitCode = metadata.getAsString(key);

        // if not defined, return the default unit of measure
        if (unitCode == null) {
            return def;
        }

        // if specified, retrieve the appropriate unit code. There are two case
        // to keep into account, first case is when the unit of measure has an
        // EPSG code, alternatively it can be instantiated as a conversion from
        // meter.
        if (unitCode.equals(GeoTiffConstants.GTUserDefinedGeoKey_String)) {
            try {
                final String unitSize = metadata.getAsString(userDefinedKey);

                // throw descriptive exception if required key is not there.
                if (unitSize == null) {
                    throw new IOException("Must define unit length when using a user defined unit");
                }

                double sz = Double.parseDouble(unitSize);
                return base.times(sz);
            } catch (NumberFormatException nfe) {
                throw new IOException(nfe);
            }
        } else {
            try {
                // using epsg code for this unit
                return allAuthoritiesFactory.createUnit("EPSG:"+unitCode);
            } catch (FactoryException fe) {
                throw new IOException(fe);
            }
        }
    }

}
