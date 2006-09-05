package graphics;

import java.awt.BasicStroke;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

import util.SwtUtils;
import data.*;
 
/**
 * This class implements and handles a line
 */
public class GmmlLine extends GmmlGraphics
{
	//TODO: make private
	public GmmlHandle handleStart;
	public GmmlHandle handleEnd;
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor for this class
	 * @param canvas - the GmmlDrawing this line will be part of
	 */
	public GmmlLine(GmmlDrawing canvas, GmmlDataObject o)
	{
		super(canvas, o);
		drawingOrder = GmmlDrawing.DRAW_ORDER_LINE;
		handleStart	= new GmmlHandle(GmmlHandle.DIRECTION_FREE, this, canvas);
		handleEnd	= new GmmlHandle(GmmlHandle.DIRECTION_FREE, this, canvas);
		setHandleLocation();
	}
		
	protected void draw(PaintEvent e, GC buffer)
	{
		double endx = gdata.getEndX();
		double endy = gdata.getEndY();
		double startx = gdata.getStartX();
		double starty = gdata.getStartY();

		Color c = null;
		if (isSelected())
		{
			c = SwtUtils.changeColor(c, selectColor, e.display);
		}
		else 
		{
			c = SwtUtils.changeColor(c, gdata.getColor(), e.display);
		}
		buffer.setForeground (c);
		buffer.setBackground (c);
		
		buffer.setLineWidth (1);
		int ls = gdata.getLineStyle();
		if (ls == LineStyle.SOLID)
		{
			buffer.setLineStyle (SWT.LINE_SOLID);
		}
		else if (ls == LineStyle.DASHED)
		{ 
			buffer.setLineStyle (SWT.LINE_DASH);
		}			

		double s = Math.sqrt(((endx-startx)*(endx-startx)) + ((endy - starty)*(endy - starty)));
		
		switch (gdata.getLineType())
		{
		
			case LineType.LINE:
				buffer.drawLine ((int)startx, (int)starty, (int)endx, (int)endy);
				break;
			case LineType.ARROW:				
				buffer.drawLine ((int)startx, (int)starty, (int)endx, (int)endy);
				drawArrowhead(buffer);
				break;
			case LineType.TBAR:
			{
				s /= 8;
	
				double capx1 = ((-endy + starty)/s) + endx;
				double capy1 = (( endx - startx)/s) + endy;
				double capx2 = (( endy - starty)/s) + endx;
				double capy2 = ((-endx + startx)/s) + endy;
	
				buffer.drawLine ((int)startx, (int)starty, (int)endx, (int)endy);
				buffer.drawLine ((int)capx1, (int)capy1, (int)capx2, (int)capy2);
			}
				break;
			case LineType.LIGAND_ROUND:
			{
				double dx = (endx - startx)/s;
				double dy = (endy - starty)/s;
							
				buffer.drawLine ((int)startx, (int)starty, (int)(endx - 6 * dx), (int)(endy - 6 * dy));
				buffer.drawOval ((int)endx - 5, (int)endy - 5, 10, 10);
				buffer.fillOval ((int)endx - 5, (int)endy - 5, 10, 10);
			}
				break;
			case LineType.RECEPTOR_ROUND:
			{
				// TODO: this code is not safe for division by zero!
				double theta 	= Math.toDegrees(Math.atan((endx - startx)/(endy - starty)));
				double dx 		= (endx - startx)/s;
				double dy 		= (endy - starty)/s;	
				
				buffer.drawLine ((int)startx, (int)starty, (int)(endx - (8*dx)), (int)(endy - (8*dy)));
				buffer.drawArc ((int)endx - 8, (int)endy - 8, 16, 16, (int)theta + 180, -180);			
			}
				break;
			case LineType.RECEPTOR_SQUARE:
			{
				s /= 8;
				
				double x3 		= endx - ((endx - startx)/s);
				double y3 		= endy - ((endy - starty)/s);
				double capx1 	= ((-endy + starty)/s) + x3;
				double capy1 	= (( endx - startx)/s) + y3;
				double capx2 	= (( endy - starty)/s) + x3;
				double capy2 	= ((-endx + startx)/s) + y3;			
				double rx1		= capx1 + 1.5*(endx - startx)/s;
				double ry1 		= capy1 + 1.5*(endy - starty)/s;
				double rx2 		= capx2 + 1.5*(endx - startx)/s;
				double ry2 		= capy2 + 1.5*(endy - starty)/s;
			
				buffer.drawLine ((int)startx, (int)starty, (int)x3, (int)y3);
				buffer.drawLine ((int)capx1, (int)capy1, (int)capx2, (int)capy2);
				buffer.drawLine ((int)capx1, (int)capy1, (int)rx1, (int)ry1);
				buffer.drawLine ((int)capx2, (int)capy2, (int)rx2, (int)ry2);
			}
				break;
			case LineType.LIGAND_SQUARE:
			{
				s /= 6;
				double x3 		= endx - ((endx - startx)/s);
				double y3 		= endy - ((endy - starty)/s);
	
				int[] points = new int[4 * 2];
				
				points[0] = (int) (((-endy + starty)/s) + x3);
				points[1] = (int) ((( endx - startx)/s) + y3);
				points[2] = (int) ((( endy - starty)/s) + x3);
				points[3] = (int) (((-endx + startx)/s) + y3);
	
				points[4] = (int) (points[2] + 1.5*(endx - startx)/s);
				points[5] = (int) (points[3] + 1.5*(endy - starty)/s);
				points[6] = (int) (points[0] + 1.5*(endx - startx)/s);
				points[7] = (int) (points[1] + 1.5*(endy - starty)/s);
				
				buffer.drawLine ((int)startx, (int)starty, (int)x3, (int)y3);
				buffer.drawPolygon(points);
				buffer.fillPolygon(points);
			}
				break;
		}
		
		c.dispose();

		
	}
	
