/**
 * TextSeer, development prototype for ProcessSeer
    Copyright (C) 2011 Evan Morrison

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * @author Evan Morrison (edm92@uow.edu.au)
 *
 */


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
