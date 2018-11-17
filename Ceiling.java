import java.awt.Polygon;

public class Ceiling
{
	private int[] x,y;

	public Ceiling(int[] x,int[] y)
	{
		this.x=x;
		this.y=y;
	}
	public int[] getX()
	{
		return x;
	}
	public int[] getY()
	{
		return y;
	}

	public Polygon getPoly(){
		return new Polygon(x,y,4);
	}
	//maybe make a method to return a polygon....it will simplify things
}