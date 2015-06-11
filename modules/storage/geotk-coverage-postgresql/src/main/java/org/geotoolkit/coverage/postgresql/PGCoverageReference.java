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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.unit.Unit;
import javax.swing.ProgressMonitor;
import net.iharder.Base64;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.referencing.operation.transform.TransferFunction;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.coverage.AbstractPyramidalCoverageReference;
import org.geotoolkit.coverage.Category;
import org.geotoolkit.storage.coverage.CoverageStoreContentEvent;
import org.geotoolkit.storage.coverage.CoverageStoreManagementEvent;
import org.geotoolkit.storage.coverage.GridMosaic;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.storage.coverage.Pyramid;
import org.geotoolkit.storage.coverage.PyramidSet;
import org.geotoolkit.storage.coverage.PyramidalModelWriter;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.coverage.postgresql.epsg.PGEPSGWriter;
import org.geotoolkit.coverage.wkb.WKBRasterConstants;
import org.geotoolkit.coverage.wkb.WKBRasterWriter;
import org.geotoolkit.internal.InternalUtilities;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.temporal.object.TemporalUtilities;
import org.geotoolkit.util.StringUtilities;
import org.geotoolkit.version.Version;
import org.opengis.coverage.SampleDimensionType;
import org.geotoolkit.feature.type.Name;
import org.opengis.geometry.DirectPosition;
import org.opengis.metadata.content.TransferFunctionType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 */
public class PGCoverageReference extends AbstractPyramidalCoverageReference {

    private final PGCoverageStore pgstore;
    private final PGPyramidSet pyramidSet;
    final Version version;

    public PGCoverageReference(final PGCoverageStore store, final Name name, Version version) {
        super(store,name,0);
        this.pgstore = store;
        this.pyramidSet = new PGPyramidSet(this);
        this.version = version;
    }

    @Override
    public boolean isWritable() throws CoverageStoreException {
        return true;
    }

    @Override
    public PGCoverageStore getStore() {
        return (PGCoverageStore) pgstore;
    }

    @Override
    public GridCoverageWriter acquireWriter() throws CoverageStoreException {
        return new PyramidalModelWriter(this);
    }

    @Override
    public PyramidSet getPyramidSet() throws DataStoreException {
        return pyramidSet;
    }

    @Override
    public Pyramid createPyramid(final CoordinateReferenceSystem crs) throws DataStoreException {

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

            final int layerId = pgstore.getLayerId(cnx,name.getLocalPart());

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

        pyramidSet.mustUpdate();
        final CoverageStoreManagementEvent event = firePyramidAdded(pyramidId);
        getStore().forwardStructureEvent(event);
        for(Pyramid p : pyramidSet.getPyramids()){
            if(p.getId().equals(pyramidId)){
                return p;
            }
        }

        //should not happen
        throw new DataStoreException("Generated pyramid not found.");
    }

    @Override
    public void deletePyramid(String pyramidId) throws DataStoreException {
        Connection cnx = null;
        Statement stmt = null;
        ResultSet rs = null;
        try{
            cnx = pgstore.getDataSource().getConnection();
            cnx.setReadOnly(false);
            stmt = cnx.createStatement();
            final int pyramidIdInt = Integer.valueOf(pyramidId);
            final StringBuilder sql = new StringBuilder("DELETE FROM ");
            sql.append(pgstore.encodeTableName("Pyramid"));
            sql.append(" WHERE id = ");
            sql.append(pyramidIdInt);
            stmt.executeUpdate(sql.toString());
        }catch(SQLException ex){
            throw new DataStoreException(ex.getMessage(), ex);
        }finally{
            pgstore.closeSafe(cnx, stmt, rs);
            pyramidSet.mustUpdate();
        }
    }

