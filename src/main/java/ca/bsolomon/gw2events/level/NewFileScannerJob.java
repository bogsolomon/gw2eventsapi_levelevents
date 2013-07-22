package ca.bsolomon.gw2events.level;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@DisallowConcurrentExecution
public class NewFileScannerJob implements Job {

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		ConfigReader.readConfigFiles();
	}

}
