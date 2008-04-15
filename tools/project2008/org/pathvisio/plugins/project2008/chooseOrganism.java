package org.pathvisio.plugins.project2008;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.pathvisio.data.DataException;
import org.pathvisio.model.ConverterException;


public class chooseOrganism {
	
	
	
	static private class OrganismDialog
	{
		public JPanel canvasButtons(){
			
			// create a new panel
			JPanel canvasButtons = new JPanel();
			
			// create two new buttons, using the makeButton method
			JButton CeButton = showMenuGUI.makeBigButton("Caenorhabditis elegans");
			JButton DrButton = showMenuGUI.makeBigButton("Drosophila melanogaster");
			JButton HsButton = showMenuGUI.makeBigButton("Homo sapiens");
			JButton MmButton = showMenuGUI.makeBigButton("Mus musculus");
			JButton RnButton = showMenuGUI.makeBigButton("Rattus norvegicus");
			JButton ScButton = showMenuGUI.makeBigButton("Saccharomyces cerevisiae");
			
			// add the functionality to the Pathway overlap Matrix button
			CeButton.addActionListener(
					new ActionListener(){
						public void actionPerformed(ActionEvent ae){
							System.out.println("CE");
							org=0;
							
							}
						}
					);
			
			// add the functionality to the Pathway overlap Matrix button
			DrButton.addActionListener(
					new ActionListener(){
						public void actionPerformed(ActionEvent ae){
							System.out.println("DR");
							org=1;
							}
						}
					);
			
			// add the functionality to the Pathway overlap Matrix button
			HsButton.addActionListener(
					new ActionListener(){
						public void actionPerformed(ActionEvent ae){
							System.out.println("HS");
							org=2;
							}
						}
					);
			
			// add the functionality to the Pathway overlap Matrix button
			MmButton.addActionListener(
					new ActionListener(){
						public void actionPerformed(ActionEvent ae){
							System.out.println("MM");
							org=3;
							}
						}
					);
			
			// add the functionality to the Pathway overlap Matrix button
			RnButton.addActionListener(
					new ActionListener(){
						public void actionPerformed(ActionEvent ae){
							System.out.println("RN");
							org=4;
							
							}
						}
					);
			
			// add the functionality to the Pathway overlap Matrix button
			ScButton.addActionListener(
					new ActionListener(){
						public void actionPerformed(ActionEvent ae){
							System.out.println("SC");
							org=5;
							
							}
						}
					);

			// add the buttons to the canvas
			canvasButtons.add(CeButton);
			canvasButtons.add(DrButton);
			canvasButtons.add(HsButton);
			canvasButtons.add(MmButton);
			canvasButtons.add(RnButton);
			canvasButtons.add(ScButton);
			
			return canvasButtons;
		}
		
		public int org =-1;
	}
	
	
	
	public static void getOrganism(){
		
		final JDialog dialog = new JDialog((JFrame)null, true);
		
		dialog.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
		OrganismDialog dlg = new OrganismDialog();
		JPanel canvasButtons=dlg.canvasButtons();
		JPanel menuCloseButtons=menuCloseButtons(dialog);
		
		dialog.add(canvasButtons, BorderLayout.CENTER);
		dialog.add(menuCloseButtons, BorderLayout.SOUTH);
		
		dialog.setSize(400,300);
		
		dialog.setVisible(true);
		
		if(dlg.org>=0){
			String[] kindOfOrganism = getOrganismInfo (dlg.org);
			System.out.println ("organisme = "+kindOfOrganism);
		}
		

	
	}
	
	
	
	
	
	public static JPanel menuCloseButtons(final JDialog dialog){
	  	  // create a new panel
	  	   JPanel canvasButtons = new JPanel();
	  		
	  	  // create two new buttons, using the makeButton method
	  		JButton menuButton = GoTermDistributionGUI.makeButton("Back to menu");
	  		final JButton closeButton = GoTermDistributionGUI.makeButton("Close");
	  		
	  		// add the functionality to the close button
	  		closeButton.addActionListener(
	  				new ActionListener(){
	  					public void actionPerformed(ActionEvent ae){
	  						System.exit(0);
	  						
	  						}
	  					}
	  				);
	  		
	  		// add the functionality to the calculate button
	  		menuButton.addActionListener(
	  				new ActionListener(){
	  					public void actionPerformed(ActionEvent ae){
	  						dialog.dispose();
	  						System.out.println("Go to Menu");
	  						}
	  					}
	  				);
	  		
	  		// add the buttons to the canvas
	  		canvasButtons.add(menuButton);
	  		canvasButtons.add(closeButton);	
	  		
	  	  return canvasButtons;
	    }
	
	
	

		
		
	
	
	
	public static String[]getOrganismInfo (int organism){
		
		String[][] annimalNames=new String[6][];
		annimalNames[0]=new String[]{"Ce_20070902.pgdb","\\Caenorhabditis_elegans"};
		annimalNames[1]=new String[]{"Dr_20070817.pgdb","\\Drosophila_melanogaster"};
		annimalNames[2]=new String[]{"Hs_41_36c.pgdb","\\Homo_sapiens"};
		annimalNames[3]=new String[]{"Mm_38_35.pgdb","\\Mus_musculus"};
		annimalNames[4]=new String[]{"Rn_39_34i.pgdb","\\Rattus_norvegicus"};
		annimalNames[5]=new String[]{"Sc_41_1d.pgdb","\\Saccharomyces_cerevisiae"};
		
		String[]organismInfo=annimalNames[organism];
		
		return organismInfo;
		
	}

	
	
	
}

