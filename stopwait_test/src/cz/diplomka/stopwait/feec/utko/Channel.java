package cz.diplomka.stopwait.feec.utko;

public class Channel {
	private double 	bitErrProb;				//probability of bit error (0.0 - <1.0)
	private long 	dataRate;				//channel data rate (bits per second)
	private long 	propDelay;				//propagation delay (microseconds)
	
	
	//Popísanie parametrov triedy Channel, kde bitErrProb je pravdepodbnost chyb, data rate je prenosova rych
	//lost a Delay je anstavenie hodnoty Delay == konstruktor triedy
	public Channel(double prob, long rate, long delay) {
		bitErrProb = prob;
		dataRate = rate;
		propDelay = delay;
	}
	
	//metoda na vypocet pravdepodnosti chyby podla vztahu 1 - (1-pravdepodbnsotchyby * dlzka ramca)
	public double getFrameErrorProbability(int frameLength) {
		return 1.0 - Math.pow( (1.0 - bitErrProb), frameLength );
	}
	
	
	//metoda na vzniknutie chybneho ramca pre vygenerovanie random ramca a nastavenie chyby ramca
	private void corrupt(Frame f) {
		boolean errorOccured = Math.random() <= getFrameErrorProbability(f.getLength()) ? true : false;
		f.setError(errorOccured);
	}
	/////////////////////////////////////check whether channel corrupts ACK frames!!!!!!!!!!!!!
	//metoda na poskodenie ramca z triedy frame
	public Frame transfer(Frame f) {
//		if(f.getType() == Frame.ACK)
//			return f;
		corrupt(f);
		return f;
	}
	
	//getery a settery
	public double getBitErrProb() {
		return bitErrProb;
	}
	
	public long getDataRate() {
		return dataRate;
	}
	
	public long getPropDelay() {
		return propDelay;
	}

	public void setBitErrProb(double bitErrProb) {
		this.bitErrProb = bitErrProb;
	}

	public void setDataRate(long dataRate) {
		this.dataRate = dataRate;
	}

	public void setPropDelay(long propDelay) {
		this.propDelay = propDelay;
	}
	
	
	}
