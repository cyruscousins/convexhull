package geom;

public class Point {
	public int x, y;
	
	public Point(Point p){
		set(p);
	}
	
	public Point(int x, int y) {
		set(x, y);
	}

	public void set(Point p){
		set(p.x, p.y);
	}
	
	public void set(int x, int y){
		this.x = x;
		this.y = y;
	}

	public boolean equals(Object o){
		if(o instanceof Point){
			Point p = (Point)o;
			return (x == p.x && y == p.y);
		}
		else return false;
	}
	
	public double slopeTo(Point p){
		return (double)(p.y - y) / (double)(p.x - x);
	}
	
	public double angleTo(Point p){
		return Math.atan2(p.y - y, p.x - x);
	}
	
	public double distance(Point p){
		int dx = p.x - x;
		int dy = p.y - y;
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	public double distanceSquared(Point p){
		int dx = p.x - x;
		int dy = p.y - y;
		return dx * dx + dy * dy;
	}
	
	//Is it a left turn from this to p1 to p2?
	public boolean isLeftTurn(Point p1, Point p2){
		return (x - p1.x) * (p1.y - p2.y) - (y - p1.y) * (p1.x - p2.x) > 0;
	}
	
	public String toString(){
		return "(" + x + " " + y + ")";
	}
}
