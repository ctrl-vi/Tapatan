/**
 * Violet Monserate
 * 10/11/2023
 * CSE 123 
 * Section 0 Creative Project: Abstract Strategy Game
 * TA: Hawa
 */

import java.util.*;

/**
 * Represents the board state of Tapatan, and checks for winners, ties, and validity of moves.
 */
public class Tapatan implements AbstractStrategyGame 
{
    /**
     * Represents whose turn it is, as well as the color of each square. Makes my life easier :)
     */
    private enum Color
    {
        BLACK(0), WHITE(1); // Default will be null (empty)
        private final int index; // As specified inside of the Interface
        
        /**
         * Creates the enumeration.
         *
         * @param index the number that will be outputed for the player
         */
        private Color(int index) 
        {
            this.index = index;
        }
  
        /**
         * Returns the index that represents the player to allow for working with the
         * {@link AbstractStrategyGame} interface, as that uses player numbers rather than 
         * colors.
         *
         * @return index associated with player's color
         */
        public int getIndex() 
        {
            return index;
        }
    }
    

    private static final int SIZE = 3; // How wide and tall the grid should be
        
    /** 
     * The current board state will be stored as an enum array, standard x and y for 2d array. "No
     * counter" is represented as a null value. For previous board state checking, it's stored as
     * a 9-character long String, with "W" as white, "B" as black, and "E" as empty, left to right,
     * top to bottom. These act as the keys inside of the map to check for ties.
     * Example:
     * [BLACK], [WHITE], [ NULL]
     * [ NULL], [BLACK], [BLACK] -----> "BWEEBBWWE"
     * [WHITE], [WHITE], [ NULL]        
     */ 
    private Color[][] currentBoardState;
    // the integer value is the number of time the specific boardstate has been encountered.
    private Map<String, Integer> previousBoardStates; 

    // Should increment at start of white turn, also counts how many counters each player put down.
    private int numberOfRounds; 
    private Color currentPlayer;

    /**
     * Contructs a new object that represents the game of tapatan. Assumes starting player is white,
     * the board is clear, and no rounds have been played yet.
     */
    public Tapatan()
    {
        previousBoardStates = new HashMap<>();
        currentBoardState = new Color[3][3];
        numberOfRounds = 0;
        currentPlayer = Color.WHITE;
    }

    /** 
     * Provides the reader a good overview of how this game works, and allows the user to jump
     * right into playing the game. 
     *
     * @returns a string explaining everything, including how to use our implementation of the
     *          game.
     * 
     */
    public String instructions()
    {
        String instructions = "Welcome to Tapatan. Here are the rules: A game starts with a 3 x 3"; 
        instructions += " Grid, with lines going from the center to all of the exterior points, "; 
        instructions += "and lines forming a border around the exterior. Each player starts with "; 
        instructions += "3 counters. Players take turns placing pieces anywhere on the board ";
        instructions += "(basically traditional Tic-Tac-Toe for the first 3 rounds). Once all ";
        instructions += "tiles have been placed, on each players turn, they move any one piece ";
        instructions += " one space along any line, as long as the resulting space is empty. The";
        instructions += " end condition is when there is a 3 counters of the same type in a row"; 
        instructions += " (horizontal, vertical, OR diagonal), and the winner is the owner of ";
        instructions += "those 3 counters. A tie is declared when 3 moves are repeated at least"; 
        instructions += " twice (similar to 3 move repetition in chess). To add a counter, type ";
        instructions += "x and y coordinates on your turn (as directed). To move a piece, enter ";
        instructions += "the x and y coordinates of the counter you want to move, and then the ";
        instructions += "x and y coordinates of the destination of the counter.";

        return instructions;
    }

    /**
     * Returns the index of the player who will take the upcoming turn, but if there is game over
     * (there is a tie or winner), this will return -1.
     *
     * @return int that presents the next player
     */
    public int getNextPlayer()
    {
        return isGameOver() ? -1 : currentPlayer.getIndex();
    }

