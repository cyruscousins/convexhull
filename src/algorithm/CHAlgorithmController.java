package algorithm;

public interface CHAlgorithmController extends Runnable{
	public void pause();
	public void unpause();
	public void step();
	public void terminate();
	public CHAlgorithm getAlgorithm();
	public void skip();
}
