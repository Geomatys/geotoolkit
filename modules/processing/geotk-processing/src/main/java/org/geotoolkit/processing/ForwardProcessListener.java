package org.geotoolkit.processing;

import org.geotoolkit.process.ProcessEvent;
import org.geotoolkit.process.ProcessListener;
import org.opengis.util.InternationalString;

/**
 * A ProcessListener implementation that simplify progressing event spreading to parent process.
 * This listener is used to update parent process progress value from child-process progressing.
 * <p>
 *  Example :
 *  We have a {@code parent} process that execute a {@code child} process.
 *  The  {@code parent} give arbitrarily 10% of his total work load to  {@code child} process and start
 *  {@code child} process at 42% of his work.
 *  This listener will forward  {@code child} progressing events from 42% to 52%;
 * </p>
 * <p>
 *  <code>
 *      Process child = new ChildProcess();
 *      child.addListener(new ForwardProcessListener(this, 42f, 10f));
 *      child.call();
 *  </code>
 * </p>
 *
 * @author Quentin Boileau (Geomatys)
 * @since 4.0
 */
public class ForwardProcessListener implements ProcessListener {
    private final AbstractProcess parentProcess;
    private final float taskPercentStart;
    private final float taskWorkLength;

    /**
     * Constructor
     * @param parentProcess process to forward events to
     * @param taskPercentStart start percent from parent process
     * @param taskWorkLength child process duration in percent
     */
    public ForwardProcessListener(AbstractProcess parentProcess, float taskPercentStart, float taskWorkLength) {
        this.parentProcess = parentProcess;
        this.taskPercentStart = taskPercentStart;
        this.taskWorkLength = taskWorkLength;
    }

    @Override
    public void started(ProcessEvent processEvent) {
        String processName = getProcessName(processEvent);
        fireProgressing(processName+" : Start", taskPercentStart, null);
    }

    private String getProcessName(ProcessEvent processEvent) {
        String processName = null;
        try {
            final InternationalString is = processEvent.getSource().getDescriptor().getDisplayName();
            if (is != null) {
                processName = is.toString();
            }
            if (processName == null) {
                processName = processEvent.getSource().getDescriptor().getIdentifier().getCode();
            }
        } catch (Exception ex) {
            processName = "";
        }
        return processName;
    }

    @Override
    public void progressing(ProcessEvent processEvent) {
        String processName = getProcessName(processEvent);
        String msg = processName + " : " + processEvent.getTask().toString();

        float progress = taskPercentStart + (taskWorkLength * (processEvent.getProgress() / 100f));
        fireProgressing(msg, progress, processEvent.getException());
    }

    @Override
    public void dismissed(ProcessEvent processEvent) {
        //no forward
    }

    private void fireProgressing(String message, float progress, Exception ex) {
        final ProcessEvent event = new ProcessEvent(parentProcess, message, progress, ex);
        final ProcessListener[] listeners = parentProcess.getListeners();
        for (ProcessListener listener : listeners) {
            listener.progressing(event);
        }
    }

    @Override
    public void paused(ProcessEvent processEvent) {
        //no forward
    }

    @Override
    public void resumed(ProcessEvent processEvent) {
        //no forward
    }

    @Override
    public void completed(ProcessEvent processEvent) {
        String processName = getProcessName(processEvent);
        fireProgressing(processName+" : Completed", taskPercentStart+ taskWorkLength, null);
    }

    @Override
    public void failed(ProcessEvent processEvent) {
        //no forward
    }
}
