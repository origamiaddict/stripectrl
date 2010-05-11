package ch.origamiaddict.stripecontrol.stripe;

public class StripeFactory {
	
	public static IStripe createStripe() {
		return new Stripe();
	}
	
	public static IStripe createStripe(int... channels) {
		IStripe s = new Stripe();
		s.setChannels(channels);
		return s;
	}
	
	public static IStripe createStripe(boolean lockChannels, int... channels) {
		IStripe s = new Stripe();
		s.setChannels(channels);
		s.lockChannels(lockChannels);
		return s;
	}
}
