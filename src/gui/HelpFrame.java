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

package gui;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import javax.swing.WindowConstants;

import boot.GuiEntry;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class HelpFrame extends javax.swing.JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	* Auto-generated main method to display this JFrame
	*/
	GuiEntry parent;
	
	
	public HelpFrame(GuiEntry thisParent) {
		super();
		parent = thisParent;
		initGUI();
	}
	
	private void initGUI() {
		try {
			BoxLayout thisLayout = new BoxLayout(getContentPane(), javax.swing.BoxLayout.X_AXIS);
			getContentPane().setLayout(thisLayout);
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			setIconImage(new ImageIcon(std.objects.iconFile).getImage());
			setTitle("TextSeer Help and About");
			
			
			JLabel HelpIcon = new JLabel(); 
			HelpIcon.setIcon(new ImageIcon(std.objects.iconFile));
			
			JLabel Description = new JLabel();
			Description.setText("<HTML><CENTER>Text Seer v"+std.string.version+"</CENTER><BR>Developed at UOW by DSL group<br>"+std.string.homeWeb+"<br/>"+std.string.license+"</HTML>");
			
			JButton linktoDSL = new JButton("Load Site");
			linktoDSL.setActionCommand("linkToDSL");
			linktoDSL.addActionListener(parent);
			add(new JLabel("   "));
			add(Description);
			add(linktoDSL);
			add(Box.createHorizontalGlue());
			add(HelpIcon);
			pack();
			this.setMinimumSize(this.getSize());
			
			setSize(400, 200);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
