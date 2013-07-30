package ca.bsolomon.gw2events.level;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.omnifaces.util.Ajax;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.layout.LayoutUnit;
import org.primefaces.event.ResizeEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;

import ca.bsolomon.gw2events.level.model.LiveEventState;
import ca.bsolomon.gw2events.level.model.Server;

@ManagedBean(name="checkboxBean")
@SessionScoped
public class CheckboxBean {

	private static final int SOR_ServID = 1013;
	private static final int SBI_ServID = 1011;
	private static final int MAGUM_ServID = 1005;

	private List<String> selectedEvents = new ArrayList<>();
  
    private Map<String,String> events;  
    
    private Server serverOne;
    private Server serverTwo;
    private Server serverThree;

	private int eastSize = 300;
    private boolean eastCollapsed;
    
    private int lowLevelBound = 0;
    private int highLevelBound = 80;
	
	public CheckboxBean() {  
    	events = new LinkedHashMap<String, String>();  
    	    	
		serverOne = ApiQueryJob.serverEvents.get(SOR_ServID);
		serverTwo = ApiQueryJob.serverEvents.get(SBI_ServID);
		serverThree = ApiQueryJob.serverEvents.get(MAGUM_ServID);
		
		updateEventList();
    }

	public void updateEventList() {
		if (ApiQueryJob.serverEvents.size() > 0) {
			events.clear();
			
			Collection<Server> servers = ApiQueryJob.serverEvents.values();
			Server server = servers.iterator().next();
			
			for (LiveEventState states:server.getEventChains()) {
				events.put(states.getEvent(), states.getEvent());
			}	
		} else {
			events = new LinkedHashMap<>();
		}
	}
	
	public void handleToggle(ToggleEvent event) {
		if (((LayoutUnit)event.getComponent()).getPosition().equals("east")) {
			eastCollapsed = (event.getVisibility() == Visibility.HIDDEN);
		}
	}
	
    public void handleResize(ResizeEvent event) {
    	int newWidth = event.getWidth();
    	if (((LayoutUnit)event.getComponent()).getPosition().equals("east")) {
    		eastSize = newWidth;
    	}
    }
    
    public int getEastSize() {
		return eastSize;
	}

	public void setEastSize(int eastSize) {
		this.eastSize = eastSize;
	}
    
	public List<String> getSelectedEvents() {
		return selectedEvents;
	}

	public void setSelectedEvents(List<String> selectedEvents) {
		this.selectedEvents = selectedEvents;
	}

	public Map<String, String> getEvents() {
		return events;
	}

	public void setEvents(Map<String, String> events) {
		this.events = events;
	}  
	
	public void handleCheckbox(DataTable serv1TempleTable, DataTable serv2TempleTable, DataTable serv3TempleTable) {
		Ajax.update(serv1TempleTable.getClientId());
		Ajax.update(serv2TempleTable.getClientId());
		Ajax.update(serv3TempleTable.getClientId());
	}
	
	public void handleServChange() {  
		
	}
	
	public void handleLevelRangeChange() {  
		
	}
	
	public void clearAll(DataTable serv1TempleTable, DataTable serv2TempleTable, DataTable serv3TempleTable) {  
		selectedEvents.clear();
		
		Ajax.update(serv1TempleTable.getClientId());
		Ajax.update(serv2TempleTable.getClientId());
		Ajax.update(serv3TempleTable.getClientId());
	}

	public boolean isEastCollapsed() {
		return eastCollapsed;
	}

	public void setEastCollapsed(boolean eastCollapsed) {
		this.eastCollapsed = eastCollapsed;
	}
	
    public Collection<Server> getServerIds() {
    	return ApiQueryJob.serverEvents.values();
	}

	public Server getServerOne() {
		if (serverOne != null)
			return serverOne;
		else
			return ApiQueryJob.serverEvents.get(SOR_ServID);
	}

	public void setServerOne(Server server) {
		if (server.equals(serverTwo) || server.equals(serverThree))
			return;
		
		this.serverOne = server;
	}

	public Server getServerTwo() {
		if (serverTwo != null)
			return serverTwo;
		else
			return ApiQueryJob.serverEvents.get(SBI_ServID);
	}

	public void setServerTwo(Server server) {
		if (server.equals(serverOne) || server.equals(serverThree))
			return;
		this.serverTwo = server;
	}

	public Server getServerThree() {
		if (serverThree != null)
			return serverThree;
		else
			return ApiQueryJob.serverEvents.get(MAGUM_ServID);
	}

	public void setServerThree(Server server) {
		if (server.equals(serverOne) || server.equals(serverTwo))
			return;
		this.serverThree = server;
	}

	public int getLowLevelBound() {
		return lowLevelBound;
	}

	public void setLowLevelBound(int lowLevelBound) {
		this.lowLevelBound = lowLevelBound;
	}

	public int getHighLevelBound() {
		return highLevelBound;
	}

	public void setHighLevelBound(int highLevelBound) {
		this.highLevelBound = highLevelBound;
	}
}
