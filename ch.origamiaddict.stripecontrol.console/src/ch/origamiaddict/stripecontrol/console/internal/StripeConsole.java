package ch.origamiaddict.stripecontrol.console.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import ch.origamiaddict.stripecontrol.artnet.service.IArtNetService;
import ch.origamiaddict.stripecontrol.artnet.utils.DmxChannel;
import ch.origamiaddict.stripecontrol.config.service.IStripeControlConfig;
import ch.origamiaddict.stripecontrol.fadermanager.service.IFaderManager;

@CmdDescr(title="Stripe Control Commands")
public class StripeConsole extends DescriptiveCommandProvider {

	
	private IArtNetService artNetService;
	@SuppressWarnings("unused")
	private ComponentContext context;
	private EventAdmin eventAdmin;
	private IFaderManager faderManager;
	private boolean isColorFaderSetUp = false;
	@SuppressWarnings("unused")
	private IStripeControlConfig config;

	@CmdDescr(description="setchannelvalue <channel> <value> set stripe channel value; range [0-255] [0-255]")
	public void _setchannelvalue(CommandInterpreter ci) {
		try {
			Integer channel = Integer.parseInt(ci.nextArgument());
			Integer value = Integer.parseInt(ci.nextArgument());
			
			artNetService.setValue(new DmxChannel(channel.shortValue(),value.shortValue()));
			
		} catch (NumberFormatException e) {
			ci.println("Wong input format; try [0-255] [0-255]");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@CmdDescr(description="dmxstatus - print driver status")
	public void _dmxstatus(CommandInterpreter ci) {
		String stat = "The ArtNet driver is";
		if (artNetService != null) {
			if (artNetService.isRunning()) {
				ci.println(stat + " running");
			} else {
				ci.println(stat + " not running");
			}
		} else {
			ci.println("ArtNet Driver bundle not active");
		}
	}
	
	@CmdDescr(description="Test OSGi Events")
	public void _eventtest(CommandInterpreter ci) {
		String message = ci.nextArgument();
		Map<String, String> eventMap = new HashMap<String,String>();
		eventMap.put("msg", message);
		Event event = new Event("ch/origamiaddict/stripecontrol/events/TEST", eventMap);
		eventAdmin.postEvent(event);
	}
	
	
	@CmdDescr(description="Test FaderManager color fader factory - [start|stop]")
	public void _rainbowfadetest(CommandInterpreter ci) {
		
		
		if(!isColorFaderSetUp ) {
			faderManager.createRgbColorWheelFader("rainbowfadetest");
			isColorFaderSetUp = true;
		}
		
		
		try {
			String cmd = ci.nextArgument();
			
			if(cmd.equals("start")) {
				faderManager.startFading("rainbowfadetest");
			}
			if(cmd.equals("stop")) {
				faderManager.stopFading("rainbowfadetest");
			}
			
			
		} catch (Exception e) {
			ci.println("Wrong input format" + e);
		}
	}

	protected void activate(ComponentContext context ) {
		   this.context = context;
	   }

	
	
	protected void deactivate(ComponentContext context ) {
		   this.context = null;
	   }
	

	/*@Override
	public String getHelp() {
		StringBuffer sb = new StringBuffer();
		sb.append("---Stripecontrol commands--- \n");
		sb.append("\t dmxstatus - print driver status \n");
		sb.append("\t setchannelvalue [channel] [value] set stripe channel value\n");
		sb.append("\t\t [channel] channel to set [0-255] \n");
		sb.append("\t\t [value] value to set [0-255] \n");
		return sb.toString();
	}*/
	
	
	   public synchronized void bindArtNetService(IArtNetService artNetService) {
		this.artNetService = artNetService;
		System.out.println("artnetservice set");
	}

	   public synchronized void unbindArtNetService(IArtNetService artNetService) {
		this.artNetService = null;
	}

	 public synchronized void bindEventAdmin(EventAdmin eventAdmin) {
		 this.eventAdmin = eventAdmin;
	 }
	 
	 public synchronized void unbindEventAdmin(EventAdmin eventAdmin) {
		 this.eventAdmin = null;
	 }
	 
	 public synchronized void bindFaderManager(IFaderManager faderManager) {
		 this.faderManager = faderManager;
	 }
	 
	 public synchronized void unbindFaderManager(IFaderManager faderManager) {
		 this.faderManager = null;
	 }
	 
	 public synchronized void bindConfig(IStripeControlConfig config) {
		 this.config = config;
	 }

	 public synchronized void unbindConfig(IStripeControlConfig config) {
		 this.config = null;
	 }

}
