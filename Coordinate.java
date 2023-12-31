/**
 * Violet Monserate
 * 10/11/2023
 * CSE 123 
 * Section 0 Creative Project: Abstract Strategy Game
 * TA: Hawa
 */

/** 
 * Helper class to represent the position of any counter on the board.
*/ 
public class Coordinate 
{
    private int xCoord;
    private int yCoord;

    /**
     * Creates a new coordinate with the given x and y coordinates.
     *
     * @param x the value we want to set the x coordinate to
     * @param y the value we want to set the y coordinate to
     */
    public Coordinate(int x, int y)
    {
        this.xCoord = x;
        this.yCoord = y;
    }

    /**
     * Sets both the x and y coodinate of this object.
     *
     * @param x the value we want to set the x coordinate to
     * @param y the value we want to set the y coordinate to
     */
    public void setCoordinate(int x, int y)
    {
        setX(x);
        setY(y);
    }

    /**
     * Sets the x coordinate.
     *
     * @param x the value we want to set the x coordinate to
     */
    public void setX(int x) 
    {
        this.xCoord = x;
    }

    /**
     * Sets the y coordinate.
     *
     * @param y the value we want to set the x coordinate to
     */
    public void setY(int y) 
    {
        this.yCoord = y;
    }

    /**
     * Gets the x coordinate (integer).
     *
     * @return the current x coordinate value
     */
    public int getX() 
    {
        return xCoord;
    }

    /**
     * Gets the y coordinate (integer).

     *
     * @return the current y coordinate value
     */
    public int getY() 
    {
        return yCoord;
    }

    /**
     * Checks whether this coordinate is contained within the 1st quadrant, with a top bound.
     *
     * @param topBound The largest number we can use, EXCLUSIVE
     * @return whether the number is within the grid or not
     */
    public boolean isValid(int topBound)
    {
        return xCoord >= 0 && xCoord < topBound && yCoord >= 0 && yCoord < topBound;
    }

    /**
     * Compares two coodinates and sees if they have the same x and y (same point on grid).
     *
     * @param otherPoint the other point we are checking against
     * @return whether it is on top of the other point or not!
     */
    public boolean isEqualTo(Coordinate otherPoint)
    {
        return xCoord == otherPoint.getX() && yCoord == otherPoint.getY(); 
    }
}