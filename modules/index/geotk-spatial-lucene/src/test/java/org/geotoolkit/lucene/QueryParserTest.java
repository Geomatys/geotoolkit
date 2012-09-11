/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Geomatys
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
package org.geotoolkit.lucene;

import org.geotoolkit.lucene.index.LuceneIndexSearcher;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class QueryParserTest {

    
    @Test
    public void BBOXTest() throws Exception {
        
        String s = "Title:*";
        assertEquals(LuceneIndexSearcher.removeOnlyWildchar(s), "metafile:doc");
        
        s = "Title:* +MetadataStandardName:earthsciences";
        assertEquals(LuceneIndexSearcher.removeOnlyWildchar(s), "metafile:doc +MetadataStandardName:earthsciences");
        
        s = "MetadataStandardName:earthsciences +Title:*";
        assertEquals(LuceneIndexSearcher.removeOnlyWildchar(s), "MetadataStandardName:earthsciences +metafile:doc");
        
        s = "Title:* +MetadataStandardName:earthsciences +bla:*";
        assertEquals(LuceneIndexSearcher.removeOnlyWildchar(s), "metafile:doc +MetadataStandardName:earthsciences +metafile:doc");
        
        s = "Title:*lit +MetadataStandardName:earthsciences +bla:*blo +title:lip*";
        assertEquals(LuceneIndexSearcher.removeOnlyWildchar(s), "Title:*lit +MetadataStandardName:earthsciences +bla:*blo +title:lip*");
        
        s = "Title:*lit +test:* +MetadataStandardName:earthsciences +bla:*blo +title:lip* +id:*";
        assertEquals(LuceneIndexSearcher.removeOnlyWildchar(s), "Title:*lit +metafile:doc +MetadataStandardName:earthsciences +bla:*blo +title:lip* +metafile:doc");
        
        s = "test:* +Title:*lit +MetadataStandardName:earthsciences +bla:*blo +title:lip* +id:*";
        assertEquals(LuceneIndexSearcher.removeOnlyWildchar(s), "metafile:doc +Title:*lit +MetadataStandardName:earthsciences +bla:*blo +title:lip* +metafile:doc");
        
        s = "test:(*) +Title:(*lit) +MetadataStandardName:earthsciences +bla:(*blo) +title:(lip*) +id:(*)";
        assertEquals(LuceneIndexSearcher.removeOnlyWildchar(s), "metafile:doc +Title:(*lit) +MetadataStandardName:earthsciences +bla:(*blo) +title:(lip*) +metafile:doc");
        
        s = "(Title:(*))";
        assertEquals(LuceneIndexSearcher.removeOnlyWildchar(s), "(metafile:doc)");
        
        s = "(Title:*)";
        assertEquals(LuceneIndexSearcher.removeOnlyWildchar(s), "(metafile:doc)");
        
        s = "(Title:* AND prout:*lop)";
        assertEquals(LuceneIndexSearcher.removeOnlyWildchar(s), "(metafile:doc AND prout:*lop)");
        
        s = "(Title:li* AND prout:* AND fiou:loi)";
        assertEquals(LuceneIndexSearcher.removeOnlyWildchar(s), "(Title:li* AND metafile:doc AND fiou:loi)");
        
        s = "(Title:*.ctd)";
        assertEquals(LuceneIndexSearcher.removeOnlyWildchar(s), "(Title:*.ctd)");
    }
}