    @Override
    public GridMosaic createMosaic(final String pyramidId, final Dimension gridSize, final Dimension tilePixelSize,
            final DirectPosition upperleft, final double pixelscale) throws DataStoreException {

        long mosaicId = 0;

        Connection cnx = null;
        Statement stmt = null;
        ResultSet rs = null;
        try{
            cnx = pgstore.getDataSource().getConnection();
            cnx.setReadOnly(false);
            stmt = cnx.createStatement();

            final int pyramidIdInt = Integer.valueOf(pyramidId);

            StringBuilder query = new StringBuilder();
            query.append("INSERT INTO ");
            query.append(pgstore.encodeTableName("Mosaic"));
            query.append("(\"pyramidId\",\"upperCornerX\",\"upperCornerY\",\"gridWidth\",\"gridHeight\",\"scale\",\"tileWidth\",\"tileHeight\") VALUES (");
            query.append(pyramidIdInt           ).append(',');
            query.append(upperleft.getOrdinate(0)).append(',');
            query.append(upperleft.getOrdinate(1)).append(',');
            query.append(gridSize.width         ).append(',');
            query.append(gridSize.height        ).append(',');
            query.append(pixelscale             ).append(',');
            query.append(tilePixelSize.width    ).append(',');
            query.append(tilePixelSize.height   );
            query.append(")");

            stmt.executeUpdate(query.toString(), new String[]{"id"});

            rs = stmt.getGeneratedKeys();
            if(rs.next()){
                mosaicId = rs.getLong(1);
            }

            final CoordinateReferenceSystem crs = upperleft.getCoordinateReferenceSystem();
            final CoordinateSystem cs = crs.getCoordinateSystem();
            for(int i=2,n=cs.getDimension();i<n;i++){
                final double value = upperleft.getOrdinate(i);
                query = new StringBuilder();
                query.append("INSERT INTO ");
                query.append(pgstore.encodeTableName("MosaicAxis"));
                query.append("(\"mosaicId\",\"indice\",\"value\") VALUES (");
                query.append(mosaicId).append(',');
                query.append(i).append(',');
                query.append(value);
                query.append(")");
                stmt.executeUpdate(query.toString());
            }

        }catch(SQLException ex){
            throw new DataStoreException(ex.getMessage(), ex);
        }finally{
            pgstore.closeSafe(cnx, stmt, rs);
        }

        pyramidSet.mustUpdate();
        final CoverageStoreManagementEvent event = fireMosaicAdded(pyramidId, String.valueOf(mosaicId));
        getStore().forwardStructureEvent(event);
        for (final Pyramid p : pyramidSet.getPyramids()) {
            if (p.getId().equals(pyramidId)) {
                for(GridMosaic mosaic : p.getMosaics()){
                    if (((PGGridMosaic)mosaic).getDatabaseId() == mosaicId) {
                        return mosaic;
                    }
                }
            }
        }

        //should not happen
        throw new DataStoreException("Generated mosaic not found.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteMosaic(String pyramidId, String mosaicId) throws DataStoreException {
        Connection cnx = null;
        Statement stmt = null;
        ResultSet rs = null;
        try{
            cnx = pgstore.getDataSource().getConnection();
            cnx.setReadOnly(false);
            stmt = cnx.createStatement();
            final int mosaicdIdInt = Integer.valueOf(mosaicId);
            final StringBuilder sql = new StringBuilder("DELETE FROM ");
            sql.append(pgstore.encodeTableName("Mosaic"));
            sql.append(" WHERE id = ");
            sql.append(mosaicdIdInt);
            stmt.executeUpdate(sql.toString());
        }catch(SQLException ex){
            throw new DataStoreException(ex.getMessage(), ex);
        }finally{
            pgstore.closeSafe(cnx, stmt, rs);
            pyramidSet.mustUpdate();
        }
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void writeTiles(final String pyramidId, final String mosaicId, final RenderedImage image, final Rectangle area,
                           final boolean onlyMissing, final ProgressMonitor monitor) throws DataStoreException {

        final int offsetX = image.getMinTileX();
        final int offsetY = image.getMinTileY();

        final int startX = (int)area.getMinX();
        final int startY = (int)area.getMinY();
        final int endX = (int)area.getMaxX();
        final int endY = (int)area.getMaxY();

        assert startX >= 0;
        assert startY >= 0;
        assert endX > startX && endX <= image.getNumXTiles();
        assert endY > startY && endY <= image.getNumYTiles();

        final RejectedExecutionHandler rejectHandler = new ThreadPoolExecutor.CallerRunsPolicy();
        final BlockingQueue queue = new ArrayBlockingQueue(Runtime.getRuntime().availableProcessors());
        final ThreadPoolExecutor executor = new ThreadPoolExecutor(
                0, Runtime.getRuntime().availableProcessors(), 1, TimeUnit.MINUTES, queue, rejectHandler);

        for(int y=startY; y<endY;y++){
            for(int x=startX;x<endX;x++){
                final Raster raster = image.getTile(offsetX+x, offsetY+y);
                final RenderedImage img = new BufferedImage(image.getColorModel(),
                        (WritableRaster)raster, image.getColorModel().isAlphaPremultiplied(), null);

                final int tx = offsetX+x;
                final int ty = offsetY+y;

                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        if (monitor != null && monitor.isCanceled()) {
                            return;
                        }
                        try {
                            writeTile(pyramidId, mosaicId, tx, ty, img);
                        } catch (DataStoreException ex) {
                            Logger.getLogger(PGCoverageReference.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });

            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeTile(String pyramidId, String mosaicId, int col, int row, RenderedImage image) throws DataStoreException {

        Connection cnx = null;
        Statement insertStmt = null;
        Statement deleteStmt = null;
        Statement selectStmt = null;
        ResultSet rs = null;
        try{
            StringBuilder query = new StringBuilder();
            query.append("SELECT id,\"mosaicId\",\"positionX\",\"positionY\" FROM ")
                 .append(pgstore.encodeTableName("Tile"))
                 .append(" WHERE \"mosaicId\"=").append(Integer.valueOf(mosaicId))
                 .append(" AND \"positionX\"=").append(Integer.valueOf(col))
                 .append(" AND \"positionY\"=").append(Integer.valueOf(row));
            cnx = pgstore.getDataSource().getConnection();
            cnx.setReadOnly(false);
            selectStmt = cnx.createStatement();
            rs = selectStmt.executeQuery(query.toString());

            while (rs.next()) {
                final int id = rs.getInt("id");
                query = new StringBuilder();
                query.append("DELETE FROM ")
                     .append(pgstore.encodeTableName("Tile"))
                     .append(" WHERE id=").append(id);
                deleteStmt = cnx.createStatement();
                deleteStmt.executeUpdate(query.toString());
            }

            final WKBRasterWriter writer = new WKBRasterWriter();
            final byte[] wkbimg = writer.write(image, null, 0);
            final String base64 = Base64.encodeBytes(wkbimg);

            query = new StringBuilder();
            query.append("INSERT INTO");
            query.append(pgstore.encodeTableName("Tile"));
            query.append("(\"raster\",\"mosaicId\",\"positionX\",\"positionY\") VALUES ( (");
            query.append("encode(").append("decode('").append(base64).append("','base64')").append(",'hex')").append(")::raster,");
            query.append(Integer.valueOf(mosaicId)).append(',');
            query.append(col).append(',');
            query.append(row).append(')');

            insertStmt = cnx.createStatement();
            insertStmt.executeUpdate(query.toString());

            final CoverageStoreContentEvent event = fireTileUpdated(pyramidId, mosaicId, Collections.singletonList(new Point(col,row)));
            getStore().forwardContentEvent(event);
        }catch(IOException ex){
            throw new DataStoreException(ex.getMessage(), ex);
        }catch(SQLException ex){
            throw new DataStoreException(ex.getMessage(), ex);
        }finally{
            pgstore.closeSafe(cnx, selectStmt, rs);
            pgstore.closeSafe(insertStmt);
            pgstore.closeSafe(deleteStmt);
        }

    }

    @Override
    public void deleteTile(String pyramidId, String mosaicId, int col, int row) throws DataStoreException {
        Connection cnx = null;
        Statement stmt = null;
        ResultSet rs = null;
        try{
            cnx = pgstore.getDataSource().getConnection();
            cnx.setReadOnly(false);
            stmt = cnx.createStatement();
            final int mosaicdIdInt = Integer.valueOf(mosaicId);
            final StringBuilder sql = new StringBuilder("DELETE FROM ");
            sql.append(pgstore.encodeTableName("Tile"));
            sql.append(" WHERE \"mosaicId\" = ").append(mosaicdIdInt);
            sql.append(" AND \"positionX\" = ").append(col);
            sql.append(" AND \"positionY\" = ").append(row);
            stmt.executeUpdate(sql.toString());
        }catch(SQLException ex){
            throw new DataStoreException(ex.getMessage(), ex);
        }finally{
            pgstore.closeSafe(cnx, stmt, rs);
            pyramidSet.mustUpdate();
        };
    }

    @Override
    public List<GridSampleDimension> getSampleDimensions() throws DataStoreException{
        final List<GridSampleDimension> dimensions = new LinkedList<>();

        boolean versionSupport = isVersionColumnExist();

        Connection cnx = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            cnx = pgstore.getDataSource().getConnection();
            cnx.setReadOnly(false);

            final int layerId = pgstore.getLayerId(cnx,name.getLocalPart());
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
                    unit = Unit.valueOf(unitStr);
                }

                //read categories
                final List<Category> categories = new ArrayList<Category>();
                final StringBuilder catQuery = new StringBuilder();
                catQuery.append("SELECT \"id\",\"band\",\"name\",\"lower\",\"upper\",\"c0\",\"c1\",\"function\",\"colors\" ");
                catQuery.append("FROM ").append(pgstore.encodeTableName("Category"));
                catQuery.append(" WHERE ");
                catQuery.append("\"band\"=").append(Integer.valueOf(sid));
                stmt = cnx.createStatement();
                final ResultSet catrs = stmt.executeQuery(catQuery.toString());
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

                    final Color[] cols = new Color[colors.length];
                    for(int i=0;i<cols.length;i++){
                        cols[i] = new java.awt.Color(InternalUtilities.parseColor(colors[i]), true);
                    }

                    final Category cat;
                    if(Double.isNaN(lower) || lower == upper){
                        cat = new Category(name, cols[0], lower);
                    }else{
                        final NumberRange range = NumberRange.create(lower, true, upper, false);
                        cat = new Category(name, cols, range, sampleToGeophysics);
                    }
                    categories.add(cat);
                }

                final GridSampleDimension dim = new GridSampleDimension(description, categories.toArray(new Category[categories.size()]), unit);
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
    public void setSampleDimensions(List<GridSampleDimension> dimensions) throws DataStoreException {
        
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

                final int layerId = pgstore.getLayerId(cnx,name.getLocalPart());
                for (int i = 0; i < dimensions.size(); i++) {

                    final GridSampleDimension dim = dimensions.get(i);
                    String versionStr;
                    if (version != null && !version.getLabel().equals(PGVersionControl.UNSET)) {
                        versionStr = TemporalUtilities.toISO8601Z(version.getDate(), TimeZone.getTimeZone("GMT+0"));
                    } else {
                        versionStr = PGVersionControl.UNSET;
                    }

                    final String description = dim.getDescription() != null ? dim.getDescription().toString() : "";
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
                        pstmt.setInt(5, (WKBRasterConstants.getPixelType(dim.getSampleDimensionType())));
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
                        pstmt.setInt(4, (WKBRasterConstants.getPixelType(dim.getSampleDimensionType())));
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
                        final double[] pNoData = dim.getNoDataValues();
                        Double[] noData;
                        if (pNoData != null) {
                            noData = new Double[pNoData.length];
                            for (int j = 0; j < noData.length; j++) {
                                noData[j] = pNoData[j];
                            }
                        }

                        double min = Double.isInfinite(dim.getMinimumValue()) ? minDimValues[i] : dim.getMinimumValue();
                        double max = Double.isInfinite(dim.getMaximumValue()) ? maxDimValues[i] : dim.getMaximumValue();

                        categories.add(new Category("data", Color.BLACK, NumberRange.create(min, true, max, true)));
                        if (pNoData != null && pNoData.length > 0) {
                            for (int k = 0; k < pNoData.length; k++) {
                                categories.add(new Category(Vocabulary.formatInternational(Vocabulary.Keys.NODATA) + String.valueOf(k), new Color(0,0,0,0), pNoData[k]));
                            }
                        }
                    }


                    for (Category category : categories) {

                        final double c0;
                        final double c1;
                        final String function;
                        final String[] colors;
                        double min = category.getRange().getMinDouble();
                        double max = category.getRange().getMaxDouble();
                        min = fixCloseToZero(min);
                        max = fixCloseToZero(max);

                        final Color[] cols = category.getColors();
                        colors = new String[cols.length];
                        for(int k=0;k<cols.length;k++){
                            colors[k] = colorToString(cols[k]);
                        }

                        final MathTransform1D trs = category.getSampleToGeophysics();
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

}