	protected void draw(PaintEvent e)
	{
		draw(e, e.gc);
	}
	
	protected boolean isContain(Point2D p)
	{
		BasicStroke stroke = new BasicStroke(10);
		Shape outline = stroke.createStrokedShape(getLine());
		return outline.contains(p);
	}

	protected boolean intersects(Rectangle2D.Double r)
	{
		BasicStroke stroke = new BasicStroke(10);
		Shape outline = stroke.createStrokedShape(getLine());
		
		return outline.intersects(r);
	}
	
	protected Rectangle getBounds()
	{
		BasicStroke stroke = new BasicStroke(10);
		Shape outline = stroke.createStrokedShape(getLine());
		return outline.getBounds();
	}
	
	/**
	 * If the line type is arrow, this method draws the arrowhead
	 */
	private void drawArrowhead(GC buffer) //TODO! clean up this mess.....
	{
		double angle = 25.0;
		double theta = Math.toRadians(180 - angle);
		double[] rot = new double[2];
		double[] p = new double[2];
		double[] q = new double[2];
		double a, b, norm;
		
		rot[0] = Math.cos(theta);
		rot[1] = Math.sin(theta);
		
		buffer.setLineStyle (SWT.LINE_SOLID);
		
		double endx = gdata.getEndX();
		double endy = gdata.getEndY();
		double startx = gdata.getStartX();
		double starty = gdata.getStartY();
		
		a = endx-startx;
		b = endy-starty;
		norm = 8/(Math.sqrt((a*a)+(b*b)));				
		p[0] = ( a*rot[0] + b*rot[1] ) * norm + endx;
		p[1] = (-a*rot[1] + b*rot[0] ) * norm + endy;
		q[0] = ( a*rot[0] - b*rot[1] ) * norm + endx;
		q[1] = ( a*rot[1] + b*rot[0] ) * norm + endy;
		int[] points = {
			(int)endx, (int)endy,
			(int)(p[0]), (int)(p[1]),
			(int)(q[0]), (int)(q[1])
		};
		
		buffer.drawPolygon (points);
		buffer.fillPolygon (points);
	}

	/**
	 * Constructs the line for the coordinates stored in this class
	 */
	public Line2D getLine()
	{
		return new Line2D.Double(gdata.getStartX(), gdata.getStartY(), gdata.getEndX(), gdata.getEndY());
	}
	
	/**
	 * Sets the line start and end to the coordinates specified
	 * <DL><B>Parameters</B>
	 * <DD>Double x1	- new startx 
	 * <DD>Double y1	- new starty
	 * <DD>Double x2	- new endx
	 * <DD>Double y2	- new endy
	 */
	public void setLine(double x1, double y1, double x2, double y2)
	{
		gdata.setStartX(x1);
		gdata.setStartY(y1);
		gdata.setEndX(x2);
		gdata.setEndY(y2);
		
//		setHandleLocation();		
	}
	
	public void setScaleRectangle(Rectangle2D.Double r) {
//		markDirty();
		gdata.setStartX(r.x);
		gdata.setStartY(r.y);
		gdata.setEndY (r.x + r.width);
		gdata.setEndY (r.y + r.height);
		
//		setHandleLocation();
//		markDirty();
	}
	
	protected Rectangle2D.Double getScaleRectangle() {
		return new Rectangle2D.Double(gdata.getStartX(), gdata.getStartY(), gdata.getEndX()
				- gdata.getStartX(), gdata.getEndY() - gdata.getStartY());
	}
	
	/**
	 * Sets this class handles at the correct position 
	 */
	protected void setHandleLocation()
	{
		handleStart.setLocation(gdata.getStartX(), gdata.getStartY());
		handleEnd.setLocation(gdata.getEndX(), gdata.getEndY());
	}
	
	public GmmlHandle[] getHandles()
	{
		return new GmmlHandle[] { handleStart, handleEnd };
	}
	
	protected void adjustToHandle(GmmlHandle h) {
//		markDirty();
		if		(h == handleStart) {
			gdata.setStartX(h.centerx); 
			gdata.setStartY(h.centery);
		}
		else if	(h == handleEnd) {
			gdata.setEndX(h.centerx); 
			gdata.setEndY(h.centery);
		}
//		markDirty();
	}
	
	protected void moveBy(double dx, double dy)
	{
//		markDirty();
		setLine(gdata.getStartX() + dx, gdata.getStartY() + dy, 
				gdata.getEndX() + dx, gdata.getEndY() + dy);
//		markDirty();		
//		setHandleLocation();
	}
	
	protected void adjustToZoom(double factor)
	{
		gdata.setStartX(gdata.getStartX() * factor);
		gdata.setStartY(gdata.getStartY() * factor);
		gdata.setEndX(gdata.getEndX() * factor);
		gdata.setEndY(gdata.getEndY() * factor);
		
//		setHandleLocation();
	}
	
	public void gmmlObjectModified(GmmlEvent e) {		
		markDirty(); // mark everything dirty
		setHandleLocation();
	}

}
