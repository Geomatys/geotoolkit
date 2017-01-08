
package org.geotoolkit.pending.demo.processing;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.process.ProcessingRegistry;
import org.geotoolkit.process.ProcessDescriptor;
import org.opengis.util.NoSuchIdentifierException;


public class FindProcessDemo {

    public static void main(String[] args) throws NoSuchIdentifierException {
        Demos.init();

        //Get an iterator on all process factory
        final Iterator<ProcessingRegistry> factoryIte = ProcessFinder.getProcessFactories();

        while (factoryIte.hasNext()) {

            //Get a factory
            final ProcessingRegistry registry = factoryIte.next();
            System.out.println("Registry : "+registry.getIdentification().getCitation().getTitle().toString());

            //Get factory process descriptor in order to find the description of all process of this factory.
            final List<ProcessDescriptor> descriptorList = registry.getDescriptors();
            Collections.sort(descriptorList, new Comparator<ProcessDescriptor>() {
                @Override
                public int compare(ProcessDescriptor o1, ProcessDescriptor o2) {
                    return o1.getIdentifier().getCode().compareTo(o2.getIdentifier().getCode());
                }
            });

            for (ProcessDescriptor descriptor : descriptorList) {
                System.out.println("    Process : "+descriptor.getIdentifier().getCode());
            }
            System.out.println("---------------------------------------------------------------------------");
        }

        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("demo", "addition");
        System.out.println(desc);
    }
}
