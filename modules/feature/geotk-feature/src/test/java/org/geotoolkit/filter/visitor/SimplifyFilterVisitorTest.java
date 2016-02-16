/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.filter.visitor;

import java.util.Collections;
import java.util.HashSet;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.opengis.filter.Filter;
import org.junit.Test;
import org.opengis.filter.Id;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.identity.Identifier;
import static org.geotoolkit.test.Assert.*;
import static org.geotoolkit.filter.FilterTestConstants.*;

/**
 * Test simplifying filter visitor
 *
 * @author Johann Sorel (Geomatys)
 */
public class SimplifyFilterVisitorTest extends org.geotoolkit.test.TestBase {

    @Test
    public void testIdRegroup(){
        final Filter id1 = FF.id(Collections.singleton(new MockIdentifier("123")));
        final Filter id2 = FF.id( new HashSet(UnmodifiableArrayList.wrap(new MockIdentifier[] {new MockIdentifier("456"), new MockIdentifier("789")}) ));
        final Filter id3 = FF.id(Collections.singleton(new MockIdentifier("789")));
        final Filter or = FF.or(UnmodifiableArrayList.wrap(new Filter[] {id1,id2,id3}));

        SimplifyingFilterVisitor visitor = new SimplifyingFilterVisitor();
        final Filter res = (Filter) or.accept(visitor, null);

        assertTrue(res instanceof Id);

        final Id ids = (Id) res;
        assertEquals(3, ids.getIdentifiers().size());
        assertEquals(3, ids.getIDs().size());

        assertTrue( ids.getIDs().contains("123"));
        assertTrue( ids.getIDs().contains("456"));
        assertTrue( ids.getIDs().contains("789"));

    }


    private static class MockIdentifier implements FeatureId{

        private final String id;

        public MockIdentifier(String id) {
            this.id = id;
        }

        @Override
        public String getID() {
            return id;
        }

        @Override
        public boolean matches(Object o) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean equals(Object obj) {
            return this.id.equals( ((MockIdentifier)obj).id );
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 37 * hash + (this.id != null ? this.id.hashCode() : 0);
            return hash;
        }

    }

}
