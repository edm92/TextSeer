package gui;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import processBuilding.process;

public class showProcesses {
	
	public static process selectProcess(){
		process p= null;
		
		process[] possibilities = new process[boot.programEntry.myprocesses.size()]; 
		String[] possibility = new String[boot.programEntry.myprocesses.size()];
		int i = 0;
		for(process pp: boot.programEntry.myprocesses){
			possibilities[i] = pp;
			possibility[i] = pp.name;
			i++;
		}
		
		String s = (String)JOptionPane.showInputDialog(
		                    null,
		                    "Select the process you wish to view:\n",
		                    "Process Selection",
		                    JOptionPane.PLAIN_MESSAGE,
		                    new ImageIcon(std.objects.iconFile),
		                    possibility,
		                    "ham");

		//If a string was returned, say so.
		if ((s != null) && (s.length() > 0)) {
			int j = 0;
			for(String ss: possibility){
				if(ss.compareTo(s) == 0){
					p = possibilities[j];
					break;
				}
				j++;
			}

		    return p;
		}
		
		return p;
	}

}
