
package org.geotoolkit;

import org.geotoolkit.data.shapefile.DbaseFileTest;
import org.geotoolkit.data.shapefile.PrjFileTest;
import org.geotoolkit.data.shapefile.ShapefileDataStoreTest;
import org.geotoolkit.data.shapefile.ShapefileReadWriteTest;
import org.geotoolkit.data.shapefile.ShapefileTest;
import org.geotoolkit.data.shapefile.ShpFileTypesTest;
import org.geotoolkit.data.shapefile.ShpFilesTest;
import org.geotoolkit.data.shapefile.ShpFilesTestStream;
import org.geotoolkit.data.shapefile.StorageFileTest;
import org.geotoolkit.data.shapefile.cpg.CPGFileTest;
import org.geotoolkit.data.shapefile.fix.IndexedFidReaderTest;
import org.geotoolkit.data.shapefile.indexed.FidIndexerTest;
import org.geotoolkit.data.shapefile.indexed.IndexedFidWriterTest;
import org.geotoolkit.data.shapefile.indexed.IndexedShapefileDataStoreFactoryTest;
import org.geotoolkit.data.shapefile.indexed.IndexedShapefileDataStoreTest;
import org.geotoolkit.data.shapefile.indexed.ShapefileQuadTreeReadWriteTest;
import org.geotoolkit.data.shapefile.indexed.ShapefileRTreeReadWriteTest;
import org.geotoolkit.data.shapefile.shp.PolygonHandler2Test;
import org.geotoolkit.data.shapefile.shp.PolygonHandlerTest;
import org.geotoolkit.index.quadtree.LineLazySearchCollectionTest;
import org.geotoolkit.index.quadtree.PointLazySearchCollectionTest;
import org.geotoolkit.index.quadtree.PolygonLazySearchCollectionTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    CPGFileTest.class,
    IndexedFidReaderTest.class,
    FidIndexerTest.class,
    IndexedFidWriterTest.class,
    IndexedShapefileDataStoreFactoryTest.class,
    IndexedShapefileDataStoreTest.class,
    ShapefileQuadTreeReadWriteTest.class,
    ShapefileRTreeReadWriteTest.class,
    ShapefileTest.class,
    PolygonHandler2Test.class,
    PolygonHandlerTest.class,
    DbaseFileTest.class,
    PrjFileTest.class,
    ShapefileDataStoreTest.class,
    ShapefileReadWriteTest.class,
    ShapefileTest.class,
    ShpFileTypesTest.class,
    ShpFilesTest.class,
    ShpFilesTestStream.class,
    StorageFileTest.class,
    LineLazySearchCollectionTest.class,
    PointLazySearchCollectionTest.class,
    PolygonLazySearchCollectionTest.class
})
public class ShapefileTestSuite {

}
