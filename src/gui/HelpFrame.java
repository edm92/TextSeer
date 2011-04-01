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
			Description.setText("<HTML><CENTER>Text Seer v"+std.string.version+"</CENTER><BR>Developed at UOW by DSL group<br>"+std.string.homeWeb+"</HTML>");
			
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
