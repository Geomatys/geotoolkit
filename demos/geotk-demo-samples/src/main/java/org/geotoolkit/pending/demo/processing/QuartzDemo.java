
package org.geotoolkit.pending.demo.processing;

import java.util.UUID;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.process.quartz.ProcessJobDetail;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.NoSuchIdentifierException;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.Matcher;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.SimpleTriggerImpl;

public class QuartzDemo {
    
    public static void main(String[] args) throws SchedulerException, NoSuchIdentifierException {
        
        //create a quartz scheduler
        final SchedulerFactory factory = new StdSchedulerFactory();
        final Scheduler scheduler = factory.getScheduler();
        scheduler.start();
        
        //listen to scheduler events
        scheduler.getListenerManager().addJobListener(new JobListener() {
            @Override
            public String getName() {
                return "listener";
            }
            @Override
            public void jobToBeExecuted(JobExecutionContext jec) {
            }
            @Override
            public void jobExecutionVetoed(JobExecutionContext jec) {
            }
            @Override
            public void jobWasExecuted(JobExecutionContext jec, JobExecutionException jee) {
                System.out.println(">>>>>>>> done");
                System.out.println(jec.getResult());
            }
        }, new Matcher<JobKey>() {
            @Override
            public boolean isMatch(JobKey t) {
                return true;
            }
        });
        
        
        
        //prepare a process job
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("mymaths", "add");       
        final ParameterValueGroup input = desc.getInputDescriptor().createValue();
        input.parameter("first").setValue(15d);
        input.parameter("second").setValue(5d);
        final ProcessJobDetail detail = new ProcessJobDetail("mymaths", "add", input);
        
        
        SimpleTrigger trigger = new SimpleTriggerImpl(UUID.randomUUID().toString(),5, 500);
        
        scheduler.scheduleJob(detail, trigger);
        
        
        
        
        
    }
    
}
