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


package textSeer.Model;

public class Gateway extends Vertex {
	
	// To test if gateway : 
	// o.getClass().equals(Gateway.class)
	
	// Actual types 
	public static enum gatetype {
		XOR, 
		AND 
	}
	
	
	
	// Type of this particular gateway
	public gatetype type;
	
	public Gateway(Graph parent){
		super(parent);
	}
	
	public Gateway(Graph parent, String name){
		super(parent, name);
	}
	
}