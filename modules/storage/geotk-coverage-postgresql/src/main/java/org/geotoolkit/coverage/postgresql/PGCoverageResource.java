/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012 - 2013, Geomatys
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
package org.geotoolkit.coverage.postgresql;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.logging.Level;
import javax.measure.Unit;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.operation.transform.TransferFunction;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.coverage.Category;
import org.apache.sis.coverage.SampleDimension;
import org.geotoolkit.coverage.SampleDimensionUtils;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.coverage.postgresql.epsg.PGEPSGWriter;
import org.geotoolkit.coverage.wkb.WKBRasterConstants;
import org.geotoolkit.data.multires.MultiResolutionModel;
import org.geotoolkit.data.multires.Pyramid;
import org.geotoolkit.data.multires.Pyramids;
import org.geotoolkit.storage.coverage.AbstractPyramidalCoverageResource;
import org.geotoolkit.storage.coverage.CoverageStoreContentEvent;
import org.geotoolkit.storage.coverage.CoverageStoreManagementEvent;
import org.geotoolkit.storage.coverage.PyramidalModelWriter;
import org.geotoolkit.temporal.object.TemporalUtilities;
import org.geotoolkit.util.StringUtilities;
import org.geotoolkit.version.Version;
import org.geotoolkit.coverage.SampleDimensionType;
import org.opengis.metadata.content.TransferFunctionType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 */
public class PGCoverageResource extends AbstractPyramidalCoverageResource {

    private final PGCoverageStore pgstore;
    private boolean updated = false;
    private final List<Pyramid> pyramids = new ArrayList<Pyramid>();
    final Version version;

    public PGCoverageResource(final PGCoverageStore store, final GenericName name, Version version) {
        super(store,name);
        this.pgstore = store;
        this.version = version;
    }

    void mustUpdate(){
        updated = false;
    }

    public synchronized Collection<Pyramid> getPyramids() {
        updateModel();
        return pyramids;
    }

    /**
     * Explore pyramids and rebuild model
     */
    private synchronized void updateModel(){
        if (updated) return;
        pyramids.clear();

        Connection cnx = null;
        Statement stmt = null;
        ResultSet rs = null;
        try{
            cnx = pgstore.getDataSource().getConnection();
            stmt = cnx.createStatement();
            final int layerId = pgstore.getLayerId(cnx,getIdentifier().tip().toString());

            final StringBuilder query = new StringBuilder();
            query.append("SELECT p.id, p.epsg, pp.value FROM ");
            query.append(pgstore.encodeTableName("Pyramid"));
            query.append(" as p ");
            query.append("LEFT OUTER JOIN ");
            query.append(pgstore.encodeTableName("PyramidProperty"));
            query.append(" AS pp ON pp.\"pyramidId\" = p.id");
            query.append(" WHERE p.\"layerId\" = ");
            query.append(layerId);
            query.append(" AND (pp.key IS NULL OR pp.key = 'version')"); //grab version
            if(version==null || version.getLabel().equals(PGVersionControl.UNSET)){
                query.append(" AND pp.value IS NULL");
            }else{
                query.append(" AND pp.value = '");
                query.append(TemporalUtilities.toISO8601Z(version.getDate(), PGVersionControl.GMT0));
                query.append("'");
            }

            final Map<Integer,String> map = new HashMap<Integer, String>();
            rs = stmt.executeQuery(query.toString());
            while(rs.next()){
                map.put(rs.getInt(1),rs.getString(2));
            }
            pgstore.closeSafe(null,stmt,rs);
            for(Map.Entry<Integer,String> entry : map.entrySet()){
                pyramids.add(readPyramid(cnx, entry.getKey(), entry.getValue()));
            }

        }catch(SQLException ex){
            pgstore.getLogger().log(Level.WARNING, ex.getMessage(),ex);
        }catch(FactoryException ex){
            pgstore.getLogger().log(Level.WARNING, ex.getMessage(),ex);
        }finally{
            pgstore.closeSafe(cnx, stmt, rs);
        }

        updated = true;
    }

