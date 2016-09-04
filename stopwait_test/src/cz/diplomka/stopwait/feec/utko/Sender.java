package cz.diplomka.stopwait.feec.utko;

public class Sender {
	private static final byte CONST = 0;	//konštantná dĺžka ditribúcie rámca
//	private static final byte EXP = 1;		//exponenciálna dĺžka distribúcie rámca
	
	private byte 		currentFrameNo;		//súčasné číslo rámca, mod2 (0 or 1)
	private boolean		timedOut;			//v prípad eže bdue mať sender timeout
	private long 		timeOutPeriod;		//timeout perióda (microseconds)
	private int 		infoFrameLength;	//d+i, INFO rámec dĺžka (bits)
	private int 		ackFrameLength;		//d, ack rámec dĺžka (bits)
	private byte 		lengthDistrib;		//dĺžka distribúcie (informácia) (CONST or EXP)
	private Frame 		frameCopy;			//súčasný rámec, kópia pre retrnasmit
	private FrameStats 	stats;				//štatistika sender rámca
	 
	//konštruktor nastaví sa timeout(True, False, prioda timeoutu, info alebo ackdlžka rámca a 
	//kopia ramca na tue false v prípade že rámec je poškodený alebo zlyhal transmit)
	public Sender(int totalFrameNo, long timeOut, int aLength, int iLength, byte lDistrib) {
		currentFrameNo = 1;	//to make sure first frame sent will have number 0
		timedOut = false;
		timeOutPeriod = timeOut;
		infoFrameLength = iLength;
		ackFrameLength = aLength;
		lengthDistrib = lDistrib;
		frameCopy = null;
		stats = new FrameStats(totalFrameNo);
	}
	//metóda ktorá vráti hodnotu timeoutu
	public boolean isTimedOut() {
		return timedOut;
	}
	
	public void timeOut() {
		timedOut = true;
	}
	
	public long getTimeOutPeriod() {
		return timeOutPeriod;
	}
	
	public int getAckFrameLength() {
		return ackFrameLength;
	}
	
	public int getInfoFrameLength() {
		return infoFrameLength;
	}
	
	public byte getLengthDistrib() {
		return lengthDistrib;
	}
	
	public FrameStats getStats() {
		return stats;
	}
	
	//metóda ktorá vypočíta dĺžku transmitu rámcu podľa vzťahu dĺžka rámca * 1000000 / (b/s)
	public long frameTransTime(int length, long dataRate) {
		return (long)length*1000000 / dataRate;
		//*1000000 because dataRate is in bits per second and result should be in microsecond
		//mustn't divide dataRate by 1000000 because if result is <1 it will be truncated to 0 when converted to long
	}
	
	//pri zlyhaní tansmitu, metóda vytvorí nový rámec podľa typu CONST alebo Exponencial, 
	//vytvorí aktuálnu dlžku rámca a inkrementuje číslo rámca 
	
	public Frame takeNewFrame() {		//creates new frame for transmission
		int frameLength;
		
		//constant data length distribution
		if(lengthDistrib == CONST)
			frameLength = infoFrameLength;
		
		//exponential data length distribution
		else {
			double random;
			do {
				random = Math.random();
			} while(random == 0);		//mustn't be 0
			int medianDataLength = infoFrameLength - ackFrameLength; 
			int actualDataLength = (int)(- medianDataLength * Math.log(random));
			frameLength = ackFrameLength + actualDataLength;
		}
		incFrameNo();	//next frame number (mod2)
		stats.nextFrame();	//resets attemptCounter
		frameCopy = new Frame(currentFrameNo, Frame.INFO, frameLength);
		return new Frame(frameCopy);
	}
	
	//mod2 increment
	private void incFrameNo() {
		if(currentFrameNo == 0)
			currentFrameNo = 1;
		else
			currentFrameNo = 0;
	}
	
	//metóda odošle rámec , nenastaví timeout a vráti rámec
	public Frame send(Frame f) {
		stats.frameSent();
		timedOut = false;
		return f;
	}
	
	//resendne rámec, nastaví mu nové číslo, a vráti kopiu rámca 
	public Frame resend() {
		stats.frameSent();
		timedOut = false;
		return frameCopy;
	}
	
	/*
	 * returns whether ack is ok or not (has error or wrong number): true = ack is ok
	 * if ack is ok send next
	 * if ack is bad do immeadiate resend (don't wait for timeout)
	 */////////////////////////////////check whether to immediately resend or wait for timeout!!!!!!!!!!!!!!!!
	//metóda príjme rámec, ak rámec obsahuje error ho nepríjme (false) inak ho príjme(teda nie je porušený)
	public boolean receive(Frame f) {
		stats.frameReceived(f.hasError());
		if(f.hasError() || (f.getNumber() != frameCopy.getNumber()))
			return false;
		else
			return true;
	}
}
