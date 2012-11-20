package fi.harkka;

public interface ISumServer extends Runnable {
	
	int getPort();
	
	int getSum();
	
	int getId();
}
