package ch.origamiaddict.stripecontrol.config.service;

import java.util.ArrayList;
import ch.origamiaddict.stripecontrol.config.mood.Mood;
import ch.origamiaddict.stripecontrol.stripe.IStripe;

public interface IStripeControlConfig {
	
	public String[] getStripeNames();
	
	public ArrayList<IStripe> getStripes();

	public void setStripes(ArrayList<IStripe> stripes);

	public IStripe getStripe(String stripe);
	
	public Mood[] getMoods();
	public void saveMood(int index, String name, String value);
	public void deleteMood(int index);
}