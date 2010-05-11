package ch.origamiaddict.stripecontrol.factories;

import java.awt.Color;

import ch.origamiaddict.stripecontrol.queue.StripeQueue;
import ch.origamiaddict.stripecontrol.stripe.IStripe;

public class QueueFactory<S extends IStripe> {
	
	QueueItemFactory<S> qif;
	
	public QueueFactory() {
		super();
		qif = new QueueItemFactory<S>();
	}
	
	public StripeQueue<S> createRandomRgbQueue(int size, boolean loop) {
		StripeQueue<S> q = new StripeQueue<S>();
		for(int i = 0; i < size; i++) {
			q.addItem(qif.createRandomColorRgbItem());	
		}		
		q.setLoop(loop);
		return q;		
	}
	
	
	public StripeQueue<S> createColorWheelRgbQueue() {
		StripeQueue<S> q = new StripeQueue<S>();		
		q.addItem(qif.createDefaultItem(new Color(0,0,0)));
		q.addItem(qif.createDefaultItem(new Color(255,0,0)));		
		q.addItem(qif.createDefaultItem(new Color(0,255,0)));
		q.addItem(qif.createDefaultItem(new Color(255,255,0)));
		q.addItem(qif.createDefaultItem(new Color(0,0,255)));
		q.addItem(qif.createDefaultItem(new Color(255,0,255)));
		q.addItem(qif.createDefaultItem(new Color(0,255,255)));
		q.addItem(qif.createDefaultItem(new Color(255,255,255)));
		return q;
	}

}
