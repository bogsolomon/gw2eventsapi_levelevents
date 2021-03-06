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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.bsolomon.gw2events.level.model.ConditionType;
import ca.bsolomon.gw2events.level.model.CountEventCondition;
import ca.bsolomon.gw2events.level.model.EventChain;
import ca.bsolomon.gw2events.level.model.EventCondition;
import ca.bsolomon.gw2events.level.model.EventState;
import ca.bsolomon.gw2events.level.model.MapInfo;
import ca.bsolomon.gw2events.level.model.SequenceEventCondition;

public class ConfigReader {

	public static final List<EventChain> trackedChains = new ArrayList<>();
	public static final List<String> queriedMapIds = new ArrayList<>(); 
	public static final Map<String, MapInfo> maps = new HashMap<>();
	
	private static final String chainFolderName = "level_event_chains";
	private static final String mapFolderName = "map_data";
	
	public static Boolean readInProcess = false;
	
	private static boolean initRead = false;
	private static WatchKey watchKey;
	private static WatchService watchService;
	
	public static void readConfigFiles() {
		Path folderPath = Paths.get(chainFolderName);
		
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(folderPath)) {
			for (Path file: stream) {
				parseChainFile(file);
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
		
		folderPath = Paths.get(mapFolderName);
		
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(folderPath)) {
			for (Path file: stream) {
				parseMapFile(file);
			}
		} catch (IOException | DirectoryIteratorException x) {
			System.out.println("File path: "+folderPath+" does not exist or is not directory.");
		}
		
		setInitRead(true);
	}

	private static void parseMapFile(Path file) {
		try {
			List<String> fileLines = Files.readAllLines(file, Charset.defaultCharset());
			
			if (fileLines!=null && fileLines.size() == 2) {
				String mapId = fileLines.get(0);
				String levelRange = fileLines.get(1);
				
				String[] levelRanges = levelRange.split("\\|");
				
				maps.put(mapId, new MapInfo(mapId, Integer.parseInt(levelRanges[0]), Integer.parseInt(levelRanges[1])));
			}
		} catch (IOException e) {
			System.out.println("File path: "+file+" read error: "+e.getLocalizedMessage());
		}
	}

	public static void parseChainFile(Path file) {
		try {
			List<String> fileLines = Files.readAllLines(file, Charset.defaultCharset());
			
			if (fileLines!=null && fileLines.size() > 2) {
				String[] splitString = fileLines.get(0).split("\\|");
				
				String chainName = splitString[0];
				String eventClass = null;
				boolean isLivingStoryEvent = false;
				
				if (splitString.length > 1)
					eventClass = splitString[1];
				
				if (splitString.length > 2)
					isLivingStoryEvent = Boolean.parseBoolean(splitString[2]);
				
				String mapId = fileLines.get(1);
				
				readInProcess = true;
				generateChainData(chainName, eventClass, isLivingStoryEvent, mapId, fileLines.subList(2, fileLines.size()));
				readInProcess = false;
			}
		} catch (IOException e) {
			System.out.println("File path: "+file+" read error: "+e.getLocalizedMessage());
		}
	}

	private static void generateChainData(String chainName, String eventClass,
			boolean isLivingStoryEvent, String mapId, List<String> subList) {
		if (!queriedMapIds.contains(mapId)) {
			queriedMapIds.add(mapId);
		}
		
		EventChain chain = new EventChain(chainName, mapId, new ArrayList<EventState>(), eventClass, isLivingStoryEvent);
		
		for (String eventStateStr:subList) {
			if (eventStateStr.length() == 0)
				continue;
			
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
			
			EventState state = new EventState(splitString[0], Integer.parseInt(splitString[3]), conditions, condType, splitString[4]);
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