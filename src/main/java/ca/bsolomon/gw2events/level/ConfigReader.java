package ca.bsolomon.gw2events.level;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	public static final List<Path> parsedPaths = new ArrayList<>(); 
	
	private static final String folderName = "level_event_chains";
	public static Boolean readInProcess = false;
	
	public static void readConfigFiles() {
		Path folderPath = Paths.get(folderName);
		
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(folderPath)) {
			for (Path file: stream) {
				if (!parsedPaths.contains(file)) {
					readInProcess = true;
					parseFile(file);
					parsedPaths.add(file);
					readInProcess = false;
				}
			}
		} catch (IOException | DirectoryIteratorException x) {
			System.out.println("File path: "+folderPath+" does not exist or is not directory.");
		}
	}

	private static void parseFile(Path file) {
		try {
			List<String> fileLines = Files.readAllLines(file, Charset.defaultCharset());
			
			String chainName = fileLines.get(0);
			String mapId = fileLines.get(0);
			
			generateChainData(chainName, mapId, fileLines.subList(2, fileLines.size()));
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
			} else {
				condType = ConditionType.COUNT;
				
				String[] conditionStr = splitString[2].split(":");
				
				int countId = Integer.parseInt(conditionStr[0]);
				String maxCount = conditionStr[1];
				
				parseEventStates(splitString[2], conditions, countId, maxCount);
			}
			
			EventState state = new EventState(splitString[0], Integer.parseInt(splitString[3]), conditions, condType);
			chain.getEventStates().add(state);
		}
		
		trackedChains.add(chain);
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
	
	private static void parseEventStates(String splitString,
			List<EventCondition> conditions, int countId, String count) {
		String[] conditionStr = splitString.split(":");
		
		for (int i=2;i<conditionStr.length;i+=2) {
			EventCondition condition = new CountEventCondition(conditionStr[i], conditionStr[i+1], countId, count);
			conditions.add(condition);
		}
	}
}