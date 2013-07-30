package ca.bsolomon.gw2events.level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.GJChronology;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import ca.bsolomon.gw2event.api.GW2EventsAPI;
import ca.bsolomon.gw2event.api.dao.Event;
import ca.bsolomon.gw2events.level.model.ConditionType;
import ca.bsolomon.gw2events.level.model.CountEventCondition;
import ca.bsolomon.gw2events.level.model.EventChain;
import ca.bsolomon.gw2events.level.model.EventCondition;
import ca.bsolomon.gw2events.level.model.EventState;
import ca.bsolomon.gw2events.level.model.LiveEventState;
import ca.bsolomon.gw2events.level.model.SequenceEventCondition;
import ca.bsolomon.gw2events.level.model.Server;
import ca.bsolomon.gw2events.level.model.ServerID;

@DisallowConcurrentExecution
public class ApiQueryJob implements Job {

	private static final String STATE = "state";
	private static final String EVENT_ID = "event_id";
	private static final String WORLD_ID = "world_id";
	
	private GW2EventsAPI api = new GW2EventsAPI();
	
	private DateTimeZone zone = DateTimeZone.forID("America/New_York");
	private Chronology gregorianJuian = GJChronology.getInstance(zone);
	
	public static Map<Integer, Server> serverEvents = new ConcurrentHashMap<>(16, 0.9f, 1);
	
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		initializeStaticApiData();
		
