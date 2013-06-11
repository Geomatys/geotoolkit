
package org.geotoolkit.pending.demo.mapmodel;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.gui.swing.go2.JMap2DFrame;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.style.DefaultStyleFactory;

/**
 * This demo illustrate the capabilities of the rendering engine to display
 * datas which are specific to your own system.
 * See filter.customaccessor demo for more accurate bindings.
 * 
 * the conventions expect to find a field named Geometry as default geometry.
 * otherwise the name must be set on the style.
 */
public class CollectionLayerDemo {
    
    private static final GeometryFactory GF = new GeometryFactory();
    private static final DefaultStyleFactory SF = new DefaultStyleFactory();
    
    public static class MySpecializedObject{
        
        private String name;
        //....
        private Geometry location;

        public MySpecializedObject(String name, Geometry location) {
            this.name = name;
            this.location = location;
            this.location.setSRID(4326);
        }
         
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Geometry getGeometry() {
            return location;
        }

        public void setGeometry(Geometry location) {
            this.location = location;
        }
        
    }
    
    public static void main(String[] args) {
        Demos.init();
                
        final MySpecializedObject obj1 = new MySpecializedObject("something", GF.createPoint(new Coordinate(30, 40)));        
        final MySpecializedObject obj2 = new MySpecializedObject("what's this?", 
                GF.createLineString(new Coordinate[]{
                    new Coordinate(15, 20),
                    new Coordinate(-27, -12),
                    new Coordinate(-50, 60)
                }));
        
        final List<MySpecializedObject> myObjects = new ArrayList<MySpecializedObject>();
        myObjects.add(obj1);
        myObjects.add(obj2);
                
        final MapContext context = MapBuilder.createContext();
        context.layers().add(MapBuilder.createCollectionLayer(myObjects));
        
        JMap2DFrame.show(context);
    }
    
}
