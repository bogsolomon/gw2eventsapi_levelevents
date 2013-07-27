package ca.bsolomon.gw2events.level;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.util.concurrent.TimeUnit;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@DisallowConcurrentExecution
public class NewFileScannerJob implements Job {

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		
		if (!ConfigReader.isInitRead())
			ConfigReader.readConfigFiles();
		
		try {
			WatchKey watchKey = ConfigReader.getWatchService().poll(10,TimeUnit.SECONDS);
			
			if (watchKey != null) {
				 // retrieves pending events for a key.
	            for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {

	                // retrieves the event type and count.
	                // gets the kind of event (create, delete) 
	                final Kind<?> kind = watchEvent.kind();

	                // handles OVERFLOW event
	                if (kind == StandardWatchEventKinds.OVERFLOW) {
	                    continue;
	                } 

	                @SuppressWarnings("unchecked")
					final WatchEvent<Path> watchEventPath = (WatchEvent<Path>)(watchEvent);
	                
	                Path dir = (Path)watchKey.watchable();
	                final Path entry = dir.resolve(watchEventPath.context());
	                
	                ConfigReader.parseFile(entry.toAbsolutePath());
	            }

	            watchKey.reset();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
