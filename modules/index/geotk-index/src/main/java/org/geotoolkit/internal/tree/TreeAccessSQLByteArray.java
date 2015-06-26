
package org.geotoolkit.internal.tree;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author rmarechal
 */
public class TreeAccessSQLByteArray extends TreeAccessByteArray {

    private final DataSource source;
    private final File directory;
    
    public TreeAccessSQLByteArray(final File directory, final DataSource source, int magicNumber, double versionNumber, int maxElements, CoordinateReferenceSystem crs) throws IOException {
        super(magicNumber, versionNumber, maxElements, crs);
        this.source = source;
        this.directory = directory;
    }

    public TreeAccessSQLByteArray(final File directory,final DataSource source, byte[] data, int magicNumber, double versionNumber) throws IOException, ClassNotFoundException {
        super(data, magicNumber, versionNumber);
        this.source = source;
        this.directory = directory;
    }

    private void printTree() throws SQLException {
        final byte[] array = getData();
        final Connection c = source.getConnection();
        final PreparedStatement stmt = c.prepareStatement("INSERT INTO \"" + directory.getAbsolutePath() + "\".\"tree\" VALUES(?)");
        stmt.setBytes(1, array);
        stmt.execute();
        stmt.close();
        c.close();
    }

    @Override
    public strictfp void flush() throws IOException {
        super.flush();
        try {
            printTree();
        } catch (SQLException ex) {
            throw new IOException(ex);
        }
    }
    
    public static byte[] getData(final File directory, final DataSource source) throws SQLException {
        byte[] data = null;
        
        final Connection c = source.getConnection();
        final Statement stmt = c.createStatement();
        final ResultSet rs = stmt.executeQuery("SELECT \"data\" FROM \"" + directory.getAbsolutePath() + "\".\"tree\"");
        if (rs.next()) {
            data = rs.getBytes("data");
        }
        rs.close();
        stmt.close();
        c.close();
        return data;
    }
}
