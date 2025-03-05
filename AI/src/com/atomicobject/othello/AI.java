package com.atomicobject.othello;

import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * Implementation of the Mini-Max Alpha-Beta Pruning Algorithm for the board
 * game Othello.
 *
 * @author Noah Clouser
 */
public class AI {
    /** Maximum depth of the Mini-Max search tree */
    private static final int     DEPTH      = 3;
    /** Maximum index of the columns in the game board */
    private final static int     MAX_COL    = 7;
    /** Maximum index of the rows in the game board */
    private final static int     MAX_ROW    = 7;
    /** Minimum index of the columns in the game board */
    private final static int     MIN_COL    = 0;
    /** Minimum index of the rows in the game board */
    private final static int     MIN_ROW    = 0;
    /**
     * 2D array for traversing the game board in 1 of 8 possible directions.
     * Index 0 of a row pertains to the X value and index 1 pertains to the Y
     * value
     */
    private final static int[][] directions = { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 }, { 1, 1 }, { -1, -1 },
            { 1, -1 }, { -1, 1 }, };

    /**
     * Default constructor
     */
    public AI () {
    }

    /**
     * Determine the next best move the AI agent should execute. Utilizing the
     * Mini-Max Alpha-Beta Pruning algorithm looks at each possible valid move
     * and searches the next DEPTH - 1 moves and evaluates the state of the
     * board determined by some heuristic.
     *
     * @param state
     *            Current state of the game board. Must be a GameState object
     * @return Returns an int array of [x, y] of the next best move.
     */
    public int[] computeMove ( final GameState state ) {

        // Determine whether the agent and the opponent is 1 or 2 on the game
        // board
        final int player = state.getPlayer();
        final int opponent = ( player == 1 ) ? 2 : 1;

        // Find all possible valid moves of the game board before any potential
        // moves have been placed
        final ArrayList<MoveNode> valid_moves = getValidMoves( state.getBoard(), player, opponent );
        if ( valid_moves.size() == 0 ) {
            return new int[] { 0, 0 };
        }

        int score = 0;
        // At this stage the agent is looking at the root of each possible valid
        // move
        final int depth_counter = 0;
        // Keeps track of valid_moves and their score to determine which move
        // will be picked
        final PriorityQueue<MoveNode> moves = new PriorityQueue<MoveNode>();

        for ( final MoveNode possible_move : valid_moves ) {
            score = alpha_beta_mini_max( depth_counter + 1, cloneBoard( state.getBoard() ), possible_move.position,
                    Integer.MIN_VALUE, Integer.MAX_VALUE, opponent );
            moves.add( new MoveNode( possible_move.getPosition(), score ) );
        }

        // Return move with the highest score
        return moves.poll().getPosition();
    }

    /**
     * Recursive function to search through each possible move while pruning
     * branches where beta <= alpha.
     *
     * @param depth_counter
     *            Tracker of the depth of the current recursive branch. Must be
     *            <= DEPTH
     * @param board
     *            2D int array of the game board before the next move is placed
     * @param move
     *            Validated [x, y] move to be placed on the game board
     * @param alpha
     *            The best value that the maximizer currently can guarantee at
     *            that level or above.
     * @param beta
     *            The best value that the minimizer currently can guarantee at
     *            that level or above.
     * @return
     */
    private int alpha_beta_mini_max ( final int depth_counter, final int[][] board, final int[] move, int alpha,
            int beta, final int player ) {

        final int opponent = ( player == 1 ) ? 2 : 1;

        // Find what tiles should be flipped
        final ArrayList<MoveNode> change_tiles = perceive( move, board, player, opponent );
        // Flip those tiles in a new board
        final int[][] new_board = flipTiles( change_tiles, board, player );
        // Find valid moves from new board
        final ArrayList<MoveNode> children = getValidMoves( new_board, player, opponent );
        // Limit? Leaf node?
        if ( depth_counter >= DEPTH || children.size() == 0 ) {
            return score( board, player );
        }

        // Initialize best_score;
        int best_score;

        // Maximize the score, this is the AI player
        if ( depth_counter % 2 == 0 ) {
            // best_score refers to the largest heuristic score found among this
            // branch's children
            best_score = Integer.MIN_VALUE;
            int score;

            for ( final MoveNode child : children ) {
                // Swap player to be the opponent of the current player because
                // we are incrementing the depth which changes maximizing to
                // minimizing in the recursive child call
                score = alpha_beta_mini_max( depth_counter + 1, new_board, child.getPosition(), alpha, beta, opponent );
                best_score = Math.max( best_score, score );
                alpha = Math.max( alpha, best_score );
                if ( alpha >= beta ) {
                    return alpha;
                }
            }
            return best_score;
        }
        // Minimize the score, this is the opponent to the AI player
        else {
            // best_score refers to the smallest heuristic score found among
            // this branch's children
            best_score = Integer.MAX_VALUE;
            int score;

            for ( final MoveNode child : children ) {
                // Swap player to be the opponent because we are incrementing
                // the depth which changes minimizing to maximizing in the
                // recursive child call
                score = alpha_beta_mini_max( depth_counter + 1, new_board, child.getPosition(), alpha, beta, opponent );
                best_score = Math.min( best_score, score );
                beta = Math.min( beta, best_score );
                if ( beta <= alpha ) {
                    return beta;
                }
            }
            return best_score;
        }
    }

    /**
     * Helper function to traverse all direction from the location of move to
     * determine what tiles will be flipped from opponent to player.
     *
     * @param move
     *            Starting position of the method to traverse all directions
     *            from. Move must be validated move in the game rules for
     *            Othello
     * @param board
     *            Current state of game board
     * @param player
     *            int value of the current player
     * @param opponent
     *            int value of the other player on the game board
     * @return Return an ArrayList of MoveNodes of all tiles that need to be
     *         flipped from opponent to player
     */
    private ArrayList<MoveNode> perceive ( final int[] move, final int[][] board, final int player,
            final int opponent ) {
        final ArrayList<MoveNode> tiles = new ArrayList<MoveNode>();
        int newRow, newCol;
        // move tile needs to be flipped by default
        tiles.add( new MoveNode( move ) );
        // Iterate through each possible direction
        for ( final int[] direction : directions ) {
            newRow = move[0] + direction[0];
            newCol = move[1] + direction[1];
            // Loop while the tile is an opponent tile
            while ( isValid( newRow, newCol ) && board[newRow][newCol] == opponent ) {
                newRow += direction[0];
                newCol += direction[1];
                // Stop looping once the next tile is a player tile
                if ( isValid( newRow, newCol ) && board[newRow][newCol] == player ) {
                    newRow -= direction[0];
                    newCol -= direction[1];
                    // Loop while the tile is not original move tile
                    while ( ! ( newRow == move[0] && newCol == move[1] ) ) {
                        // Add the opponent tile to the ArrayList
                        tiles.add( new MoveNode( new int[] { newRow, newCol } ) );
                        newRow -= direction[0];
                        newCol -= direction[1];
                    }
                    break;
                }
            }
        }
        return tiles;
    }

    /**
     * Creates a new int[][] array that is a copy of the game board passed in
     * but with all of the tiles flipped from opponent->player from change_tiles
     * ArrayList
     *
     * @param change_tiles
     *            ArrayList of all tiles needed to be flipped
     * @param board
     *            Game board before tiles are flipped
     * @param player
     *            int value of the player that the tiles will be flipped to.
     *            Either 1 or 2.
     * @return Return the int[][] array with tiles flipped
     */
    private int[][] flipTiles ( final ArrayList<MoveNode> change_tiles, final int[][] board, final int player ) {
        // Clone board into a new int[][] array
        final int[][] new_board = new int[board.length][board[0].length];
        for ( int row = 0; row < board.length; row++ ) {
            for ( int col = 0; col < board[row].length; col++ ) {
                new_board[row][col] = board[row][col];
            }
        }
        // Flip each tile to be the player's value
        for ( final MoveNode tile : change_tiles ) {
            new_board[tile.getRow()][tile.getCol()] = player;
        }
        return new_board;
    }

    /**
     * Creates an ArrayList of MoveNodes of all valid potential tiles the player
     * can make given the int[][] array of the game board.
     *
     * @param board
     *            int[][] array of the current state of the game board
     * @param player
     *            int value of the current player
     * @param opponent
     *            int value of the other player on the game board
     * @return Returns an ArrayList of MoveNodes
     */
    private ArrayList<MoveNode> getValidMoves ( final int[][] board, final int player, final int opponent ) {
        final ArrayList<MoveNode> valid_moves = new ArrayList<MoveNode>();

        // Iterate through each tile in the board
        for ( int row = 0; row < board.length; row++ ) {
            for ( int col = 0; col < board[row].length; col++ ) {
                // Explore the tile if it is the player's tile
                if ( board[row][col] == player ) {
                    int next_row;
                    int next_col;

                    for ( int i = 0; i < board.length; i++ ) {
                        boolean in_range = true;
                        int[] direction;
                        /*
                         * Determines what direction to check and if the row and
                         * col is in a VALID POSITION. Example: Checking the
                         * tile in the right direction will increase the y
                         * value. The starting y value must have at least 2 more
                         * tiles on its right for an opponent tile and another
                         * player tile to be valid. Therefore, if the starting y
                         * value is greater than MAX_COL - 2 then there is not
                         * enough tiles to be a potential valid move.
                         */
                        switch ( i ) {
                            case 0:
                                direction = new int[] { 0, 1 };
                                if ( col > MAX_COL - 2 ) {
                                    in_range = false;
                                }
                                break;
                            case 1:
                                direction = new int[] { 0, -1 };
                                if ( col < MIN_COL + 2 ) {
                                    in_range = false;
                                }
                                break;
                            case 2:
                                direction = new int[] { 1, 0 };
                                if ( row > MAX_ROW - 2 ) {
                                    in_range = false;
                                }
                                break;
                            case 3:
                                direction = new int[] { -1, 0 };
                                if ( row < MIN_ROW + 2 ) {
                                    in_range = false;
                                }
                                break;
                            case 4:
                                direction = new int[] { 1, 1 };
                                if ( row > MAX_ROW - 2 || col > MAX_COL - 2 ) {
                                    in_range = false;
                                }
                                break;
                            case 5:
                                direction = new int[] { -1, -1 };
                                if ( row < MIN_ROW + 2 || col < MIN_COL + 2 ) {
                                    in_range = false;
                                }
                                break;
                            case 6:
                                direction = new int[] { 1, -1 };
                                if ( row > MAX_ROW - 2 || col < MIN_COL + 2 ) {
                                    in_range = false;
                                }
                                break;
                            case 7:
                                direction = new int[] { -1, 1 };
                                if ( row < MIN_ROW + 2 || col > MAX_COL - 2 ) {
                                    in_range = false;
                                }
                                break;
                            default:
                                direction = new int[] { 0, 1 };
                                if ( col > MAX_COL - 2 ) {
                                    in_range = false;
                                }
                                break;
                        }
                        // Traverse in the direction
                        if ( in_range ) {
                            next_row = row + direction[0];
                            next_col = col + direction[1];
                            // Loop while the tile is valid and has the
                            // opponent's value
                            while ( isValid( next_row, next_col ) && board[next_row][next_col] == opponent ) {
                                next_row += direction[0];
                                next_col += direction[1];
                                // Stop iterating if the next tile is valid and
                                // blank. This signifies a valid move the player
                                // could place their tile on
                                if ( isValid( next_row, next_col ) && board[next_row][next_col] == 0 ) {
                                    valid_moves.add( new MoveNode( new int[] { next_row, next_col } ) );
                                }
                            }

                        }
                    }
                }
            }
        }
        return valid_moves;
    }

    /**
     * Calculate the current score given a heuristic and an int[][] array of the
     * state of the game board. The current heuristic assigns a flat/static int
     * value to each grid coordinate. Corner squares are weighted the most, the
     * square diagonal is weighted the least, and other squares are weighted
     * according to their positional value. These values were gathered from Brad
     * Prangnell's, Parker Williams's, and Rob Ardies's report, "An AI for the
     * game Othello"
     *
     * This is a simple heuristic that could be optimized and improved with a
     * dynamic factor
     *
     * @param board
     *            Current state of the game board to be evaluated
     * @param player
     *            int value to determine whether the player is 1 or 2 on the
     *            game board
     * @return Return the int score of subtracting the player's score from the
     *         opponent's score. When maximizing, higher positive numbers are
     *         ideal whereas lower negative numbers are ideal for minimizing a
     *         player's moves/
     */
    private int score ( final int[][] board, final int player ) {
        final int[][] hueristic = new int[][] { { 50, -3, 7, 2, 2, 7, -3, 50 }, { -3, -12, 1, 1, 1, 1, -12, -3 },
                { 7, 1, 1, 1, 1, 1, 1, 7 }, { 2, 1, 1, 1, 1, 1, 1, 2 }, { 2, 1, 1, 1, 1, 1, 1, 2 },
                { 7, 1, 1, 1, 1, 1, 1, 7 }, { -3, -12, 1, 1, 1, 1, -12, -3 }, { 50, -3, 7, 2, 2, 7, -3, 50 } };

        int player_score = 0;
        int opponent_score = 0;

        for ( int row = 0; row < board.length; row++ ) {
            for ( int col = 0; col < board[row].length; col++ ) {
                final int tile = board[row][col];
                if ( tile == player ) {
                    player_score += hueristic[row][col];
                }
                else if ( tile != 0 ) {
                    opponent_score += hueristic[row][col];
                }
            }
        }
        return player_score - opponent_score;
    }

    /**
     * Checks whether the row and col are within the boundaries of game board
     *
     * @param row
     *            int value of the x value to check
     * @param col
     *            int value of the y value to check
     * @return Return true if the row and col are within the boundaries,
     *         otherwise false
     */
    private boolean isValid ( final int row, final int col ) {
        return row >= MIN_ROW && row <= MAX_ROW && col >= MIN_COL && col <= MAX_COL;
    }

    /**
     * Create a new copy of the board
     *
     * @param board
     *            Current state of the game board
     * @return Returns a new int[][] copy of board
     */
    private int[][] cloneBoard ( final int[][] board ) {
        final int[][] clone = new int[board.length][board[0].length];
        for ( int row = 0; row < board.length; row++ ) {
            for ( int col = 0; col < board[row].length; col++ ) {
                // Update the square to either blank, red, or yellow
                clone[row][col] = board[row][col];
            }
        }
        return clone;
    }

    /**
     * Node class to hold the [x,y] position and its priority.
     */
    private class MoveNode implements Comparable<MoveNode> {

        /** [x,y] position of the node */
        private final int[] position;
        /** Score of the node used for priority queues */
        private final int   priority;

        /**
         * Constructor for creating entries in a PriorityQueue
         *
         * @param position
         *            [x,y] position of the node
         * @param priority
         *            Score of the node
         */
        public MoveNode ( final int[] position, final int priority ) {
            this.position = position;
            this.priority = priority;
        }

        /**
         * Constructor for creating entries in an ArrayList
         *
         * @param position
         *            [x,y] position of the node
         */
        public MoveNode ( final int[] position ) {
            this.position = position;
            this.priority = 0;
        }

        public int[] getPosition () {
            return this.position;
        }

        public int getRow () {
            return this.position[0];
        }

        public int getCol () {
            return this.position[1];
        }

        @Override
        public int compareTo ( final MoveNode other ) {
            return other.priority - this.priority;
        }

    }
}
