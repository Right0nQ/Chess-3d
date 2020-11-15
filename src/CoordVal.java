
public class CoordVal {
	public int x;
	public int y;
	public int val;
	
	public CoordVal(int x, int y, int[][] board) {
		this.x = x;
		this.y = y;
		this.val = board[y][x];
	}
}
