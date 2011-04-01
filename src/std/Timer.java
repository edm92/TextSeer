package std;

public class Timer {

		  private long start;
		  private long end;

		  public Timer() {
		    reset();
		  }

		  public void start() {
		    start = System.currentTimeMillis();
		  }

		  public void end() {
		    end = System.currentTimeMillis();
		  }

		  public long duration(){
		    return (end-start);
		  }

		  public void reset() {
		    start = 0;  
		    end   = 0;
		  }

		  
		  public String toString(){
			  return this.duration() + " ms";
		  }

}