    /**
     * If it's the first 3 rounds will add a new counter at the given x and y coordinate, otherwise
     * it will ask the user for a coordinate to move from and a coordinate to move to. Any given 
     * coordinate must be within the grid (as shown by coordinate markers), destination for any
     * counter must be empty, the piece at the origin must be the same color as the user, and the 
     * piece moves must be along the lines inside of the Tapatan board. At the end, the board state
     * will reflect the changes made by the current player's move, and will move on to the next 
     * player's turn!
     * 
     * <p>Inadherence to any of these parameters will lead to a couple exceptions being thrown.
     *
     * @param scanner the scanner used to get inputs, and thus coordinates, from the terminal
     * @throws IndexOutOfBoundsException coordinate is not within our gird
     * @throws IllegalStateException we are trying to add too many counters, that's not a legal
     *          state
     * @throws IllegalArgumentException chosen space is filled, you're moving another player's
     *      piece, you're trying to move the piece too far, or you're moving a piece that doesn't
     *      exist.
     */
    public void makeMove(Scanner scanner)
    {
        if (numberOfRounds < 3) 
        {
            System.out.print("Input the x coordinate: ");
            int x = scanner.nextInt();
            System.out.print("Input the y coordinate: ");
            int y = scanner.nextInt();

            addCounter(new Coordinate(x, y));
        }
        else
        {
            System.out.print("Input the origin x coordinate: ");
            int x1 = scanner.nextInt();
            System.out.print("Input the origin y coordinate: ");
            int y1 = scanner.nextInt();
            Coordinate origin = new Coordinate(x1, y1);

            System.out.print("Input the destination x coordinate: ");
            int x2 = scanner.nextInt();
            System.out.print("Input the destination y coordinate: ");
            int y2 = scanner.nextInt();
            Coordinate destination = new Coordinate(x2, y2);

            movePiece(origin, destination);
        }
        switchPlayer();
    }

    /**
     * Returns the index of the winning player (black as 0, white as 1). If the game is not over
     * or there is no winner (tie), a -1. 
     *
     * @return int that represents the winner/lack thereof
     */
    public int getWinner()
    {
        Color winnerColor = getWinnerColor(); 
        return winnerColor == null ? -1 : winnerColor.getIndex(); 
    }

    /**
     * Checks the current board state and sees if we have a clear winner or tie.
     *
     * @return true if we have a winner, false if we are still in play
     */
    public boolean isGameOver()
    {
        return getWinnerColor() != null || isTie();
    }

    /**
     * Will add a counter to the board, as long as we are allowed to do so (Must not have >3 tokens
     * of your color on the board already, square must be empty). Changes board state to reflect
     * this. Also will increment number of rounds if necessary, and switch players. Will also 
     * archive the new board state for stalemate checking.
     * 
     *
     * @param destination where we want to put the piece
     * 
     * @throws IndexOutOfBoundsException coordinate is not within our gird
     * @throws IllegalStateException we are trying to add too many counters, that's not a legal
     *          state
     * @throws IllegalArgumentException the chosen space is already filled with another piece
     */
    private void addCounter(Coordinate destination)
    {
        if (!destination.isValid(SIZE))
        {
            String message = "At least one component of coordinates is Out of Bounds";
            throw new IndexOutOfBoundsException(message);
        }
        else if (numberOfRounds >= 3)
        {
            String message = "Cannot add more counters";
            throw new IllegalStateException(message);
        }
        else if (!isSquareOpen(destination))
        {
            String message = "The destination is already filled";
            throw new IllegalArgumentException(message);
        }
        setPoint(currentPlayer, destination);
        archiveCurrentBoardState();
    }
    
