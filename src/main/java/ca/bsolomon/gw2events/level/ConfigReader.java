package ca.bsolomon.gw2events.level;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;

import ca.bsolomon.gw2events.level.model.ConditionType;
import ca.bsolomon.gw2events.level.model.CountEventCondition;
import ca.bsolomon.gw2events.level.model.EventChain;
import ca.bsolomon.gw2events.level.model.EventCondition;
import ca.bsolomon.gw2events.level.model.EventState;
import ca.bsolomon.gw2events.level.model.SequenceEventCondition;

public class ConfigReader {

	public static final List<EventChain> trackedChains = new ArrayList<>();
	public static final List<String> queriedMapIds = new ArrayList<>(); 
	
	private static final String folderName = "level_event_chains";
	public static Boolean readInProcess = false;
	
	private static boolean initRead = false;
	private static WatchKey watchKey;
	private static WatchService watchService;
	
	public static void readConfigFiles() {
		Path folderPath = Paths.get(folderName);
		
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(folderPath)) {
			for (Path file: stream) {
				parseFile(file);
			}
		} catch (IOException | DirectoryIteratorException x) {
			System.out.println("File path: "+folderPath+" does not exist or is not directory.");
		}
		
		try {
			watchService = FileSystems.getDefault().newWatchService();
			watchKey = folderPath.register(watchService,StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_MODIFY);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		setInitRead(true);
	}

	public static void parseFile(Path file) {
		try {
			List<String> fileLines = Files.readAllLines(file, Charset.defaultCharset());
			
			String chainName = fileLines.get(0);
			String mapId = fileLines.get(1);
			
			readInProcess = true;
			generateChainData(chainName, mapId, fileLines.subList(2, fileLines.size()));
			readInProcess = false;
		} catch (IOException e) {
			System.out.println("File path: "+file+" read error: "+e.getLocalizedMessage());
		}
	}

	private static void generateChainData(String chainName, String mapId,
			List<String> subList) {
		if (!queriedMapIds.contains(mapId)) {
			queriedMapIds.add(mapId);
		}
		
		EventChain chain = new EventChain(chainName, mapId, new ArrayList<EventState>());
		
		for (String eventStateStr:subList) {
			String[] splitString = eventStateStr.split("\\|");
			ConditionType condType;
			List<EventCondition> conditions = new ArrayList<>();
			
			if (splitString[1].equals("And")) {
				condType = ConditionType.AND;
				
				parseEventStates(splitString[2], conditions);
			} else if (splitString[1].equals("Or")) {
				condType = ConditionType.OR;
				
				parseEventStates(splitString[2], conditions);
			} else if (splitString[1].equals("SeqId")) {
				condType = ConditionType.SEQUENCE_ID;
				
				String[] conditionStr = splitString[2].split(":");
				
				int sequenceId = Integer.parseInt(conditionStr[0]);
				
				parseEventStates(splitString[2], conditions, sequenceId);
			} else if (splitString[1].equals("CountEnd")) {
				condType = ConditionType.COUNT_END;
				
				String maxCount = splitString[2];
				
				EventCondition condition = new CountEventCondition(null, null, maxCount);
				conditions.add(condition);
			} else {
				condType = ConditionType.COUNT;
				
				parseEventStates(splitString[2], conditions);
			}
			
			EventState state = new EventState(splitString[0], Integer.parseInt(splitString[3]), conditions, condType);
			chain.getEventStates().add(state);
		}
		
		if (!trackedChains.contains(chain)) {
			trackedChains.add(chain);
		} else {
			int index = trackedChains.indexOf(chain);
			trackedChains.set(index, chain);
		}
	}

	private static void parseEventStates(String splitString,
			List<EventCondition> conditions) {
		String[] conditionStr = splitString.split(":");
		
		for (int i=0;i<conditionStr.length;i+=2) {
			EventCondition condition = new EventCondition(conditionStr[i], conditionStr[i+1]);
			conditions.add(condition);
		}
	}
	
	private static void parseEventStates(String splitString,
			List<EventCondition> conditions, int sequenceId) {
		String[] conditionStr = splitString.split(":");
		
		for (int i=1;i<conditionStr.length;i+=2) {
			EventCondition condition = new SequenceEventCondition(conditionStr[i], conditionStr[i+1], sequenceId);
			conditions.add(condition);
		}
	}

	public static boolean isInitRead() {
		return initRead;
	}

	public static void setInitRead(boolean initRead) {
		ConfigReader.initRead = initRead;
	}

	public static WatchKey getWatchKey() {
		return watchKey;
	}

	public static WatchService getWatchService() {
		return watchService;
	}
}