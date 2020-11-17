/**
 * Tomcat JEE scheduler workaround
 */
@WebListener
public class CronJobManager implements ServletContextListener {
    private ScheduledExecutorService scheduler;
    
    @Inject
    private CronHelperService cronService;
    
    @Override
    public void contextInitialized(ServletContextEvent event) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new FileCleanupDailyJob(), 0, 1, TimeUnit.DAYS);
        scheduler.scheduleAtFixedRate(new RefreshApplicationCacheJob(cronService), 0, 1, TimeUnit.DAYS);
        scheduler.scheduleAtFixedRate(new DailyNotificationJob(cronService), 10, 15, TimeUnit.MINUTES);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        System.out.println("Stopping Scheduler");
        try {
            scheduler.shutdownNow();
            System.out.println("Shutdown scheduler completed.");
        } catch (SchedulerException e) {
            LOG.severe("Unable to shutdown scheduler");
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