    /**
     * Moves a given counter at an origin to a desired destination. Alters both origin and 
     * destination points inside of 2d array to reflect the current board state. Also will 
     * switch players, and increment number of rounds if necessary. Will also archive the new 
     * board state for stalemate checking.
     *
     * @param origin The place where we are moving our point from, must be within grid and point
     *      has the current player's piece.
     * @param destination The place where we are moving our point to, must be within grid and
     *      point is empty.
     * @throws IndexOutOfBoundsException coordinate(s) is/are not within our grid
     * @throws IllegalArgumentException chosen space is filled, you're moving another player's
     *      piece, you're not moving along the lines, or you're moving a piece that doesn't
     *      exist.
     */
    private void movePiece(Coordinate origin, Coordinate destination)
    {
        if (!origin.isValid(SIZE) || !destination.isValid(SIZE))
        {
            String message = "At least one component of coordinates is Out of Bounds";
            throw new IndexOutOfBoundsException(message);
        }
        if (!isSquareOpen(destination))
        {
            String message = "The destination space is already filled";
            throw new IllegalArgumentException(message);
        }
        if (colorAt(origin) == null)
        {
            String message = "You need to select a piece already on the board";
            throw new IllegalArgumentException(message);
        }
        if (colorAt(origin) == oppositeColor(currentPlayer))
        {
            String message = "You're moving someone else's piece! That's rude...";
            throw new IllegalArgumentException(message);
        }
        if (!isValidMove(origin, destination))
        {
            String message = "Please move along the lines on the board!";
            throw new IllegalArgumentException(message);
        }
        setPoint(null, origin);
        setPoint(currentPlayer, destination);
        archiveCurrentBoardState();
    }

    /** 
     * Extracts the more complicated part of moving a piece: whether it's going over a line...
     * Assumes that you'll be within the grid 
     *
     * @returns boolean that tells us whether the move is valid or not 
     */
    private boolean isValidMove(Coordinate origin, Coordinate destination)
    {
        double distance = Math.pow(
                Math.pow(origin.getX() - destination.getX(), 2)
                + Math.pow(origin.getY() - destination.getY(), 2), 
                0.5
        );
        boolean isMovingTooFar = distance > 1.5;
        //centers and corners are able to move freely (within the 1 up/down, 1 left/right range)
        boolean isCenterOrCorner = 
                origin.isEqualTo(new Coordinate(1, 1)) 
                || origin.isEqualTo(new Coordinate(0, 0))
                || origin.isEqualTo(new Coordinate(2, 0))
                || origin.isEqualTo(new Coordinate(0, 2))
                || origin.isEqualTo(new Coordinate(2, 2));
        
        boolean isMovingFromSideToAnotherSide = 
                (
                    origin.isEqualTo(new Coordinate(1, 0))
                    || origin.isEqualTo(new Coordinate(0, 1))
                    || origin.isEqualTo(new Coordinate(2, 1))
                    || origin.isEqualTo(new Coordinate(1, 2))
                )
                &&
                (
                    destination.isEqualTo(new Coordinate(1, 0))
                    || destination.isEqualTo(new Coordinate(0, 1))
                    || destination.isEqualTo(new Coordinate(2, 1))
                    || destination.isEqualTo(new Coordinate(1, 2))
                );

        return !isMovingTooFar && (isCenterOrCorner || !isCenterOrCorner);
    }

    /**
     * Checks to see if the current boardstate contains a winner, as outline in instructions
     *
     * @return color of winner, null if undecided OR tie
     * @throws invalidArguementException if the square is invalid or already occupied
     */
    private Color getWinnerColor()
    {
        if (
                (
                    colorAt(0, 0) != null 
                    && colorAt(0, 0) == colorAt(1, 1)
                    && colorAt(1, 1) == colorAt(2, 2)
                )
                ||
                (
                    colorAt(2, 0) != null
                    && colorAt(2, 0) == colorAt(1, 1)
                    && colorAt(1, 1) == colorAt(0, 2)
                )
        )
        {
            return colorAt(1, 1); // Diagonal Win
        }

        for (int i = 0; i < SIZE; i++)
        {
            if (
                    colorAt(0, i) != null 
                    && colorAt(0, i) == colorAt(1, i)
                    && colorAt(1, i) == colorAt(2, i)
            )
            {
                return colorAt(0, i); // Horizontal Win
            }
            else if (
                    colorAt(i, 0) != null 
                    && colorAt(i, 0) == colorAt(i, 1) 
                    && colorAt(i, 1) == colorAt(i, 2)
            )
            {
                return colorAt(i, 0); // Vertical Win
            }
        }
        
        return null;
    }