    private PGPyramid readPyramid(final Connection cnx, final int pyramidId, final String epsgcode) throws SQLException, FactoryException{

        final PGCoverageStore store = getOriginator();
        final CoordinateReferenceSystem crs = store.getEPSGFactory().createCoordinateReferenceSystem(epsgcode);
        PGPyramid pyramid = new PGPyramid(this, pyramidId, crs);

        Statement stmt = null;
        ResultSet rs = null;
        try{
            stmt = cnx.createStatement();

            //get mosaic additional axis values to recreate discret axis
            final SortedSet[] discretValues = new SortedSet[crs.getCoordinateSystem().getDimension()];
            for(int i=0;i<discretValues.length;i++){
                discretValues[i] = new TreeSet();
            }

            StringBuilder query = new StringBuilder();
            query.append("SELECT ma.\"indice\", ma.\"value\" FROM ");
            query.append(store.encodeTableName("Mosaic"));
            query.append(" AS m , ");
            query.append(store.encodeTableName("MosaicAxis"));
            query.append(" AS ma WHERE m.\"pyramidId\" = ");
            query.append(pyramidId);
            query.append(" AND ma.\"mosaicId\" = m.\"id\"");

            rs = stmt.executeQuery(query.toString());
            while(rs.next()){
                final int indice = rs.getInt(1);
                final double value = rs.getDouble(2);
                discretValues[indice].add(value);
            }
            store.closeSafe(rs);

            final double[][] table = new double[discretValues.length][0];
            for(int i=0;i<discretValues.length;i++){
                final Object[] ds = discretValues[i].toArray();
                final double[] vals = new double[ds.length];
                for(int k=0;k<ds.length;k++){
                    vals[k] = (Double)ds[k];
                }
                table[i] = vals;
            }

            final CoordinateReferenceSystem dcrs = crs;
            pyramid = new PGPyramid(this, pyramidId, dcrs);

            query = new StringBuilder();
            query.append("SELECT \"id\",\"upperCornerX\",\"upperCornerY\",\"gridWidth\",\"gridHeight\",\"scale\",\"tileWidth\",\"tileHeight\" FROM ");
            query.append(store.encodeTableName("Mosaic"));
            query.append(" WHERE \"pyramidId\" = ");
            query.append(pyramidId);

            rs = stmt.executeQuery(query.toString());
            while(rs.next()){
                final long mosaicId = rs.getLong(1);
                final double cornerX = rs.getDouble(2);
                final double cornerY = rs.getDouble(3);
                final int gridWidth = rs.getInt(4);
                final int gridHeight = rs.getInt(5);
                final double scale = rs.getDouble(6);
                final int tileWidth = rs.getInt(7);
                final int tileHeight = rs.getInt(8);

                final GeneralDirectPosition position = new GeneralDirectPosition(crs);
                position.setOrdinate(0, cornerX);
                position.setOrdinate(1, cornerY);


                if(crs.getCoordinateSystem().getDimension() > 2){
                    //retrieve additional axis value
                    Statement stmt2 = null;
                    ResultSet rs2 = null;
                    try{
                        stmt2 = cnx.createStatement();
                        query = new StringBuilder();
                        query.append("SELECT \"indice\",\"value\" FROM ");
                        query.append(store.encodeTableName("MosaicAxis"));
                        query.append(" WHERE \"mosaicId\" = ");
                        query.append(mosaicId);
                        rs2 = stmt2.executeQuery(query.toString());
                        while(rs2.next()){
                            position.setOrdinate(rs2.getInt(1), rs2.getDouble(2));
                        }
                    }finally{
                        store.closeSafe(null, stmt2, rs2);
                    }
                }

                final PGGridMosaic mosaic = new PGGridMosaic(this,
                        mosaicId, pyramid, position,
                        new Dimension(gridWidth, gridHeight),
                        new Dimension(tileWidth, tileHeight), scale);
                pyramid.mosaics.add(mosaic);
            }

        }catch(SQLException ex){
            store.getLogger().log(Level.WARNING, ex.getMessage(),ex);
        }finally{
            store.closeSafe(null, stmt, rs);
        }

        return pyramid;
    }

    @Override
    public boolean isWritable() throws CoverageStoreException {
        return true;
    }

    @Override
    public PGCoverageStore getOriginator() {
        return (PGCoverageStore) pgstore;
    }

    @Override
    public GridCoverageWriter acquireWriter() throws CoverageStoreException {
        return new PyramidalModelWriter(this);
    }

