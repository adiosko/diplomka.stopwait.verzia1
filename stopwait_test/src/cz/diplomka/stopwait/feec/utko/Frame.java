package cz.diplomka.stopwait.feec.utko;

public class Frame {
	public static final byte INFO = 0;	//info frame type
	public static final byte ACK = 1;	//ack frame type
	
	private byte	number;			//frame number by mod2 (0 or 1)
	private byte	type;			//frame type (INFO or ACK)
	private int 	length;			//total frame length (bits)
	private boolean hasError;		//is frame corrupted
	
	
	//vygenerovanie konstruktoru ramca, bude popísané číslom, typom(INFO alebo ACK), dĺžkou a 
	//typom či ej chybný alebo nie
	public Frame(byte no, byte ty, int len) {
		number = no;
		type = ty;
		length = len;
		hasError = false;
	}
	
	//metoda nastavi ramce na jeho označené číslo, typ a dĺžku
	public Frame(Frame f) {
		this(f.number, f.type, f.length);
	}
	
	
	//metóda nastaví rámec, že je problémový a uloží to do premennej hasError v metóde
	public void setError(boolean err) {
		hasError = err;
	}
	
	//gettery a settery premenných triedy
	public byte getNumber() {
		return number;
	}
	
	public byte getType() {
		return type;
	}
	
	public int getLength() {
		return length;
	}
	
	public boolean hasError() {
		return hasError;
	}
	
	public boolean isHasError() {
		return hasError;
	}

	public void setHasError(boolean hasError) {
		this.hasError = hasError;
	}

	public static byte getInfo() {
		return INFO;
	}

	public static byte getAck() {
		return ACK;
	}

	public void setNumber(byte number) {
		this.number = number;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public void setLength(int length) {
		this.length = length;
	}
}