    /**
     * Checks if a board state appears in the map more than 2 times, which should yield a 
     * tie according to the rules of Tapatan.
     *
     * @return true if we have a winner, false if we do not
     */
    private boolean isTie()
    {
        boolean result = false;
        for (int count : previousBoardStates.values())
        {
            result = result ? result : count >= 3;
        }
        return result;

    }

    /**
     * Returns whether the square already contains a counter (if null, then it's open, otherwise
     * it's taken up by a counter of some sort) (use colorAt() method to find that color).
     *
     * @param coordinate the square we want to check
     * @return whether the square is open or not as a true/false
     * @throws IndexOutOfBoundsException coordinate is not within our grid
     */
    private boolean isSquareOpen(Coordinate coordinate)
    {
        if (!coordinate.isValid(SIZE))
        {
            String message = "At least one of the components of coordinate is Out Of Bounds";
            throw new IndexOutOfBoundsException(message);
        }
        return colorAt(coordinate) == null;
    }

    /**
     * Returns the {@link Color} of the square at the given coordinate.
     *
     * @param x the x coordinate, from 0 to 2 (inclusive), otherwise throws exception 
     * @param y the y coordinate, from 0 to 2 (inclusive), otherwise throws exception
     * @return the color of the square you are looking for
     * 
     * @throws IndexOutOfBoundsException the square does not exist on the grid
     */
    private Color colorAt(int x, int y)
    {
        return colorAt(new Coordinate(x, y));
    }

    /**
     * Returns the {@link Color} of the square at the given coordinate.
     *
     * @param coordinate the square we are checking. x and y coordinates must be from 0 to 2 
     *      (inclusive), otherwise throws exception 
     * @return the color of the square you are looking for
     * 
     * @throws IndexOutOfBoundsException the square does not exist on the grid
     */
    private Color colorAt(Coordinate coordinate)
    {
        if (!coordinate.isValid(SIZE))
        {
            String message = "At least one of the components of coordinate is Out Of Bounds";
            throw new IndexOutOfBoundsException(message);
        }
        return currentBoardState[coordinate.getY()][coordinate.getX()];
    }

    /**
     * Inverts the given color, useful for finding opponent {@link Color}, or next player.
     *
     * @param color given Color
     * @return the opposite Color (eg. BLACK -> WHITE)
     */
    private Color oppositeColor(Color color)
    {
        return color == Color.WHITE ? Color.BLACK : Color.WHITE;
    }


    /**
     * Increments the times the current board state has happened. otherwise will add the new 
     * Key and Value pair to the map
     */
    private int archiveCurrentBoardState()
    {
        String currentBoardAsString = currentBoardStateToString();
        int count;
        if (previousBoardStates.containsKey(currentBoardAsString))
        {
            count = previousBoardStates.get(currentBoardAsString) + 1;
        }
        else
        {
            count = 1;
        }
        previousBoardStates.put(currentBoardAsString, count);
        return count;
    }

    /**
     * Helper class that allows us to create a concise, easily comparable key for use with the
     * previousBoardStates map. 
     * Example: 
     * [BLACK], [WHITE], [ NULL]
     * [ NULL], [BLACK], [BLACK] -----> "BWEEBBWWE"
     * [WHITE], [WHITE], [ NULL]        
     *
     * @return The current board state as a {@link String}, Left-To-Right, Top-To-Bottom, with
     *      "W" representing White, "B" representing Black, and "E" representing empty/null
     */ 
    private String currentBoardStateToString()
    {
        String output = "";
        for (Color[] row : currentBoardState) 
        {
            for (Color square : row) 
            {
                if (square == Color.WHITE)
                {
                    output += "W";
                }
                else if (square == Color.BLACK)
                {
                    output += "B";
                } 
                else
                {
                    output += "E";
                }
            }
        }
        return output;
    }

