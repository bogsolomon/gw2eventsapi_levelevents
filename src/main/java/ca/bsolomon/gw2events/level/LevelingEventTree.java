package ca.bsolomon.gw2events.level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.omnifaces.util.Ajax;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ca.bsolomon.gw2event.api.GW2EventsAPI;
import ca.bsolomon.gw2events.level.model.LiveEventState;
import ca.bsolomon.gw2events.level.model.MapEvents;
import ca.bsolomon.gw2events.level.model.MapInfo;
import ca.bsolomon.gw2events.level.model.Server;

@ManagedBean(name="levelEventTree")
@SessionScoped
public class LevelingEventTree {
	
	@ManagedProperty(value="#{checkboxBean}")
	private CheckboxBean checkboxBean;

	private List<LiveEventState> serv1ExpandedNodes = new ArrayList<>();
	private List<LiveEventState> serv2ExpandedNodes = new ArrayList<>();
	private List<LiveEventState> serv3ExpandedNodes = new ArrayList<>();
	
	private List<String> serv1Maps = new ArrayList<>();
	private List<String> serv2Maps = new ArrayList<>();
	private List<String> serv3Maps = new ArrayList<>();
	
	public void setCheckboxBean(CheckboxBean checkboxBean) {
		this.checkboxBean = checkboxBean;
	}
	
	public TreeNode getServ1Tree() {
		Server serv = checkboxBean.getServerOne();
		
		List<MapEvents> retStates = new ArrayList<>();
		
		getServerStates(serv, retStates);
		
		Collections.sort(retStates);
				
		TreeNode serv1Root = new DefaultTreeNode("root", null);
		
		for (MapEvents event:retStates) {
			String mapName = event.getMapName();
			
			LiveEventState map = new LiveEventState("", null, mapName, "", 0, "", false);
			
			TreeNode mapNode = new DefaultTreeNode("map", map, serv1Root);
			
			if (serv1ExpandedNodes.contains(map)) {
				mapNode.setExpanded(true);
			}
			
			for (LiveEventState state:event.getEvents()) {
				TreeNode stateNode = new DefaultTreeNode("event", state, mapNode);
			}
		}
		
		return serv1Root;
	}

	public TreeNode getServ2Tree() {
		Server serv = checkboxBean.getServerTwo();
		
		List<MapEvents> retStates = new ArrayList<>();
		
		getServerStates(serv, retStates);
		
		Collections.sort(retStates);
		
		TreeNode serv2Root = new DefaultTreeNode("root", null);
		
		for (MapEvents event:retStates) {
			String mapName = event.getMapName();
			
			LiveEventState map = new LiveEventState("", null, mapName, "", 0, "", false);
			
			TreeNode mapNode = new DefaultTreeNode("map", map, serv2Root);
			
			if (serv2ExpandedNodes.contains(map)) {
				mapNode.setExpanded(true);
			}
			
			for (LiveEventState state:event.getEvents()) {
				TreeNode stateNode = new DefaultTreeNode("event", state, mapNode);
			}
		}
		
		return serv2Root;
	}
	
	public TreeNode getServ3Tree() {
		Server serv = checkboxBean.getServerThree();
		
		List<MapEvents> retStates = new ArrayList<>();
		
		getServerStates(serv, retStates);
		
		Collections.sort(retStates);
		
		TreeNode serv3Root = new DefaultTreeNode("root", null);
		
		for (MapEvents event:retStates) {
			String mapName = event.getMapName();
					
			LiveEventState map = new LiveEventState("", null, mapName, "", 0, "", false);
			
			TreeNode mapNode = new DefaultTreeNode("map", map, serv3Root);
			
			if (serv3ExpandedNodes.contains(map)) {
				mapNode.setExpanded(true);
			}
			
			for (LiveEventState state:event.getEvents()) {
				TreeNode stateNode = new DefaultTreeNode("event", state, mapNode);
			}
		}
		
		return serv3Root;
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
	
	public void onServ1NodeExpand(NodeExpandEvent event) {  
        serv1ExpandedNodes.add((LiveEventState)event.getTreeNode().getData());
        
        //Ajax.update(event.getTreeNode().getParent().getClientId());
    }  
      
    public void onServ1NodeCollapse(NodeCollapseEvent event) {  
    	serv1ExpandedNodes.remove((LiveEventState)event.getTreeNode().getData());
    }

	public List<LiveEventState> getServ1ExpandedNodes() {
		return serv1ExpandedNodes;
	}
	
	public void onServ2NodeExpand(NodeExpandEvent event) {  
        serv2ExpandedNodes.add((LiveEventState)event.getTreeNode().getData());
    }  
      
    public void onServ2NodeCollapse(NodeCollapseEvent event) {  
    	serv2ExpandedNodes.remove((LiveEventState)event.getTreeNode().getData());
    }

	public List<LiveEventState> getServ2ExpandedNodes() {
		return serv2ExpandedNodes;
	}
	
	public void onServ3NodeExpand(NodeExpandEvent event) {  
        serv3ExpandedNodes.add((LiveEventState)event.getTreeNode().getData());
    }  
      
    public void onServ3NodeCollapse(NodeCollapseEvent event) {  
    	serv3ExpandedNodes.remove((LiveEventState)event.getTreeNode().getData());
    }

	public List<LiveEventState> getServ3ExpandedNodes() {
		return serv3ExpandedNodes;
	}

	public List<String> getServ1Maps() {
		return serv1Maps;
	}

	public List<String> getServ2Maps() {
		return serv2Maps;
	}

	public List<String> getServ3Maps() {
		return serv3Maps;
	}  
}
