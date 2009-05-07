

package org.geotoolkit.filter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.geotoolkit.filter.accessor.Accessors;
import org.geotoolkit.filter.accessor.PropertyAccessor;

import org.opengis.filter.FilterVisitor;
import org.opengis.filter.Id;
import org.opengis.filter.identity.Identifier;

public class DefaultId implements Id{

    private static final String XPATH_ID = "@id";

    private final DualKeyMap keys = new DualKeyMap();

    public DefaultId( Set<? extends Identifier> ids ) {
        for(Identifier id : ids){
            keys.put(id.getID(), id);
        }
    }

    @Override
    public Set<Object> getIDs() {
        return keys.keySet();
    }

    @Override
    public Set<Identifier> getIdentifiers() {
        return new HashSet<Identifier>(keys.values());
    }

    @Override
    public boolean evaluate(Object object) {
        if (object == null) {
            return false;
        }

        final PropertyAccessor accessor = Accessors.getAccessor(object.getClass(), XPATH_ID, null);

        if (accessor == null) {
            return false;
        }
        return keys.containsKey(accessor.get(object, XPATH_ID, null));
    }

    @Override
    public Object accept(FilterVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }

    /**
     * Take advantage of the fact that both ObjectId and Identifier are unique and
     * ObjectId is an attribut of Identifier.
     * This special map act like a double key map and so benefit all the performance
     * of hashmap.
     */
    private class DualKeyMap extends HashMap<Object,Identifier>{

        @Override
        public boolean containsValue(Object value) {
            if(value instanceof Identifier){
                Identifier ident = (Identifier) value;
                return containsKey(ident.getID());
            }else{
                return false;
            }
        }
    }
}
