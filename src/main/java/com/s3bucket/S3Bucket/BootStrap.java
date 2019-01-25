
@Component
public class BootStrap implements ApplicationListener<ContextRefreshedEvent> {

    private static Logger logger = Logger.getLogger(BootStrap.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent arg0) {
        methodName();
        addInitialMarketData();

    }
    void methodName() {
      logger.info("Called methodName()");
    }
}