    @Override
    public Collection<Pyramid> getModels() throws DataStoreException {
        return getPyramids();
    }

    @Override
    public MultiResolutionModel createModel(MultiResolutionModel template) throws DataStoreException {
        if (template instanceof Pyramid) {
            final Pyramid p = (Pyramid) template;
            final Pyramid n = newPyramid(p.getCoordinateReferenceSystem());
            Pyramids.copyStructure(p, n);
            return n;
        } else {
            throw new DataStoreException("Unsupported template : "+template);
        }
    }

    @Override
    public void removeModel(String identifier) throws DataStoreException {
        Connection cnx = null;
        Statement stmt = null;
        ResultSet rs = null;
        try{
            cnx = pgstore.getDataSource().getConnection();
            cnx.setReadOnly(false);
            stmt = cnx.createStatement();
            final int pyramidIdInt = Integer.valueOf(identifier);
            final StringBuilder sql = new StringBuilder("DELETE FROM ");
            sql.append(pgstore.encodeTableName("Pyramid"));
            sql.append(" WHERE id = ");
            sql.append(pyramidIdInt);
            stmt.executeUpdate(sql.toString());
        }catch(NumberFormatException ex){
            throw new DataStoreException("Identifier "+identifier+" not found in models");
        }catch(SQLException ex){
            throw new DataStoreException(ex.getMessage(), ex);
        }finally{
            pgstore.closeSafe(cnx, stmt, rs);
            mustUpdate();
        }
    }

