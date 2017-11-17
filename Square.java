

public class Square {
	
	int x, y;
	int mine;
	int count;
	boolean flag, shown;
	
	public Square(int x, int y) {
		this.x = x;
		this.y = y;
		
		this.mine  = 0;
		this.count = 0;
		this.flag  = false;
		this.shown = false;
	}
	
	public void countMines(Square[][] grid) {
		if (this.mine == 1)
			this.count = -1;
		else
			this.count = grid[this.x - 1][this.y - 1].mine
					   + grid[this.x - 1][this.y + 1].mine
					   + grid[this.x + 1][this.y - 1].mine
					   + grid[this.x + 1][this.y + 1].mine
					   + grid[this.x - 1][this.y].mine
					   + grid[this.x + 1][this.y].mine
					   + grid[this.x][this.y - 1].mine
					   + grid[this.x][this.y + 1].mine;
	}
	
}