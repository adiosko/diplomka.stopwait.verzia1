package cz.diplomka.stopwait.feec.utko;

public class FrameStats {
public static final int distribLen = 201;
	
	private int 	totalFrameNo;			//počet rámcov na odoslanie
	private long 	totalReceived;			//počet prijatých rámcov
	private long	corruptedReceived;		//počet porušených rámcov
	private long 	totalSent;				//počet odslaných rámcov
	private long 	attemptCounter;			//výpočet súčasného čísla rámcu pri prenose
	private long 	attemptDistrib[];		//počet všetkých prenosov pre každý rámec
	private int 	attemptDistribIndex;	//súčasný index pre attemptDistrib
	private long	attemptDistrib2[];		//počet rámcov odolaných 1x 2x 3x ... záleží na indexe prenosu 
	
	
	//konstruktor triedy, nastaví počet rámcov, počet prenosov pre rámec, počet rámcov odolsnaých n krát
	public FrameStats(int totalNo) {
		totalFrameNo = totalNo;
		totalReceived = 0;
		corruptedReceived = 0;
		totalSent = 0;
		attemptCounter = 0;
		attemptDistrib = new long[totalFrameNo];
		attemptDistribIndex = -1;
		attemptDistrib2 = new long[distribLen];
		for(int i=0; i<distribLen; i++)
			attemptDistrib2[i] = 0;
	}

	//metóda nastaví ďalší rámec, v prípade že bude index -1 uloží to do premennej attemptCounter a
	//ďalej v prípade že čítač bude menší ako dĺžka všetkých rámcov, tak nastane inkrementácia rámcov
	public void nextFrame() {
		if(attemptDistribIndex != -1) {
			attemptDistrib[attemptDistribIndex] = attemptCounter;
			if(attemptCounter < distribLen)
				attemptDistrib2[(int)attemptCounter]++;
		}
		attemptDistribIndex++;
		attemptCounter = 0;
	}
	
	//metóda na odoslanie rámcu, metóda inkrementuje početpokusov a počet všetkých odolaných rámcov
	public void frameSent() {
		attemptCounter++;
		totalSent++;
	}
	
	//receiver will only use this method, since receiver only counts corrupted info frames
	public void frameReceived(boolean hasError) {
		totalReceived++;
		if(hasError)
			corruptedReceived++;
	}
	
	//getery a setery
	public long[] getAttemptDistrib() {
		return attemptDistrib;
	}

	public long getAttemptCounter() {
		return attemptCounter;
	}

	public void setAttemptCounter(long attemptCounter) {
		this.attemptCounter = attemptCounter;
	}

	public int getAttemptDistribIndex() {
		return attemptDistribIndex;
	}

	public void setAttemptDistribIndex(int attemptDistribIndex) {
		this.attemptDistribIndex = attemptDistribIndex;
	}

	public static int getDistriblen() {
		return distribLen;
	}

	public void setTotalFrameNo(int totalFrameNo) {
		this.totalFrameNo = totalFrameNo;
	}

	public void setTotalReceived(long totalReceived) {
		this.totalReceived = totalReceived;
	}

	public void setCorruptedReceived(long corruptedReceived) {
		this.corruptedReceived = corruptedReceived;
	}

	public void setTotalSent(long totalSent) {
		this.totalSent = totalSent;
	}

	public void setAttemptDistrib(long[] attemptDistrib) {
		this.attemptDistrib = attemptDistrib;
	}

	public void setAttemptDistrib2(long[] attemptDistrib2) {
		this.attemptDistrib2 = attemptDistrib2;
	}

	public long[] getAttemptDistrib2() {
		return attemptDistrib2;
	}
	
	public long getCorruptedReceived() {
		return corruptedReceived;
	}

	public int getTotalFrameNo() {
		return totalFrameNo;
	}

	public long getTotalReceived() {
		return totalReceived;
	}

	public long getTotalSent() {
		return totalSent;
	}

}
