package edu.stanford.bmir.protegex.notification;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


import edu.stanford.smi.protege.model.Transaction;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.Log;


/**
 * @author Jack Elliott <jacke@stanford.edu>
 */
public class NotificationSchedulerServlet  {

    private static final Logger logger = Logger.getLogger(NotificationSchedulerServlet.class.getName());

    private ScheduledExecutorService service;




    public void destroy() {
        service.shutdownNow();
    }



}