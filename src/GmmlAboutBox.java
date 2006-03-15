import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;

public class GmmlAboutBox extends Dialog
{
	
	private static final long serialVersionUID = 1L;

	public GmmlAboutBox(Shell parent) 
	{
		super (parent);
	}

	public GmmlAboutBox(Shell parent, int style) 
	{
		super (parent, style);
	}
	
	public void open()
	{
		Shell parent = getParent();
		final Shell shell = new Shell (parent, SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL);

		shell.setText ("About GMML-Vision");		
		GridLayout ly = new GridLayout();
		ly.numColumns = 2;
		shell.setLayout (ly);
		
		Label lbl = new Label (shell, SWT.NULL);
		lbl.setText ("GMML-Vision");
		GridData gd = new GridData (GridData.HORIZONTAL_ALIGN_CENTER);
		gd.horizontalSpan = 2;		
		lbl.setLayoutData (gd);
		
		lbl = new Label (shell, SWT.NULL);
		Image img = new Image (shell.getDisplay(), "images/logo.jpg");
		lbl.setImage (img);

		lbl = new Label (shell, SWT.NULL);
		lbl.setText ("R.M.H. Besseling\nS.P.M.Crijns\nI.Kaashoek\nM.M.Palm\n" +
				"E.D Pelgrim\nT.A.J. Kelder\nM.P. van Iersel\n\nBiGCaT");
		
		final Button btnOk = new Button (shell, SWT.PUSH);
		btnOk.setText ("OK");
		gd = new GridData (GridData.HORIZONTAL_ALIGN_CENTER);
		gd.horizontalSpan = 2;
		gd.widthHint = 60;
		btnOk.setLayoutData (gd);
		
		btnOk.addListener(SWT.Selection, new Listener() {
			public void handleEvent (Event event) {
					shell.dispose();
			}
		});
			
		shell.pack();
		shell.open();
		
		Display display = parent.getDisplay();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();			
		}
		
		img.dispose();
	}
}