    private Pyramid newPyramid(final CoordinateReferenceSystem crs) throws DataStoreException {

        String pyramidId = "";

        Connection cnx = null;
        Statement stmt = null;
        ResultSet rs = null;
        try{
            cnx = pgstore.getDataSource().getConnection();
            cnx.setReadOnly(false);
            //find or insert coordinate reference system
            final PGEPSGWriter writer = new PGEPSGWriter(pgstore);
            final String epsgCode = String.valueOf(writer.getOrCreateCoordinateReferenceSystem(crs));

            stmt = cnx.createStatement();

            final int layerId = pgstore.getLayerId(cnx,getIdentifier().tip().toString());

            StringBuilder query = new StringBuilder();
            query.append("INSERT INTO ");
            query.append(pgstore.encodeTableName("Pyramid"));
            query.append("(\"layerId\",\"epsg\") VALUES (");
            query.append(layerId);
            query.append(",'");
            query.append(epsgCode);
            query.append("')");

            stmt.executeUpdate(query.toString(), new String[]{"id"});

            rs = stmt.getGeneratedKeys();
            if(rs.next()){
                pyramidId = String.valueOf(rs.getInt(1));
            }

            //write version
            if(version!=null && !version.getLabel().equals(PGVersionControl.UNSET)){
                query.setLength(0);
                query.append("INSERT INTO ");
                query.append(pgstore.encodeTableName("PyramidProperty"));
                query.append("(\"pyramidId\",\"key\",\"type\",\"value\") VALUES (");
                query.append(pyramidId);
                query.append(",'version','date','");
                query.append(TemporalUtilities.toISO8601Z(version.getDate(), TimeZone.getTimeZone("GMT+0")));
                query.append("')");
                stmt.executeUpdate(query.toString());
            }

        }catch(FactoryException ex){
            throw new DataStoreException(ex);
        }catch(SQLException ex){
            throw new DataStoreException(ex);
        }finally{
            pgstore.closeSafe(cnx, stmt, rs);
        }

        mustUpdate();
        final CoverageStoreManagementEvent event = firePyramidAdded(pyramidId);
        getOriginator().forwardEvent(event);
        for(Pyramid p : getPyramids()){
            if(p.getIdentifier().equals(pyramidId)){
                return p;
            }
        }

        //should not happen
        throw new DataStoreException("Generated pyramid not found.");
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException{
        final List<SampleDimension> dimensions = new LinkedList<>();

        boolean versionSupport = isVersionColumnExist();

        Connection cnx = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            cnx = pgstore.getDataSource().getConnection();
            cnx.setReadOnly(false);

            final int layerId = pgstore.getLayerId(cnx,getIdentifier().tip().toString());
            String versionStr;
            if (version != null && !version.getLabel().equals(PGVersionControl.UNSET)) {
                versionStr = TemporalUtilities.toISO8601Z(version.getDate(), TimeZone.getTimeZone("GMT+0"));
            } else {
                versionStr = PGVersionControl.UNSET;
            }

            final StringBuilder query = new StringBuilder();
            if(versionSupport) {
                query.append("SELECT \"id\",\"version\",\"indice\",\"description\",\"dataType\",\"unit\"");
                query.append("FROM ").append(pgstore.encodeTableName("Band"));
                query.append(" WHERE ");
                query.append("\"layerId\"=").append(Integer.valueOf(layerId));
                query.append(" AND \"version\" LIKE \'").append(versionStr).append("\' ");
                query.append("ORDER BY \"indice\" ASC");
            } else {
                query.append("SELECT \"id\",\"indice\",\"description\",\"dataType\",\"unit\"");
                query.append("FROM ").append(pgstore.encodeTableName("Band"));
                query.append(" WHERE ");
                query.append("\"layerId\"=").append(Integer.valueOf(layerId));
                query.append("ORDER BY \"indice\" ASC");
            }

            stmt = cnx.createStatement();
            rs = stmt.executeQuery(query.toString());

            while (rs.next()) {
                final int sid = rs.getInt("id");
                final int indice = rs.getInt("indice");
                final String description = rs.getString("description");
                final SampleDimensionType type = WKBRasterConstants.getDimensionType(rs.getInt("dataType"));
                final String unitStr = rs.getString("unit");
                Unit unit = null;
                if(unitStr != null && !unitStr.isEmpty()) {
                    unit = Units.valueOf(unitStr);
                }

                //read categories
                final StringBuilder catQuery = new StringBuilder();
                catQuery.append("SELECT \"id\",\"band\",\"name\",\"lower\",\"upper\",\"c0\",\"c1\",\"function\",\"colors\" ");
                catQuery.append("FROM ").append(pgstore.encodeTableName("Category"));
                catQuery.append(" WHERE ");
                catQuery.append("\"band\"=").append(Integer.valueOf(sid));
                stmt = cnx.createStatement();
                final ResultSet catrs = stmt.executeQuery(catQuery.toString());
                final SampleDimension.Builder b = new SampleDimension.Builder();
                while(catrs.next()){
                    final String name = catrs.getString("name");
                    final double lower = catrs.getDouble("lower");
                    final double upper = catrs.getDouble("upper");
                    final double c0 = catrs.getDouble("c0");
                    final double c1 = catrs.getDouble("c1");
                    final String function = catrs.getString("function");
                    final String[] colors = catrs.getString("colors").split(",");

                    final TransferFunction f = new TransferFunction();
                    TransferFunctionType functionType = TransferFunctionType.valueOf(function);
                    if (functionType != null) {
                        f.setType(functionType);
                    } else{
                        throw new IllegalArgumentException("Unsupported transform : "+function);
                    }
                    f.setScale(c1);
                    f.setOffset(c0);

                    final MathTransform1D sampleToGeophysics = f.getTransform();

                    if (Double.isNaN(lower) || lower == upper) {
                        b.addQualitative(name, lower);
                    } else {
                        final NumberRange range = NumberRange.create(lower, true, upper, false);
                        b.addQuantitative(name, range, sampleToGeophysics, unit);
                    }
                }
                final SampleDimension dim = b.setName(description).build();
                dimensions.add(indice, dim);
            }

        } catch (SQLException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        } finally {
            pgstore.closeSafe(cnx, stmt, rs);
        }
        return dimensions;
    }

