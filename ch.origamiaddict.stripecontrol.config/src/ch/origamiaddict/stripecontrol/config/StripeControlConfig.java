package ch.origamiaddict.stripecontrol.config;

import java.util.ArrayList;
import java.util.Dictionary;

import org.osgi.service.component.ComponentContext;

import ch.origamiaddict.stripecontrol.config.mood.IMood;
import ch.origamiaddict.stripecontrol.config.mood.Mood;
import ch.origamiaddict.stripecontrol.config.service.IStripeControlConfig;
import ch.origamiaddict.stripecontrol.stripe.IStripe;
import ch.origamiaddict.stripecontrol.stripe.StripeFactory;

public class StripeControlConfig implements IStripeControlConfig {

	private ArrayList<IStripe> stripes = new ArrayList<IStripe>();
	private ArrayList<IMood> moods = new ArrayList<IMood>();

	public String[] getStripeNames() {
		ArrayList<String> names = new ArrayList<String>();
		for (IStripe s : stripes) {
			names.add(s.getName());
		}
		return names.toArray(new String[0]);
	}

	public ArrayList<IStripe> getStripes() {
		return stripes;
	}

	public void setStripes(ArrayList<IStripe> stripes) {
		this.stripes = stripes;
	}

	@SuppressWarnings("unused")
	private ComponentContext context;

	@SuppressWarnings("unchecked")
	protected void activate(ComponentContext c) {
		this.context = c;

		Dictionary properties = c.getProperties();

		String stripes = (String) properties.get("config.stripes");
		
		

		if (stripes != null) {

			for (String stripe : stripes.split(";")) {
				String name = null;
				ArrayList<Integer> channels = new ArrayList<Integer>();

				for (String sconfig : stripe.split(",")) {
					String[] keyValue = sconfig.split("=");
					if (keyValue[0].equals("name")) {
						name = keyValue[1];
					}
					if (keyValue[0].equals("channels")) {
						for (String channel : keyValue[1].split(" ")) {
							channels.add(Integer.parseInt(channel));
						}
					}
				}
				if (name == null) {
					name = "No stripe configured";
				}
				// create stripe from config
				IStripe s = StripeFactory.createStripe();
				s.setName(name);
				s.setChannels(channels.toArray(new Integer[0]));
				this.stripes.add(s);

			}
		}
		
		String moodstring = (String) properties.get("config.moods");
		if (moodstring != null) {

			for (String mood : moodstring.split(";")) {
				String[] keyValue = mood.split(",");
				moods.add(new Mood(keyValue[0], keyValue[1]));
			}
		}
		
		System.out.println("activated config");

	}

	protected void deactivate(ComponentContext c) {
		this.context = null;
		System.out.println("deactivated config");
	}

	protected void modified(ComponentContext c) {
		System.out.println("modified config");
	}

	@Override
	public IStripe getStripe(String stripe) {
		for (IStripe s : stripes) {
			if (s.getName().equals(stripe)) {
				return s;
			}
		}
		return null;
	}

	@Override
	public Mood[] getMoods() {	
		return moods.toArray(new Mood[0]);
	}

	@Override
	public void saveMood(int index, String name, String value) {
		if(index > 0) {
			//new mood
			moods.add(new Mood(name, value));
		} else {
			moods.get(index).setName(name);
			moods.get(index).setValue(value);
		}
		
	}

	@Override
	public void deleteMood(int index) {
		moods.remove(index);
	}

}

/*
 * Um eine Service Component mitttels des Config Admin Service zu konfigurieren,
 * reicht es aus, eine entsprechende Konfiguration anzulegen, die als PID den
 * Namen der Service Component besitzt. Aus der Service Component heraus kann
 * dann über die Component Properties auf die entsprechenden Werte zugegriffen
 * werden (vgl. dazu auch im Buch 12.7.1, "Component Properties").
 * ManagedService oder ManagedServiceFactory muss dabei nicht mehr implementiert
 * werden!
 * 
 * Wenn du dazu weitere Informationen suchst, dann seien dir im "OSGi Service
 * Platform Service Compendium" die Abschnitte 112.6 und 112.7 empfohlen, da ist
 * das nämlich spezifiziert.
 */