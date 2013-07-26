package ca.bsolomon.gw2events.level;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import ca.bsolomon.gw2events.level.model.Server;

@FacesConverter(value = "serverConverter")
public class ServerIDConverter implements Converter{

	@Override
	public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
		
		for (Server serv:ApiQueryJob.serverEvents.values()) {
			if (serv.getServerName().equals(s))
				return serv;
		}
		 
		 return null;
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object o) {
		if (o == null || o.equals("")) {
            return "";
        } else {
        	return ((Server)o).getServerName();
        }
	}

}
