package gui;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;


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
public class results extends javax.swing.JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public  static JTextArea resultsArea;
	private JScrollPane scrollPane;

	/**
	* Auto-generated main method to display this 
	* JPanel inside a new JFrame.
	*/
	
	
	public results() {
		super();
		initGUI();
	}
	
	private void initGUI() {
		try {
			setPreferredSize(new Dimension(800, 600));
			{
				resultsArea = new JTextArea();
				resultsArea.setText("Text Seer \n --------------- \n");
				resultsArea.setFont(new Font("Courier", Font.PLAIN, 12));
				resultsArea.setLineWrap(true);
				resultsArea.setWrapStyleWord(true);
				resultsArea.setEditable(false);

				scrollPane = new JScrollPane(resultsArea);
				this.add(scrollPane);
				scrollPane.setVerticalScrollBarPolicy(
						JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
				scrollPane.setPreferredSize(new Dimension(800, 600));
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void fixDimensions(Dimension fixDims) {
		scrollPane.setPreferredSize(fixDims);
	}

}
