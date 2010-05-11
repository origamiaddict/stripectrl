package ch.origamiaddict.stripecontrol.web.test.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import ch.origamiaddict.stripecontrol.artnet.service.IArtNetService;
import ch.origamiaddict.stripecontrol.config.mood.Mood;
import ch.origamiaddict.stripecontrol.config.service.IStripeControlConfig;
import ch.origamiaddict.stripecontrol.stripe.IStripe;

import com.google.gson.*;

public class AjaxFadeServletTest extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static EventAdmin eventAdmin;
	private static IArtNetService artNet;
	@SuppressWarnings("unused")
	private static ComponentContext componentContext;
	private static IStripeControlConfig config;
	
	 protected void activate( ComponentContext componentContext )
	   {
		 AjaxFadeServletTest.componentContext = componentContext;
	      //String port = componentContext.getBundleContext().getProperty( "org.osgi.service.http.port" );
		 //if( port == null || port.trim().length() == 0 ) port = "80";
		 //System.out.println( "ServletComponent aktiviert: http://localhost:" + port + "/MeinFormular.html" );
	   }
	 

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		// implementation not shown for brevity
		if(request == null) return;
		// get the userId
		//112233
		//012345
		//String iphone = request.getParameter("iphone");
		
		String action = request.getParameter("action");
		
		if(action != null) {
			if(action.equals("setColor")) {
				String color = request.getParameter("color");
				
				String stripe = request.getParameter("stripe");
				Boolean allStripes = Boolean.parseBoolean(request.getParameter("allStripes"));
				
				if(color != null && color.length() == 6 && stripe != null) {

					System.out.println(color);
					short r,g,b;
					try {
						r = Short.parseShort(color.substring(0, 2),16);
						g = Short.parseShort(color.substring(2, 4),16);
						b = Short.parseShort(color.substring(4, 6),16);
					
						sendColor(allStripes, stripe, r,g,b);
						System.out.println("AJAX set color value:");
						System.out.println("R:" + r);
						System.out.println("G:" + g);
						System.out.println("B:" + b);
					
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				
					//System.out.print("iphone"+iphone);
					response.setContentType("text/xml");
					response.setHeader("Cache-Control", "no-cache");		
					response.getWriter().write("<result>"+color+"</result>");
				}
			}
			
			if(action.equals("setBlackout")) {
				String reqBlackout = request.getParameter("blackout");
				
				if(reqBlackout != null && reqBlackout != "") {
					if(reqBlackout.equals("true")) {
						AjaxFadeServletTest.artNet.setBlackout(true);
					} else if (reqBlackout.equals("false")) {
						AjaxFadeServletTest.artNet.setBlackout(false);
					} 
				}
			}
			if(action.equals("getBlackout")) {
				boolean blackout = AjaxFadeServletTest.artNet.isBlackout();
				response.setContentType("text/plain");
				response.setHeader("Cache-Control", "no-cache");		
				response.getWriter().write(""+blackout);	
			}
			
			if(action.equals("stripesList")) {
				String[] stripes;
				
				if(config == null) {
					stripes = new String[] {"No configuration found"};
				} else {
					stripes = config.getStripeNames();
					if(stripes== null || stripes.length == 0) {
						stripes = new String[] {"No stripe configured"};
					}	
				}
				Gson gson = new Gson();
				//application/json
				response.setContentType("text/html");
				response.setHeader("Cache-Control", "no-cache");		
				response.getWriter().write(gson.toJson(stripes));
			}
			if(action.equals("channelsList")){
				String stripeName = request.getParameter("stripe");
				Integer[] channels = null;
				
				if(stripeName != null && stripeName != "") {
					channels = config.getStripe(stripeName).getChannels();	
				}
				if(channels == null) {
					//TODO: error handling
				}
				
				Gson gson = new Gson();
				
				//application/json
				response.setContentType("text/html");
				response.setHeader("Cache-Control", "no-cache");		
				response.getWriter().write(gson.toJson(channels));
			}
			if(action.equals("setChannels")) {
				String stripeName = request.getParameter("stripe");
				String[] channels = request.getParameterValues("channels");
				//Integer[] channels = null;
				ArrayList<Integer> arr = new ArrayList<Integer>();
				for( String ch : channels) {
					arr.add(Integer.parseInt(ch));
				}
				
				if(stripeName != null && stripeName != "") {
					System.out.println("Setting channels to new value" + arr);
					config.getStripe(stripeName).setChannels(arr.toArray(new Integer[0]));
					//todo: update configuration for all modules-> should be made by config admin
					
					Gson gson = new Gson();
					response.setContentType("text/html");
					response.setHeader("Cache-Control", "no-cache");		
					response.getWriter().write(gson.toJson("true"));
				}
			}
			if(action.equals("getMoods")) {
				Mood[] marr = config.getMoods();
				
				if (marr != null) {
					Gson gson = new Gson();
					response.setContentType("text/html");
					response.setHeader("Cache-Control", "no-cache");		
					response.getWriter().write(gson.toJson(marr));
				}
			
			}
			if(action.equals("deleteMood")) {
				String index = request.getParameter("index");
				config.deleteMood(Integer.parseInt(index));
			}
			if(action.equals("saveMood")) {
				String index = request.getParameter("index");
				String name = request.getParameter("name");
				String value = request.getParameter("value");
				config.saveMood(Integer.parseInt(index), name, value);
			}
			if(action.equals("deleteMood")) {
				String index = request.getParameter("index");
				config.deleteMood(Integer.parseInt(index));
			}
		}
		if(action.equals("setStripeName")) {
			String stripeName = request.getParameter("stripe");
			String newStripeName = request.getParameter("name");
			
			if (stripeName!=null && stripeName.trim().length()!=0) {
				if (newStripeName!=null && newStripeName.trim().length()!=0) {
					config.getStripe(stripeName).setName(newStripeName);
				}
			}
		}
		//Todo: HERE IS A BUG IN THE HEX CONVERSION
		if(action.equals("getStripeColor")) {
			String stripeName = request.getParameter("stripe");
			
			if (stripeName!=null && stripeName.trim().length()!=0) {
				Integer[] values = artNet.getValues(config.getStripe(stripeName).getChannels());
				//System.out.println("CHANNEL VALUES:::" + values);
				
				StringBuffer sb = new StringBuffer();
				for(Integer v : values) {
					
					//int to hex
					String hex = Integer.toHexString(v);
					
					//System.out.println("PARSED VALUE: org: " + v + " hex: " + hex);
					if(hex.length() < 2) {
						hex = "0"+hex;
					}
					sb.append(hex);
				}
				Gson gson = new Gson();
				response.setContentType("text/html");
				response.setHeader("Cache-Control", "no-cache");		
				response.getWriter().write(gson.toJson(sb.toString()));
			}
		}
		// check the id. If it is not existing already then return true else
		// false
		/*if ((targetId != null)) {
			response.setContentType("text/xml");
			response.setHeader("Cache-Control", "no-cache");
			response.getWriter().write("<valid>true</valid>");
		} else {
			response.setContentType("text/xml");
			response.setHeader("Cache-Control", "no-cache");
			response.getWriter().write("<valid>false</valid>");
		}*/
	}
	
	

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		doGet(request, response);
	}
	
	private void sendColor(boolean allStripes, String stripe, Short... value) {
		Map<String, String> eventMap = new HashMap<String,String>();
		ArrayList<Integer> channels = new ArrayList<Integer>();
		
		
		if(allStripes) {
			ArrayList<IStripe> stripes = config.getStripes();
			
			
 			if(stripes == null) return;
			for(IStripe s : stripes) {
				for(Integer channel : s.getChannels()) {
					channels.add(channel);
				}
			}
		} else {
			//send color for stripe
			IStripe s = config.getStripe(stripe);
			if(s == null) return;
			for(Integer channel : s.getChannels()) {
				channels.add(channel);
			}	
		}
		int i= 0;
		for(Integer channel: channels) {
			eventMap.put("ch_"+channel, value[i++].toString());
		}
		
		
		Event event = new Event("ch/origamiaddict/stripecontrol/events/dmxdriver/FADER", eventMap);
		if(eventAdmin != null) {
			eventAdmin.postEvent(event);	
		} else {		
			System.out.println("Artnet Service not installed");
		}
			
	}
	
	public void unbindEventAdmin(EventAdmin eventAdmin) {
		AjaxFadeServletTest.eventAdmin = null;
	}
	
	public void bindEventAdmin(EventAdmin eventAdmin) {
		AjaxFadeServletTest.eventAdmin = eventAdmin;
	}
	
	public void bindArtNet(IArtNetService artNet) {
		AjaxFadeServletTest.artNet = artNet;
	}
	
	public void unbindArtNet(IArtNetService artNet) {
		AjaxFadeServletTest.artNet = null;
	}
	
	public void bindConfig(IStripeControlConfig config) {
		AjaxFadeServletTest.config = config;
	}
	
	public void unbindConfig(IStripeControlConfig config) {
		AjaxFadeServletTest.config = null;
	}
	
	/*
	 * <reference bind="bindConfigAdmin" 
   		cardinality="1..1" 
   		interface="org.osgi.service.cm.ConfigurationAdmin" 
   		name="IStripeControlConfig" 
   		policy="dynamic" 
   		unbind="unbindConfigAdmin"/>
	 */
	
	/*public void bindConfigAdmin(ConfigurationAdmin config) {
		this.config = config;
	}
	
	public void unbindConfigAdmin(ConfigurationAdmin config) {
		this.config = null;
	}*/
	
	
}
