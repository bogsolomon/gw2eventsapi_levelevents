package ca.bsolomon.gw2events.level;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import ca.bsolomon.gw2events.level.model.LiveEventState;
import ca.bsolomon.gw2events.level.model.Server;

@ManagedBean(name="levelEventBean")
@SessionScoped
public class LevelingEventBean {
	
	@ManagedProperty(value="#{checkboxBean}")
	private CheckboxBean checkboxBean;

	public void setCheckboxBean(CheckboxBean checkboxBean) {
		this.checkboxBean = checkboxBean;
	}
	
	public List<LiveEventState> getServ1Status() {
		Server serv = checkboxBean.getServerOne();
		
		return serv.getEventChains();
	}
	
	public List<LiveEventState> getServ2Status() {
		Server serv = checkboxBean.getServerTwo();
		
		return serv.getEventChains();
	}
	
	public List<LiveEventState> getServ3Status() {
		Server serv = checkboxBean.getServerThree();
		
		return serv.getEventChains();
	}
}