		if (!ConfigReader.readInProcess) {
			List<String> mapIds = new ArrayList<>(ConfigReader.queriedMapIds);
			
			for (String mapId:mapIds) {
				List<Event> data = api.queryMapEventStatus(mapId);
				
				Map<String, String> dataMap = new HashMap<>();
				
				for (int i=0;i<data.size();i++) {
					Event obj = data.get(i);
					
					Integer worldId = Integer.parseInt(obj.getMapId());
					
					if (worldId < 2000) {
						dataMap.put(obj.getWorldId()+"-"+obj.getEventId(), obj.getState());
					}
				}
				
				for (EventChain chain:ConfigReader.trackedChains) {
					if (chain.getMapId().equals(mapId)) {
						processEventChainData(chain, dataMap);
					}
				}
			}
		}
	}

	private void processEventChainData(EventChain chain, Map<String, String> data) {
		DateTime time = new DateTime(gregorianJuian);
		
		for (EventState state:chain.getEventStates()) {
			ConditionType type = state.getConditionType();
			
			switch (type) {
				case AND : 
					processAndCondition(state, data, chain.getChainName(), time); 
					break;
				case OR : 
					processOrCondition(state, data, chain.getChainName(), time); 
					break;
				case COUNT :
					processCountCondition(state, data, chain.getChainName(), time); 
					break;
				case COUNT_END :
					processCountEndCondition(state, chain.getChainName(), time); 
					break;
				case SEQUENCE_ID : 
					processSequenceIdCondition(state, data, chain.getChainName(), time); 
					break;
			}
		}
		
		LiveEventState searchState = new LiveEventState(null, null, chain.getChainName(), null, -1);
		
		for (ServerID servId:ServerID.values()) {
			Server serv = serverEvents.get(servId.getUid());
			
			if (serv != null && serv.getEventChains().contains(searchState)) {
				int index = serv.getEventChains().indexOf(searchState);
				LiveEventState oldState = serv.getEventChains().get(index);
				
				if (!oldState.getDate().equals(time) && !oldState.getStatus().equals("Not up")) {
					LiveEventState newState = new LiveEventState("Not up", time, 
							chain.getChainName(), "", -1);
					newState.setUpdateDate(time);
					
					serv.getEventChains().set(index, newState);
				}
			} else {
				if (serv == null) {
					serverEvents.put(servId.getUid(), new Server(servId));
				} else {
					LiveEventState newState = new LiveEventState("Not up", time, 
							chain.getChainName(), "", -1);
					newState.setUpdateDate(time);
					serverEvents.get(servId.getUid()).getEventChains().add(newState);
				}
			}
		}
	}

	private void processSequenceIdCondition(EventState state,
			Map<String, String> data, String chainName, DateTime time) {
		List<EventCondition> conditions = state.getConditions();
		
		for (ServerID servId:ServerID.values()) {
			boolean conditionMet = false;
			
			Server serv = serverEvents.get(servId.getUid());
			
			LiveEventState searchState = new LiveEventState(null, null, chainName, null, -1);
			
			if (serv.getEventChains().contains(searchState)) {
				int index = serv.getEventChains().indexOf(searchState);
				LiveEventState oldState = serv.getEventChains().get(index);
				
				int seqId = ((SequenceEventCondition)conditions.get(0)).getSequenceId();
				
				if (oldState.getSequenceId() != seqId) {
					continue;
				}
			}
			
			for (EventCondition condition:conditions) {
				String combinedId = servId.getUid()+"-"+condition.getEventId();
				
				if (data.get(combinedId)!=null && data.get(combinedId).equals(condition.getState())) {
					conditionMet = true;
					break;
				}
			}
			
			if (conditionMet) {
				changeChainState(state, chainName, time, servId);
			}
		}
	}

	private void processCountEndCondition(EventState state, String chainName,
			DateTime time) {
		List<EventCondition> conditions = state.getConditions();
		
		String maxCount = ((CountEventCondition)conditions.get(0)).getMaxCountValue();
		
		for (ServerID servId:ServerID.values()) {
			Server serv = serverEvents.get(servId.getUid());
			
			LiveEventState searchState = new LiveEventState(null, null, chainName, null, -1);
			
			if (serv.getEventChains().contains(searchState)) {
				int index = serv.getEventChains().indexOf(searchState);
				LiveEventState oldState = serv.getEventChains().get(index);
				
				LiveEventState newState = new LiveEventState(oldState.getStatus()+": "+oldState.getCount()+"/"+maxCount, oldState.getDate(), 
						oldState.getEvent(), state.getWaypoint(), oldState.getSequenceId());
				newState.setCount(0);
				newState.setUpdateDate(oldState.getUpdateDate());
				
				serv.getEventChains().set(index, newState);
			}
		}
	}

	private void processCountCondition(EventState state,
			Map<String, String> data, String chainName, DateTime time) {
		List<EventCondition> conditions = state.getConditions();
				
		for (ServerID servId:ServerID.values()) {
			boolean conditionMet = false;
			
			for (EventCondition condition:conditions) {
				String combinedId = servId.getUid()+"-"+condition.getEventId();
				
				if (data.get(combinedId)!= null && data.get(combinedId).equals(condition.getState())) {
					conditionMet = true;
					break;
				}
			}
			
			if (!serverEvents.containsKey(servId.getUid())) {
				serverEvents.put(servId.getUid(), new Server(servId));
			}
			
			Server serv = serverEvents.get(servId.getUid());
			
			LiveEventState searchState = new LiveEventState(null, null, chainName, null, -1);
						
			if (conditionMet) {
				if (serv.getEventChains().contains(searchState)) {
					int index = serv.getEventChains().indexOf(searchState);
					LiveEventState oldState = serv.getEventChains().get(index);
					
					if (oldState.getStatus().equals(state.getOutputText())) {
						oldState.setCount(oldState.getCount()+1);
					} else {
						LiveEventState newState = new LiveEventState(state.getOutputText(), time, chainName, state.getWaypoint(), state.getSequenceId());
						newState.setCount(1);
						newState.setUpdateDate(time);
						
						serv.getEventChains().set(index, newState);
					}
				} else {
					LiveEventState newState = new LiveEventState(state.getOutputText(), time, chainName, state.getWaypoint(), state.getSequenceId());
					newState.setCount(1);
					newState.setUpdateDate(time);
					serv.getEventChains().add(newState);
				}
			}
		}
	}

	private void processOrCondition(EventState state,
			Map<String, String> data, String chainName, DateTime time) {
		List<EventCondition> conditions = state.getConditions();
		
		for (ServerID servId:ServerID.values()) {
			boolean conditionMet = false;
			
			for (EventCondition condition:conditions) {
				String combinedId = servId.getUid()+"-"+condition.getEventId();
				
				if (data.get(combinedId)!= null && data.get(combinedId).equals(condition.getState())) {
					conditionMet = true;
					break;
				}
			}
			
			if (conditionMet) {
				changeChainState(state, chainName, time, servId);
			}
		}
	}

	private void processAndCondition(EventState state,
			Map<String, String> data, String chainName, DateTime time) {
		List<EventCondition> conditions = state.getConditions();
		
		for (ServerID servId:ServerID.values()) {
			boolean conditionMet = true;
			
			for (EventCondition condition:conditions) {
				String combinedId = servId.getUid()+"-"+condition.getEventId();
				
				if (data.get(combinedId)== null || !data.get(combinedId).equals(condition.getState())) {
					conditionMet = false;
					break;
				}
			}
			
			if (conditionMet) {
				changeChainState(state, chainName, time, servId);
			}
		}
	}

	private void changeChainState(EventState state, String chainName,
			DateTime time, ServerID servId) {
		if (!serverEvents.containsKey(servId.getUid())) {
			serverEvents.put(servId.getUid(), new Server(servId));
		}
		
		Server serv = serverEvents.get(servId.getUid());
		
		LiveEventState newState = new LiveEventState(state.getOutputText(), time, chainName, state.getWaypoint(), state.getSequenceId());
		
		if (!serv.getEventChains().contains(newState)) {
			newState.setUpdateDate(time);
			serv.getEventChains().add(newState);
		} else {
			int index = serv.getEventChains().indexOf(newState);
			
			LiveEventState oldState = serv.getEventChains().get(index);
			
			if (oldState.getStatus().equals(newState.getStatus())) {
				newState.setUpdateDate(oldState.getDate());
			} else {
				newState.setUpdateDate(time);
			}
			
			serv.getEventChains().set(index, newState);
		}
	}

	private void initializeStaticApiData() {
		if (GW2EventsAPI.eventIdToName == null || GW2EventsAPI.eventIdToName.size() == 0) {
			GW2EventsAPI.generateEventIds();
			GW2EventsAPI.generateMapIds();
			GW2EventsAPI.generateNAWorldIds();
		}
	}

}
