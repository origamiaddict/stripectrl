package ch.origamiaddict.stripecontrol.fader;

import java.util.ArrayList;
import java.util.TimerTask;


public class StripeTimerTask extends TimerTask implements ITickObservable{
	
	ArrayList<ITickObserver> observers;
	
	
	public StripeTimerTask() {
		super();
		observers = new ArrayList<ITickObserver>();
	}

	@Override
	public void run() {				
		notifyObservers();			
	}

	@Override
	public void addObserver(ITickObserver o) {
		observers.add(o);		
	}

	@Override
	public int countObservers() {
		return observers.size();
	}

	@Override
	public void deleteObserver(ITickObserver o) {
		observers.remove(o);		
	}

	@Override
	public void deleteObservers() {
		observers.clear();		
	}

	private void notifyObservers() {
		for(ITickObserver o: observers) {
			o.tick(this);
		}
	}
	


}
