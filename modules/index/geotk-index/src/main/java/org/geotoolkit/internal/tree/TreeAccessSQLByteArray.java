
package org.geotoolkit.internal.tree;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import static org.geotoolkit.index.tree.manager.postgres.LucenePostgresSQLTreeEltMapper.SCHEMA;
import org.geotoolkit.index.tree.manager.util.AeSimpleSHA1;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author rmarechal
 */
public class TreeAccessSQLByteArray extends TreeAccessByteArray {

    private final DataSource source;
    private final Path directory;

    public TreeAccessSQLByteArray(final Path directory, final DataSource source, int magicNumber, double versionNumber, int maxElements, CoordinateReferenceSystem crs) throws IOException {
        super(magicNumber, versionNumber, maxElements, crs);
        this.source = source;
        this.directory = directory;
    }

    public TreeAccessSQLByteArray(final Path directory,final DataSource source, byte[] data, int magicNumber, double versionNumber) throws IOException, ClassNotFoundException {
        super(data, magicNumber, versionNumber);
        this.source = source;
        this.directory = directory;
    }

    private void printTree() throws SQLException, NoSuchAlgorithmException, UnsupportedEncodingException {
        final byte[] array = getData();
        try (final Connection c = source.getConnection();
             final Statement dstmt  = c.createStatement()) {
            dstmt.executeUpdate("DELETE FROM \"" + getSchemaName(directory.getFileName().toString()) + "\".\"tree\"");

            try (final PreparedStatement stmt = c.prepareStatement("INSERT INTO \"" + getSchemaName(directory.getFileName().toString()) + "\".\"tree\" VALUES(?)")) {
                stmt.setBytes(1, array);
                stmt.execute();
            }
        }
    }

    @Override
    public strictfp void flush() throws IOException {
        super.flush();
        try {
            printTree();
        } catch (SQLException | NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            throw new IOException(ex);
        }
    }

    public static byte[] getData(final Path directory, final DataSource source) throws SQLException, NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] data = null;
        try (final Connection c = source.getConnection();
             final Statement stmt = c.createStatement();
             final ResultSet rs = stmt.executeQuery("SELECT \"data\" FROM \"" + getSchemaName(directory.getFileName().toString()) + "\".\"tree\"")) {
            if (rs.next()) {
                data = rs.getBytes("data");
            }
        }
        return data;
    }

    private static String getSchemaName(String absolutePath) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        final String sha1 = AeSimpleSHA1.SHA1(absolutePath);
        return SCHEMA+sha1;
    }
}
