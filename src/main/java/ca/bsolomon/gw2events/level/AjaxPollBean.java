package ca.bsolomon.gw2events.level;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.omnifaces.util.Ajax;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.panel.Panel;

import ca.bsolomon.gw2events.level.model.LiveEventState;
import ca.bsolomon.gw2events.level.model.Server;

@ManagedBean(name="ajaxPollBean")
@SessionScoped
public class AjaxPollBean {
	
	@ManagedProperty(value="#{checkboxBean}")
	private CheckboxBean checkboxBean;
	
	private ConcurrentMap<String, LiveEventState> eventMap = new ConcurrentHashMap<>(70, 0.9f, 1);
	
	private Server serv1;
	private Server serv2;
	private Server serv3;
	
	public void updateEventStatus(DataTable serv1Table, DataTable serv2Table, DataTable serv3Table) {
		if (serv1 == null)
			serv1 = checkboxBean.getServerOne();
				
		if (serv1 != null) {
			for (LiveEventState status:serv1.getEventChains()) {
				if (!checkboxBean.getSelectedEvents().contains(status.getEvent())) {
					checkServerEvent(serv1Table, serv2Table, serv3Table, status.getEvent());
				}
			}
		}
	}

	private void checkServerEvent(DataTable serv1Table, DataTable serv2Table,
			DataTable serv3Table, String event) {
		Server oldServ1 = serv1;
		Server oldServ2 = serv2;
		Server oldServ3 = serv3;
		
		serv1 = checkboxBean.getServerOne();
		serv2 = checkboxBean.getServerTwo();
		serv3 = checkboxBean.getServerThree();
		
		if (serv1 == oldServ1)
			checkStatusUpdate(serv1Table, event, serv1);
		else
			Ajax.update(serv1Table.getClientId());
			
		if (serv2 == oldServ2)
			checkStatusUpdate(serv2Table, event, serv2);
		else
			Ajax.update(serv2Table.getClientId());
		
		if (serv3 == oldServ3)
			checkStatusUpdate(serv3Table, event, serv3);
		else
			Ajax.update(serv3Table.getClientId());
	}
	
	private void checkStatusUpdate(DataTable servTable,
			String event, Server serv) {
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
						//toUpdateRow = true;
						toUpdate = true;
					}
				}
			}
		} else {
			for (LiveEventState evStat:serv.getEventChains()) {
				if (evStat.getEvent().equals(event)) {
					eventMap.put(keyName, evStat);
					toUpdate = true;
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
}