    /**
     * Changes who the current player is, and also increments the number of rounds, if 
     * appropriate (the next player is white, which is the person who starts the game).
     *  
     */
    private void switchPlayer()
    {
        currentPlayer = oppositeColor(currentPlayer);
        if (currentPlayer == Color.WHITE)
        {
            numberOfRounds++;
        }
        
    }
    
    /**
     * Sets the {@link Color} at the given coordinate, which must be within the grid.
     *
     * @param color the desired {@link Color} that we want, or null if square will be empty.
     * @param coordinate the place we want to put/remove the counter. Must be within grid.
     * 
     * @throws IndexOutOfBoundsException at least 1 part of the coordinate is Out Of Bounds
     */
    private void setPoint(Color color, Coordinate coordinate)
    {
        if (!coordinate.isValid(SIZE))
        {
            String message = "At least one of the components of coordinate is Out Of Bounds";
            throw new IndexOutOfBoundsException(message);
        }
        currentBoardState[coordinate.getY()][coordinate.getX()] = color;
    }

    /**
     * returns the board state in a way that can simply be printed into the terminal, yet looks 
     * awesome and cool. I SPENT WAY TOO LONG ON THIS TEMPLATE but it looks very pretty! 
     *
     * <p>Example:
     * <pre>
     *┌─────┐   ┌─────┐   ┌─────┐
     *│     │───│     │───│     │
     *└─────┘   └─────┘   └─────┘
     *   │  ╲      │      ╱  │
     *   │   ╲     │     ╱   │
     *   │    ╲    │    ╱    │
     *┌─────┐  ╲┌─────┐╱  ┌─────┐
     *│BLACK│───│WHITE│───│     │
     *└─────┘  ╱└─────┘╲  └─────┘
     *   │    ╱    │    ╲    │
     *   │   ╱     │     ╲   │
     *   │  ╱      │      ╲  │
     *┌─────┐   ┌─────┐   ┌─────┐
     *│     │───│     │───│WHITE│
     *└─────┘   └─────┘   └─────┘
     * </pre>
     *
     * @returns a human readable (but not machine readable) String that can be rendered in terminal
     */
    public String toString()
    {
        String[] values = new String[9]; // Stores all text values, left to right, top to bottom
        for (int i = 0; i < SIZE; i++)
        {
            for (int j = 0; j < SIZE; j++)
            {
                if (currentBoardState[i][j] != null)
                {    
                    values[i * SIZE + j] = currentBoardState[i][j].toString();
                }
                else
                {
                    values[i * SIZE + j] = "     ";
                }
            }
        }   
        String output = "";
        output += ("       ┌─┐       ┌─┐       ┌─┐       \n");
        output += ("       │0│       │1│       │2│       \n");
        output += ("       └─┘       └─┘       └─┘       \n");
        output += (" ┌─┐ ┌─────┐   ┌─────┐   ┌─────┐     \n");
        output += (String.format(" │0│ │%1$s│───│%2$s│───│%3$s│     \n", 
                            values[0], 
                            values[1], 
                            values[2]));
        output += (" └─┘ └─────┘   └─────┘   └─────┘     \n");
        output += ("        │  ╲      │      ╱  │        \n");
        output += ("        │   ╲     │     ╱   │        \n");
        output += ("        │    ╲    │    ╱    │        \n");
        output += (" ┌─┐ ┌─────┐  ╲┌─────┐╱  ┌─────┐     \n");
        output += (String.format(" │1│ │%1$s│───│%2$s│───│%3$s│     \n", 
                            values[3], 
                            values[4], 
                            values[5]));
        output += (" └─┘ └─────┘  ╱└─────┘╲  └─────┘     \n");
        output += ("        │    ╱    │    ╲    │        \n");
        output += ("        │   ╱     │     ╲   │        \n");
        output += ("        │  ╱      │      ╲  │        \n");
        output += (" ┌─┐ ┌─────┐   ┌─────┐   ┌─────┐     \n");
        output += (String.format(" │2│ │%1$s│───│%2$s│───│%3$s│     \n", 
                            values[6], 
                            values[7], 
                            values[8]));
        output += (" └─┘ └─────┘   └─────┘   └─────┘     \n");
        return output;
    }



}