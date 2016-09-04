package cz.diplomka.stopwait.feec.utko;

public class Tracing {
private static boolean isOn = true;	//isOn je zapnutý tracing, inak nie 
	
    //metóda nastavená na trasovanie prenosu
	public static boolean isOn() {
		return isOn;
	}
	
	//metóda zapnutie
	public static void enable() {
		isOn = true;
	}
	
	//metóda vypnutie
	public static void disable() {
		isOn = false;
	}
	
	//metóda vypíše trace
	public static void print(String string) {
		System.out.println(string);
	}

}
