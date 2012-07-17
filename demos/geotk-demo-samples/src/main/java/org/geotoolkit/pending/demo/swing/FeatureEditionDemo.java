
package org.geotoolkit.pending.demo.swing;

import java.util.Date;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.gui.swing.propertyedit.JFeatureOutLine;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

/**
 * Demo showing how to use the generic feature editor widget.
 */
public class FeatureEditionDemo {

    public static void main(String[] args) {

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.add("boolean", Boolean.class);
        ftb.add("boolean[]", boolean[].class);
        ftb.add("Boolean[]", Boolean[].class);
        ftb.add("integer", Integer.class);
        ftb.add("integer[]", int[].class);
        ftb.add("Integer[]", Integer[].class);
        ftb.add("double", Double.class);
        ftb.add("String", String.class);
        ftb.add("Date", Date.class);
        final FeatureType type = ftb.buildFeatureType();
        final Feature feature = FeatureUtilities.defaultFeature(type, "id-1");

        JFeatureOutLine.show(feature,true);


        System.out.println(feature);

    }

}
