package ca.bsolomon.gw2events.level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import ca.bsolomon.gw2event.api.GW2EventsAPI;
import ca.bsolomon.gw2events.level.model.LiveEventState;
import ca.bsolomon.gw2events.level.model.MapEvents;
import ca.bsolomon.gw2events.level.model.MapInfo;
import ca.bsolomon.gw2events.level.model.Server;

@ManagedBean(name="levelEventBean")
@SessionScoped
public class LevelingEventBean {
	
	@ManagedProperty(value="#{checkboxBean}")
	private CheckboxBean checkboxBean;

	public void setCheckboxBean(CheckboxBean checkboxBean) {
		this.checkboxBean = checkboxBean;
	}
	
	public List<MapEvents> getServ1Status() {
		Server serv = checkboxBean.getServerOne();
		
		List<MapEvents> retStates = new ArrayList<>();
		
		getServerStates(serv, retStates);
		
		Collections.sort(retStates);
		
		return retStates;
	}

	public List<MapEvents> getServ2Status() {
		Server serv = checkboxBean.getServerTwo();
		
		List<MapEvents> retStates = new ArrayList<>();
		
		getServerStates(serv, retStates);
		
		Collections.sort(retStates);
		
		return retStates;
	}
	
	public List<MapEvents> getServ3Status() {
		Server serv = checkboxBean.getServerThree();
		
		List<MapEvents> retStates = new ArrayList<>();
		
		getServerStates(serv, retStates);
		
		Collections.sort(retStates);
		
		return retStates;
	}
	
	private void getServerStates(Server serv, List<MapEvents> retStates) {
		if (serv!=null) {
			for (LiveEventState event:serv.getEventChains()) {
				if (!checkboxBean.getSelectedEvents().contains(event.getEvent())) {
					MapInfo info = ConfigReader.maps.get(event.getMapId());
					
					if ((info.getLowLevelRange() >= checkboxBean.getLowLevelBound() &&
							info.getLowLevelRange() <= checkboxBean.getHighLevelBound()) ||
						(info.getHighLevelRange() >= checkboxBean.getLowLevelBound() &&
								info.getHighLevelRange() <= checkboxBean.getHighLevelBound())) {
						String mapName = GW2EventsAPI.mapIdToName.get(info.getMapId());
						
						if (event.isSingleEvent()) {
							mapName = mapName+" - Single";
						}
						
						boolean added = false;
						
						for (MapEvents mapEvent:retStates) {
							if (mapEvent.getMapName().equals(mapName)) {
								mapEvent.getEvents().add(event);
								added = true;
								break;
							}
						}
						
						if (!added) {
							MapEvents mapEvent = new MapEvents(mapName);
							mapEvent.getEvents().add(event);
							
							retStates.add(mapEvent);
						}
					}
				}
			}
		}
	}
}
