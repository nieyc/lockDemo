package com.github.nyc.bootDemo.task;

/**
 * 定时任务，可以替代之前的plus应用
 */
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SchedulerTask {
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	
	private int count=0;

	/**
	 * 每6s执行一次
	 * 
	 */
    @Scheduled(cron="*/6 * * * * ?")
    private void process(){
        System.out.println("this is scheduler task runing  "+(count++));
    }
    
    
    /**
     * 延迟1秒钟，每6s执行一次
     */
    @Scheduled(initialDelay=1000, fixedRate=6000)
    public void reportCurrentTime() {
        System.out.println("现在时间：" + dateFormat.format(new Date()));
    }
    
    
    /**
     * 每天下午16点08分执行一次
     */
    @Scheduled(cron="0 08 16 ? * *")
    public void fixTaskEveryDay() {
        System.out.println("每天执行一次");
    }
    
    
    /**
     * 每隔1小时执行一次
     */
    @Scheduled(cron = "0 0 */1 * * ?")
    public void fixTaskOneHour() {
        System.out.println("每1小时执行一次");
    }

}