    private boolean isVersionColumnExist() {
        Connection cnx = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            cnx = pgstore.getDataSource().getConnection();
            cnx.setReadOnly(false);

            final StringBuilder query = new StringBuilder();
            query.append( "SELECT count(column_name) FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = ");
            query.append("'"+pgstore.getDatabaseSchema()+"'");
            query.append("AND table_name = 'Band' AND column_name = 'version'");

            stmt = cnx.createStatement();
            rs = stmt.executeQuery(query.toString());
            return rs.next();

        } catch(SQLException ex) {
            return false;
        } finally {
            pgstore.closeSafe(cnx, stmt, rs);
        }
    }

    @Override
    public ViewType getPackMode() throws DataStoreException {
        return ViewType.RENDERED;
    }

    @Override
    public void setPackMode(ViewType packMode) throws DataStoreException {
    }

    @Override
    public void setSampleDimensions(List<SampleDimension> dimensions) throws DataStoreException {

        boolean versionSupport = isVersionColumnExist();

        if (dimensions != null) {
            double[] maxDimValues = new double[dimensions.size()];
            double[] minDimValues =  new double[dimensions.size()];

//            if (analyse != null) {
//                maxDimValues = (double[]) analyse.get("max");
//                minDimValues = (double[]) analyse.get("min");
//            } else {
                Arrays.fill(maxDimValues, Double.MAX_VALUE);
                Arrays.fill(minDimValues, Double.MIN_VALUE);
//            }

            Connection cnx = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            try{
                cnx = pgstore.getDataSource().getConnection();
                cnx.setReadOnly(false);

                final int layerId = pgstore.getLayerId(cnx,getIdentifier().tip().toString());
                for (int i = 0; i < dimensions.size(); i++) {

                    final SampleDimension dim = dimensions.get(i);
                    String versionStr;
                    if (version != null && !version.getLabel().equals(PGVersionControl.UNSET)) {
                        versionStr = TemporalUtilities.toISO8601Z(version.getDate(), TimeZone.getTimeZone("GMT+0"));
                    } else {
                        versionStr = PGVersionControl.UNSET;
                    }

                    final String description = dim.getName() != null ? dim.getName().toString() : "";
                    final String unit = dim.getUnits() != null ? dim.getUnits().toString() : "";

                    final StringBuilder query = new StringBuilder();
                    if (versionSupport) {
                        query.append("INSERT INTO ");
                        query.append(pgstore.encodeTableName("Band"));
                        query.append(" (\"layerId\",\"version\",\"indice\",\"description\",\"dataType\",\"unit\") ");
                        query.append("VALUES (?, ?, ?, ?, ?, ?)");

                        pstmt = cnx.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);

                        pstmt.setInt(1, layerId);
                        pstmt.setString(2, versionStr);
                        pstmt.setInt(3, i);
                        pstmt.setString(4, description);
                        pstmt.setInt(5, (WKBRasterConstants.getPixelType(SampleDimensionType.REAL_32BITS /* dim.getSampleDimensionType() */)));
                        pstmt.setString(6, unit);

                    } else {
                        query.append("INSERT INTO ");
                        query.append(pgstore.encodeTableName("Band"));
                        query.append(" (\"layerId\",\"indice\",\"description\",\"dataType\",\"unit\") ");
                        query.append("VALUES (?, ?, ?, ?, ?)");

                        pstmt = cnx.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);

                        pstmt.setInt(1, layerId);
                        pstmt.setInt(2, i);
                        pstmt.setString(3, description);
                        pstmt.setInt(4, (WKBRasterConstants.getPixelType(SampleDimensionType.REAL_32BITS /* dim.getSampleDimensionType() */)));
                        pstmt.setString(5, unit);
                    }
                    pstmt.executeUpdate();

                    int sid = 0;
                    final ResultSet keyrs = pstmt.getGeneratedKeys();
                    if (keyrs.next()) {
                        sid = keyrs.getInt(1);
                    }

                    //create the categories
                    List<Category> categories = dim.getCategories();
                    if (categories == null || categories.isEmpty()) {
                        //use the statistic analyze to build categories.
                        categories = new ArrayList<>();
                        final double[] pNoData = SampleDimensionUtils.getNoDataValues(dim);
                        Double[] noData;
                        if (pNoData != null) {
                            noData = new Double[pNoData.length];
                            for (int j = 0; j < noData.length; j++) {
                                noData[j] = pNoData[j];
                            }
                        }

                        double min = Double.isInfinite(SampleDimensionUtils.getMinimumValue(dim)) ? minDimValues[i] : SampleDimensionUtils.getMinimumValue(dim);
                        double max = Double.isInfinite(SampleDimensionUtils.getMaximumValue(dim)) ? maxDimValues[i] : SampleDimensionUtils.getMaximumValue(dim);

                        final SampleDimension.Builder builder = new SampleDimension.Builder();
                        builder.addQuantitative("data", NumberRange.create(min, true, max, true), null, null);

                        if (pNoData != null && pNoData.length > 0) {
                            for (int k = 0; k < pNoData.length; k++) {
                                builder.addQualitative(null, pNoData[k]);
                            }
                        }
                        categories.addAll(builder.categories());
                    }

                    for (Category category : categories) {
                        final double c0;
                        final double c1;
                        final String function;
                        final String[] colors = new String[0];
                        double min = category.getSampleRange().getMinDouble();
                        double max = category.getSampleRange().getMaxDouble();
                        min = fixCloseToZero(min);
                        max = fixCloseToZero(max);

                        final MathTransform1D trs = category.getTransferFunction().orElse(null);
                        if (trs == null) {
                            function = TransferFunctionType.LINEAR.name();
                            c0 = 0;
                            c1 = 1;
                        } else {
                            final TransferFunction transfertFunction = new TransferFunction();
                            transfertFunction.setTransform(trs);
                            function = transfertFunction.getType().name();
                            c0 = transfertFunction.getOffset();
                            c1 = transfertFunction.getScale();
                        }

                        final StringBuilder catQuery = new StringBuilder("INSERT INTO ");
                        catQuery.append(pgstore.encodeTableName("Category"));
                        catQuery.append(" (\"band\",\"name\",\"lower\",\"upper\",\"c0\",\"c1\",\"function\",\"colors\") ");
                        catQuery.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?)");


                        PreparedStatement catstmt = cnx.prepareStatement(catQuery.toString());
                        catstmt.setInt(   1, sid);
                        catstmt.setString(2, String.valueOf(category.getName()));
                        catstmt.setDouble(3, min);
                        catstmt.setDouble(4, max);
                        catstmt.setDouble(5, c0);
                        catstmt.setDouble(6, c1);
                        catstmt.setString(7, function);
                        catstmt.setString(8, StringUtilities.toCommaSeparatedValues((Object[]) colors));
                        catstmt.executeUpdate();
                    }
                }

            }catch(SQLException ex){
                throw new DataStoreException(ex.getMessage(), ex);
            }finally{
                pgstore.closeSafe(cnx, pstmt, rs);
            }
        }
    }

    /**
     * Postgres do not like this value.
     * Caused by : ERROR: "4.9E-324" is out of range for type double precision
     * org.postgresql.core.v3.QueryExecutorImpl.receiveErrorResponse(QueryExecutorImpl.java:2103)
     *
     * @param d
     * @return
     */
    private static double fixCloseToZero(double d){
        if(d == 4.9E-324){
            return 0.0;
        }
        return d;
    }

    /**
     * Color to hexadecimal.
     *
     * @param color
     * @return color in hexadecimal form
     */
    private static String colorToString(final Color color) {
        if (color == null) {
            return null;
        }

        String redCode = Integer.toHexString(color.getRed());
        String greenCode = Integer.toHexString(color.getGreen());
        String blueCode = Integer.toHexString(color.getBlue());
        if (redCode.length() == 1)      redCode = "0" + redCode;
        if (greenCode.length() == 1)    greenCode = "0" + greenCode;
        if (blueCode.length() == 1)     blueCode = "0" + blueCode;

        final String colorCode;
        int alpha = color.getAlpha();
        if(alpha != 255){
            String alphaCode = Integer.toHexString(alpha);
            if (alphaCode.length() == 1) alphaCode = "0" + alphaCode;
            colorCode = "#" + alphaCode + redCode + greenCode + blueCode;
        }else{
            colorCode = "#" + redCode + greenCode + blueCode;
        }
        return colorCode.toUpperCase();
    }

    @Override
    public CoverageStoreManagementEvent fireMosaicAdded(String pyramidId, String mosaicId) {
        return super.fireMosaicAdded(pyramidId, mosaicId);
    }

    @Override
    public CoverageStoreManagementEvent fireMosaicDeleted(String pyramidId, String mosaicId) {
        return super.fireMosaicDeleted(pyramidId, mosaicId);
    }

    @Override
    public CoverageStoreContentEvent fireTileUpdated(String pyramidId, String mosaicId, List<Point> tiles) {
        return super.fireTileUpdated(pyramidId, mosaicId, tiles);
    }
}
