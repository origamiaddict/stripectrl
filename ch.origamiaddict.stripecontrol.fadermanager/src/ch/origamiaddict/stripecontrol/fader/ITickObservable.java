package ch.origamiaddict.stripecontrol.fader;

public interface ITickObservable {
	    void addObserver(ITickObserver o);
	    void deleteObserver(ITickObserver o);
	    void deleteObservers();
	    int countObservers();

}
