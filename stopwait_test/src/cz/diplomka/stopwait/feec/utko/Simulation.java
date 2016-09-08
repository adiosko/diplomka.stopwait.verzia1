package cz.diplomka.stopwait.feec.utko;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Simulation {
public static void main(String[] args) throws IOException {
		
		//Pridanie inputu (vypýta si či chceme defaultne hodnoty rámca alebo custom hodnoty)
		Input input = new Input();
		input.read();
		
		//vytvorenie kanálu s probability datarate a delay z inputu
		Channel channel = new Channel(input.getBitErrProb(), input.getDataRate(), input.getPropDelay());
		
		//sender nastaví číslo rámca, timeout periodu, dlžku ACK rámcu a INFO rámcu s ich dňlžkou
		Sender sender = new Sender(input.getTotalFrameNo(), input.getTimeOutPeriod(), input.getAckFrameLength(), input.getInfoFrameLength(), input.getLengthDistrib());
		//receiver nastaví počet rámcov na prijatie, 
		Receiver receiver = new Receiver(input.getTotalFrameNo(), input.getProcTime(), input.getAckFrameLength());
		//vytvorenie štatistík
		TimeStats time = new TimeStats();
		
		//výpis šírky pásma, delay, čas spracovania dát receivera, počet prijatých ACK rámcov (počet bitov) receivera,
		//počet prijatých INFO rámcov (počet bitov) receivera, dňlžku distribúcie rámcov sendera, prvadepodobnosť chyby kanálu, počet prijatých rámcov a spracovaný čas
		System.out.println("\nData rate is \t\t\t\t\t" + channel.getDataRate() + " bps.");
		System.out.println("Propagation delay is \t\t\t\t" + channel.getPropDelay() + " microseconds.");
		System.out.println("Info frame pocessing time is \t\t\t" + receiver.getProcTime() + " microseconds.");
		System.out.println("Acknowledgement frame length is \t\t" + receiver.getAckFrameLength() + " bits.");
		System.out.println("Info frame length is \t\t\t\t" + sender.getInfoFrameLength() + " bits");
		System.out.println("Data length distribution is \t\t\t" + (sender.getLengthDistrib() == 0 ? "constant." : "exponential."));
		System.out.format("Probability of bit error is \t\t\t%f.%n", channel.getBitErrProb());
		System.out.println("Time-out period is \t\t\t\t" + sender.getTimeOutPeriod() + " microseconds.");
		System.out.println("Total number of frames for transmission is \t" + sender.getStats().getTotalFrameNo() + ".");
		System.out.println("Elapsed time is \t\t\t\t" + time.getElapsedTime() + " microseconds.");

		
		//totalFrames si vypýta počet rámcov sendera vytvorí rámec f a pozoruje všetky rámce, ak je rámec OK  
		int i = 0;
		int totalFrames = sender.getStats().getTotalFrameNo();
		Frame f;
		boolean corrupted;		//is info frame corrupted
		boolean ackOk = true;	//in order to send new frame, and not do a resend
		long timeToAdd;
		
		//rámec po prijatí získa štatistiky a vytvorí nový rámec ak je rámec timeoutovaný resend inak vytvorí nový rámec a odošle ho kanálom
		receiver.getStats().nextFrame();
		/////////////////////////(check whether sender, after receiving bad ack, immediately resends, or waits for time out)
		while(i < totalFrames) {
			if(sender.isTimedOut() || !ackOk) {
				f = sender.resend();	//if sender timed-out or received bad ack - resend
			} else {
				f = sender.takeNewFrame();	//send a new frame
				f = sender.send(f);
			}
			//táto časť kódu pridá senderu dňlžku a datarate  a odošle rámec
			timeToAdd = sender.frameTransTime(f.getLength(), channel.getDataRate());
			time.add(timeToAdd);
			
			//transmit skor
			f = channel.transfer(f);
			
			timeToAdd = channel.getPropDelay();
			time.add(timeToAdd);
			
			corrupted = receiver.receive(f);
			
			timeToAdd = receiver.getProcTime();
			time.add(timeToAdd);
			
			//táto časť kódu má na starosti, že ak je rámec špatný, pridelí mu timeout, inak mu anstaví paramtre a príjme rámec na strane receivera
			if(corrupted) {
				//don't send ack, make sender time-out
				sender.timeOut();

				timeToAdd = sender.getTimeOutPeriod();
				time.add(timeToAdd);
			} else {
				f = receiver.sendAck();
				
				timeToAdd = receiver.frameTransTime(f.getLength(), channel.getDataRate());
				time.add(timeToAdd);
				
				f = channel.transfer(f);
				
				timeToAdd = channel.getPropDelay();
				time.add(timeToAdd);
				
				ackOk = sender.receive(f);
				
				if(ackOk) {	//frame is successfully sent and ack is successfully received
					i++;	//so we can send next frame
					receiver.getStats().nextFrame();
				}
			}
		}
		
		sender.getStats().nextFrame();	//to store stats for last frame sent

		
		System.out.println("\nPress 'Enter' to show results...");
		new BufferedReader(new InputStreamReader(System.in)).readLine();

		
		//printing results and statistics part
		
		int totalNo = sender.getStats().getTotalFrameNo();
		int infoLen = sender.getInfoFrameLength();
		int ackLen = receiver.getAckFrameLength();
		long rate = channel.getDataRate();
		long infoTransTime = sender.frameTransTime(infoLen, rate);
		long ackTransTime = receiver.frameTransTime(ackLen, rate);
		long timeOut = sender.getTimeOutPeriod();
		double frameErrProb = channel.getFrameErrorProbability(infoLen);
		long propDelay = channel.getPropDelay();
		long procTime = receiver.getProcTime();
		
		double ts = time.throughputSim(totalNo, infoTransTime);
		double tt = time.throughputTheor(infoTransTime,ackTransTime ,timeOut , frameErrProb, propDelay, procTime);
		
		System.out.print("\nResults:");
		time.throughputEval(ts, tt);
		
		System.out.println("\nStatistics:");
		System.out.println("\tTotal elapsed time: \t\t\t\t" + time.getElapsedTime() + " microseconds.");
		System.out.println("\tNumber of info frames to send: \t\t\t" + totalNo);
		System.out.println("\tTotal number of info frames actually sent: \t" + sender.getStats().getTotalSent());
		System.out.println("\tNumber of corrupted info frames: \t\t" + receiver.getStats().getCorruptedReceived());
		System.out.println("\tNumber of corrupted ack frames: \t\t" + sender.getStats().getCorruptedReceived());
		System.out.println("\n\tDistribution of info frames transmission attempts (#attempt count: frame count):");
		for(int j=1; j<FrameStats.distribLen; j++) {
			if(((j-1) % 10) == 0) {
				System.out.println();
			}
			System.out.format("#%3d: %6d  |  ", j, sender.getStats().getAttemptDistrib2()[j]);
		}
		System.out.println("");
		
		System.out.println("\n\tDistribution of ack frames transmission attempts (#attempt count: frame count):");
		for(int j=1; j<11; j++) {
			if(((j-1) % 10) == 0) {
				System.out.println();
			}
			System.out.format("#%3d: %6d  |  ", j, receiver.getStats().getAttemptDistrib2()[j]);
		}
		System.out.println("");
        
		
		
		
		
/*		System.out.println("\n\tDistribution of info frames transmission attempts (#frame number: attempt count):");
		for(int j=0; j<totalNo; j++) {
			if((j % 10) == 0) {
				System.out.println();
				System.out.print("\t\t");
			}
			System.out.format("#%03d: %2d   ", j, sender.getStats().getAttemptDistrib()[j]);
		}
		System.out.println("");
*/		
		
		
/*		boolean calcOptLen = input.calculateOptLength();	//do we want to calculate optimal info frame length
		if(!calcOptLen)
			return;
		int optLen = time.optInfoLength(input);
		System.out.println("/nOptimal info frame length is " + optLen + " bits.");
*/		
	}
}
