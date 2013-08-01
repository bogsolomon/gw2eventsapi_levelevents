package ca.bsolomon.gw2events.level;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.omnifaces.util.Ajax;
import org.primefaces.component.selectmanycheckbox.SelectManyCheckbox;
import org.primefaces.component.treetable.TreeTable;

import ca.bsolomon.gw2event.api.GW2EventsAPI;
import ca.bsolomon.gw2events.level.model.LiveEventState;
import ca.bsolomon.gw2events.level.model.MapInfo;
import ca.bsolomon.gw2events.level.model.Server;

@ManagedBean(name="ajaxPollTree")
@SessionScoped
public class AjaxPollTree {
	
	@ManagedProperty(value="#{checkboxBean}")
	private CheckboxBean checkboxBean;
	
	@ManagedProperty(value="#{levelEventTree}")
	private LevelingEventTree levelTree;
	
	private ConcurrentMap<String, LiveEventState> eventMap = new ConcurrentHashMap<>(70, 0.9f, 1);
	
	private Server serv1;
	private Server serv2;
	private Server serv3;
	
	public void updateEventStatus(TreeTable serv1Table, TreeTable serv2Table, TreeTable serv3Table) {
		if (serv1 == null)
			serv1 = checkboxBean.getServerOne();
				
		if (serv1 != null) {
			for (LiveEventState status:serv1.getEventChains()) {
				if (!checkboxBean.getSelectedEvents().contains(status.getEvent())) {
					MapInfo info = ConfigReader.maps.get(status.getMapId());
					
					if ((info.getLowLevelRange() >= checkboxBean.getLowLevelBound() &&
							info.getLowLevelRange() <= checkboxBean.getHighLevelBound()) ||
						(info.getHighLevelRange() >= checkboxBean.getLowLevelBound() &&
								info.getHighLevelRange() <= checkboxBean.getHighLevelBound())) {
						checkServerEvent(serv1Table, serv2Table, serv3Table, status.getEvent());
					}
				}
			}
		}
	}
	
	private void checkServerEvent(TreeTable serv1Table, TreeTable serv2Table,
			TreeTable serv3Table, String event) {
		Server oldServ1 = serv1;
		Server oldServ2 = serv2;
		Server oldServ3 = serv3;
		
		serv1 = checkboxBean.getServerOne();
		serv2 = checkboxBean.getServerTwo();
		serv3 = checkboxBean.getServerThree();
		
		if (serv1 == oldServ1)
			checkStatusUpdate(serv1Table, event, serv1, levelTree.getServ1ExpandedNodes(), levelTree.getServ1Maps());
		else
			Ajax.update(serv1Table.getClientId());
			
		if (serv2 == oldServ2)
			checkStatusUpdate(serv2Table, event, serv2, levelTree.getServ2ExpandedNodes(), levelTree.getServ2Maps());
		else
			Ajax.update(serv2Table.getClientId());
		
		if (serv3 == oldServ3)
			checkStatusUpdate(serv3Table, event, serv3, levelTree.getServ3ExpandedNodes(), levelTree.getServ3Maps());
		else
			Ajax.update(serv3Table.getClientId());
	}
	
	private void checkStatusUpdate(TreeTable servTable,
			String event, Server serv, List<LiveEventState> selectedMaps, List<String> knownMaps) {
		boolean toUpdate = false;
		//boolean toUpdateRow = false;
		
		String keyName = serv.getServerName()+event;
		
		if (eventMap.containsKey(keyName)) {
			String oldStatus = eventMap.get(keyName).getStatus();
			
			for (LiveEventState evStat:serv.getEventChains()) {
				if (evStat.getEvent().equals(event)) {
					String newStatus = evStat.getStatus();
					
					if (!oldStatus.equals(newStatus)) {
						eventMap.put(keyName, evStat);
						
						String mapName = GW2EventsAPI.mapIdToName.get(evStat.getMapId());
						
						if (evStat.isSingleEvent()) {
							mapName = mapName +" - Single";
						}
						
						LiveEventState search = new LiveEventState("", null, mapName, "", 0, "", false);
						if (selectedMaps.contains(search) || !knownMaps.contains(mapName)) {
							toUpdate = true;
							
							if (!knownMaps.contains(mapName)) {
								knownMaps.add(mapName);
							}
						}
					}
					
					break;
				}
			}
		} else {
			for (LiveEventState evStat:serv.getEventChains()) {
				if (evStat.getEvent().equals(event)) {
					eventMap.put(keyName, evStat);
					String mapName = GW2EventsAPI.mapIdToName.get(evStat.getMapId());
					
					if (evStat.isSingleEvent()) {
						mapName = mapName +" - Single";
					}
					
					LiveEventState search = new LiveEventState("", null, mapName, "", 0, "", false);
					if (selectedMaps.contains(search) || !knownMaps.contains(mapName)) {
						toUpdate = true;
						
						if (!knownMaps.contains(mapName)) {
							knownMaps.add(mapName);
						}
					}
					
					break;
				}
			}
		}
		
		if (toUpdate) {
			Ajax.update(servTable.getClientId());
		}
	}

	public void setCheckboxBean(CheckboxBean checkboxBean) {
		this.checkboxBean = checkboxBean;
	}

	public void setLevelTree(LevelingEventTree levelTree) {
		this.levelTree = levelTree;
	}
}