package cz.diplomka.stopwait.feec.utko;

public class TimeStats {
	private long elapsedTime;		//total elapsed time
	
	//konštruktor nastaví čas na hodnotu 0
	public TimeStats() {
		elapsedTime = 0;
	}
	
	//getter
	public long getElapsedTime() {
		return elapsedTime;
	}
	
	//metóda an pridanie času
	public void add(long time) {
		elapsedTime += time;
	}
	
	//metóda na simulovanie priepustnosti siete
	public double throughputSim(int framesToSendNo, long infoTransTime) {
		return ((double)(framesToSendNo * infoTransTime)) / elapsedTime;
	}
	
	//metóda na výpočet teoretickej priepustnosti na základe (informatívny čas transmitu támca)
	// /(timeOut + infoTransTime) * frameErrProb / (1.0 - frameErrProb) + infoTransTime + 2 * propDelay + procTime + ackTransTime;
	public double throughputTheor(long infoTransTime, long ackTransTime, long timeOut, double frameErrProb, long propDelay, long procTime) {
		double a = (timeOut + infoTransTime) * frameErrProb / (1.0 - frameErrProb);
		double b = infoTransTime + 2 * propDelay + procTime + ackTransTime;
		return (double)infoTransTime / (a + b);
	}
	
	//metóda porovnanie teoretickej a praktickej priepustnosti vyjadrená v %
	public void throughputEval(double throughputSim, double throughputTheor) {
		System.out.format("%n\tSimulated throughput:\t %05.2f%%%n", throughputSim * 100);
		System.out.format("\tTheoretical throughput:\t %05.2f%%%n", throughputTheor * 100);
	}
	
	//výpočet optimálnej dĺžky rámca, na základe prijatej pravdepodbnosti erroru skusit 64 512 1280 1500
	public int optInfoLength(Input in) {
		return 8192;
	}
}
