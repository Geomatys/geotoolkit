
package org.geotoolkit.pending.demo.swing;

import java.util.Date;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.geotoolkit.gui.swing.propertyedit.JFeatureOutLine;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 * Demo showing how to use the generic feature editor widget.
 */
public class FeatureEditionDemo {

    public static void main(String[] args) {

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.addAttribute(Boolean.class).setName("boolean");
        ftb.addAttribute(Integer.class).setName("integer");
        ftb.addAttribute(Double.class).setName("double");
        ftb.addAttribute(String.class).setName("String");
        ftb.addAttribute(Date.class).setName("Date");
        ftb.addAttribute(boolean[].class).setName("boolean[]");
        ftb.addAttribute(int[].class).setName("integer[]");
        ftb.addAttribute(double[].class).setName("double[]");
        ftb.addAttribute(Boolean[].class).setName("Boolean[]");
        ftb.addAttribute(Integer[].class).setName("Integer[]");
        ftb.addAttribute(Double[].class).setName("Double[]");
        ftb.addAttribute(String[].class).setName("String[]");
        final FeatureType type = ftb.build();
        final Feature feature = type.newInstance();

        JFeatureOutLine.show(null,feature,true);


        System.out.println(feature.toString());

    }

}
