import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import Utility.Point;

public class JQK {

	private static boolean victory = false;

	private static Scanner scan = new Scanner(System.in);

	// new plan: generate all possible states, store in array or something
	private static HashMap<State, State> states = new HashMap<>();
	private static HashMap<State, List<State>> rules = new HashMap<>();
	private static final State victoryState = new State();

	/**
	 * New plan. Create an array of all possible states. Sort these states according
	 * to their visible set into objects called "rules" (I could also call it a
	 * "facade") Each rule object has a "move" instance variable which says what
	 * card should be moved and where. In other words, a state looks at its visible
	 * cards, which determines its rule, which determines its move.
	 * 
	 * I realized that the number of states per rule is actually pretty
	 * straight-forward. Rules with three visible cards have one state in them,
	 * Rules with two visible cards and three visible cards have 2 states in them.
	 * is that important?
	 * 
	 * The goal is to set the moves for each rule so that every state flows to the
	 * end-state. I want to start at the end state, and hook up adjacent states
	 * 
	 */

	public static Random randy = new Random();

	/**
	 * generate algorithm without regard to visibility: start with win state.
	 * recursively add all neighbors.
	 * 
	 * 
	 */

	private static final Point[] SWAPS = { new Point(0, 1), new Point(0, 2), new Point(1, 0), new Point(1, 2),
			new Point(2, 0), new Point(2, 1)

	};

	private static final Point[] REVERSESWAPS = { new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(2, 1),
			new Point(0, 2), new Point(1, 2)

	};

	public static void permute() {

		State start = new State();
		start.add(Card.JACK, 0);
		start.add(Card.QUEEN, 0);
		start.add(Card.KING, 0);

		permuteHelper(start, states, 0);
	}

	private static int stateCount = 0;

	/**
	 * Generates a lot of lists right now. Should find some way not to generate a
	 * new list every time just to use the table?
	 * 
	 * Considering changing data structers. Would be nice to know if two states are on the same branch
	 * 
	 * @param state
	 * @param states2
	 */
	private static void permuteHelper(State state, HashMap<State, State> states2, int depth) {
		// no base case. Just keep going until all neighbors are visited.
		for (int i = 0; i < SWAPS.length; i++) {
			Point nextMove = SWAPS[i];
			// There isn't always a card to move, so check
			Card toMove = state.top(nextMove.getX());
			if (toMove != null) {
				// create a deep copy of the current state
				State nextState = new State(state);
				// make one move
				nextState.move(toMove, nextMove.getY());
				// check if i've been here already
				if (states2.containsValue(nextState)) {
					continue;
				} else {
					nextState.setNumber(stateCount++);
					nextState.setDepth(depth);
					// tell the new state to remember where it came from
					nextState.setMoveCommand(REVERSESWAPS[i]);
					// make sure I never visit this state again
					// if(states2.containsKey(nextState)) {
					// System.out.println("permute encounters duplicates");
					// }
					states2.put(nextState, nextState);
					// check all the new states neighbors.
					permuteHelper(nextState, states2, depth++);
				}

			}

		}

	}

	public static void main(String[] args) {
		System.out.println("Start");

		victoryState.add(Card.JACK, 0);
		victoryState.add(Card.QUEEN, 0);
		victoryState.add(Card.KING, 0);

		permute();
		makeRules();
		printRules();

		// testAllStates();

		/**
		 * Now for an experiment, where I force every state in the same rule to share
		 * the same move commands.
		 * 
		 * Actually, let's just start wth one.
		 */

		for (int i = 0; i < 2; i++) {
			correctAllMoves();
		}

		// printRules();
		testAllStates();

	}

	
	/**
	 * turns out its not enough to just try and make moves that lead to a lower depth.
	 * 
	 */
	
	private static void correctAllMoves() {
		int correctCount = 0;
		int failCount = 0;
		for (List<State> l : rules.values()) {
			if (l.size() > 1) {
				if (!l.get(0).getMoveCommand().equals(l.get(1).getMoveCommand())) {
					// l.get(1).setMoveCommand(new Point(l.get(0).getMoveCommand()));
					if (moveCorrecter(l)) {
						System.out.println("			Set " + l + " to same moves: " + l.get(0).getMoveCommand());
						correctCount++;
						// break;
					} else {
						System.out.println("			Move correcter couldn't do anything");
						System.out.println();
						failCount++;
						continue;
					}

				}

			}
		}

		System.out.println("Corrected " + correctCount + " failed " + failCount);

	}

	/**
	 * try to change a rule so it maps all states to a lower depth
	 * 
	 * @param l
	 */
	private static boolean moveCorrecter(List<State> l) {

		for (int i = 0; i < SWAPS.length; i++) {
			int successCount = 0;

			/**
			 * This next part is badly done. The code gets all cluttered because I have to
			 * act on two states per rule. I'm going to try to change the depths of both at
			 * the end, so I'm going to use a point to remember the new depths.
			 */
			Point depths = new Point(0, 0);

			// act on both states
			for (int j = 0; j < l.size(); j++) {
				State temp = new State(l.get(j));
				temp.setMoveCommand(SWAPS[i]);
				System.out.println(temp);
				// System.out
				// .println("Temp is: " + temp + " move is: " + SWAPS[i] + ", success is: " +
				// successCount);

				if (temp.move()) {
					State destination = states.get(temp);
					if (temp.getDepth() > destination.getDepth()) {
						successCount++;
						// this part is for remembering the new depths
						if (successCount == 1) {
							depths.setX(destination.getDepth());
						} else {
							depths.setY(destination.getDepth());
						}
						// System.out.println(temp + " at depth " + temp.getDepth() + " can move,
						// SUCCESS");
					} else {
						// System.out.println(temp + " at depth " + temp.getDepth() + " can move, LOWER
						// DEPTH");
					}
				} else {
					// System.out.println(" " + temp + " at depth " + temp.getDepth() + " can't
					// move");
				}
			}

			// if all states map to a lower depth for this command, adopt this command, and
			// stop looking
			if (successCount == l.size()) {
				for (int j = 0; j < l.size(); j++) {
					l.get(j).setMoveCommand(SWAPS[i]);
				}
				// mix of loops and magic numbers here (assuming two elements). Very bad. Oh
				// well
				System.out.println();
				System.out.println("Set " + l.get(0).getDepth() + " to depth " + depths.getX());
				l.get(0).setDepth(depths.getX());
				System.out.println("Set " + l.get(1).getDepth() + " to depth " + depths.getY());
				l.get(1).setDepth(depths.getY());

				System.out.println("Move corrector finished");
				return true;
			}

		}
		return false;

	}

	private static void printRules() {
		System.out.println("printing rules");
		int count = 0;
		for (List<State> l : rules.values()) {
			System.out.println("Rule: " + count);
			count++;
			for (State s : l) {
				System.out.println("	" + s + " has depth " + s.getDepth() + ", number " + s.getNumber() + ".	"
						+ s.getMoveCommand());
			}
		}
	}

	private static void makeRules() {
		/**
		 * here I have a hash map which uses the visible cards from a state as keys. The
		 * idea is to get every state with the same visible cards in the same bucket.
		 * 
		 */
		for (State s : states.values()) {
			if (rules.containsKey(s.facade())) {
				// System.out.println(s + " had he same key");
				rules.get(s.facade()).add(s);
			} else {
				ArrayList<State> nextRule = new ArrayList<>();
				nextRule.add(s);
				rules.put(s.facade(), nextRule);
			}
		}

		// System.out.println(rules + "\n" + rules.size());

	}

	private static void testStateMethods() {
		State testState = new State();
		State otherState = new State();

		testState.add(Card.JACK, 1);
		otherState.add(Card.JACK, 1);

		System.out.println(testState);
		System.out.println(otherState);

		System.out.println("is testState valid? " + testState.isValid());
		System.out.println("are the states equal? " + otherState.equals(testState));

	}

	private static void testPermute() {

		int permuteSuccessCount = 0;
		// System.out.println(states);
		int randomTestStrictness = 100;
		for (int i = 0; i < randomTestStrictness; i++) {
			State testPermute = new State();
			testPermute.randomize();
			// testPermute.add(Card.JACK, 1);
			// System.out.println(testPermute + " should equal " + states.get(testPermute));
			// System.out.println("test permue: " + states.get(testPermute));
			if (states.containsValue(testPermute)) {
				permuteSuccessCount++;
			}
		}
		System.out.println("A random state was found in the states list " + permuteSuccessCount + " out of "
				+ randomTestStrictness + " times");
		System.out.println("States contains: " + states.size() + " elements");

	}

	// TODO: maybe change this so it can detect a failure instead of not halting.
	private static void testAllStates() {
		State[] allStates = states.values().toArray(new State[0]);
		ArrayList<Integer> steps = new ArrayList<>();

		for (int i = 0; i < allStates.length; i++) {
			// System.out.println("all states has: " + allStates[i]);
			State testState = allStates[i];
			testState = new State(states.get(testState));

			int count = 0;
			while (!testState.equals(victoryState)) {

				testState.move();
				// System.out.println(testState);
				testState = new State(states.get(testState));
				count++;
			}
			steps.add(count);
		}
		Collections.sort(steps);
		System.out.println("All states lead to the end state");
		System.out.println("steps taken: " + steps);
	}

	private static void manageInput() {

		String input = scan.nextLine();

		if (input.equals("q")) {
			System.exit(0);
		}

		// if (input.equals("r")) {
		// randomize();
		// }

	}

	/**
	 * 
	 * Thoughts:
	 * 
	 * My initial thoughts were: "First I will get all the cards laid, out, so they
	 * are easy to work with." "then I'll get them in order, and then put them back
	 * together."
	 * 
	 * This idea seemed good because it would allow the program to have 3 modes. If
	 * the program detects three face up cards, it can easily switch them into any
	 * order. That mode is easy.
	 * 
	 * The problem is getting to and from the state with three face up cards. When
	 * there are 2 piles, there's no way to know which pile has two cards. So I'll
	 * have to devise a strategy that turns two piles into three, no matter where
	 * the third card is.
	 * 
	 * The same problem rears its head when re-assembling the cards. In order to put
	 * the in order in the leftmost pile, I must have [Jack, Queen] [] [King] first.
	 * This would revert the program to the mode where it tries to lay out the
	 * cards.
	 * 
	 * Instead, it might be wiser to rely on modes that have to do with what kinds
	 * of card are showing, instead of the number of showing cards, or a combination
	 * of the two.
	 * 
	 * _______________________________________________________________________
	 * 
	 * I propose a new mode: "If you can see a Jack". I'm going to test and see if I
	 * can write code that does things "if it can see a jack" before I think more.
	 * If a Jack is showing, it means the program is not very far along. It doesn't
	 * mean the jack is in the right place though.
	 * 
	 * Maybe making the program behave correctly when the Jack is correctly or
	 * incorrectly buried seems less impossible than detecting whether [Jack, Queen]
	 * [] [King] is an accident. Mostly because the latter seems entirely impossible
	 * ________________________________________________________________________
	 * 
	 * 
	 * if jack is visible, try to put it on left. if there's something there, put it
	 * in the middle. if its already in the middle, move stuff on the left to the
	 * far right
	 * 
	 * 
	 * If I expose the jack, it will get moved to the right place. Can I move the
	 * Queen so that if the jack is not in the right place, it becomes exposed?
	 * 
	 * If the jack has already been exposed, there are 6 possibilities.
	 * 
	 * 
	 * _______________________________________________________
	 * 
	 * been trying to solve on a case by case basis for a few hours now. i read this
	 * paper once about gray codes. They made their permutations into a graph and
	 * used pathfinding techniques on them to get an answer
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

	/*
	 * private static void solve() { EnumSet<Card> visible = visible(); if
	 * (visible.contains(Card.JACK)) {
	 * 
	 * // deal with a special case: if (cards.get(0).isEmpty() &&
	 * compareAt(Card.KING, 2) && compareAt(Card.JACK, 1)) { move(Card.KING, 0);
	 * System.out.println("move the king to 0 for special case (looking for queen)"
	 * ); return; }
	 * 
	 * // deal with another special case: if (compareAt(Card.JACK, 0) &&
	 * compareAt(Card.QUEEN, 1) && cards.get(2).isEmpty()) { move(Card.JACK, 2);
	 * System.out.println("move the Jack to 2 for special case (looking for king)");
	 * return; }
	 * 
	 * // Always moving the jack to 2 from 1 // makes it easy to move cards from 0
	 * to 1, // but more importantly, it makes the special case possible. if
	 * (compareAt(Card.JACK, 1)) { move(Card.JACK, 2);
	 * System.out.println("move the jack to 2 to simplify"); return; }
	 * 
	 * // If the jack is visible, and the left is empty, then the jack is // on the
	 * right. // Try to move it to the left slot. if (cards.get(0).isEmpty()) {
	 * move(Card.JACK, 0); System.out.println("Jack put in place"); return; }
	 * 
	 * // if the left slot isn't empty, // the jack is there, or something's in the
	 * way.
	 * 
	 * // if the jack is in the right place, try and put the // queen on it if
	 * (compareAt(Card.JACK, 0)) {
	 * 
	 * 
	 * //J Q ?
	 * 
	 * if (visible.contains(Card.QUEEN)) {
	 * 
	 * 
	 * // specific case J,K,Q
	 * 
	 * if (compareAt(Card.KING, 1)) { move(Card.QUEEN, 0);
	 * System.out.println("specific case: Queen put in place"); return; }
	 * 
	 * if (compareAt(Card.QUEEN, 2)) { move(Card.QUEEN, 1);
	 * System.out.println("Preparing queen"); return; }
	 * 
	 * move(Card.QUEEN, 0); System.out.println("Queen put in place"); return;
	 * 
	 * } // if we have a jack and a king, the queen is // hidden, so I need // a
	 * system to ensure it gets uncovered.
	 * 
	 * // J X K
	 * 
	 * //There's only two ways to get to this case. I spawn there, or the jack was
	 * //moved, but the queen was under the king.
	 * 
	 * 
	 * if (visible.contains(Card.KING)) { // if the king is in the middle, check
	 * under it. / if (compareAt(Card.KING, 1)) { move(Card.KING, 2);
	 * System.out.println("moving king to look for queen"); return;
	 * 
	 * }
	 * 
	 * // if the king is on the right, move the jack move(Card.JACK, 1);
	 * System.out.println("moving jack to look for queen"); return; }
	 * 
	 * // I realize here ^ that: // in the case of [] [J] [Q, K], // the algorithm
	 * would fail, and keep moving the // jack from 1 to 0 // and back. So a special
	 * case is created for // just this arrangement.
	 * 
	 * //J X X
	 * 
	 * // There's something in the way
	 * 
	 * } else { move(top(0), 1); System.out.println("There's something in the way");
	 * return; }
	 * 
	 * }
	 * 
	 * if (visible.contains(Card.QUEEN) && visible.contains(Card.KING)) { if
	 * (compareAt(Card.QUEEN, 0)) {
	 * 
	 * move(Card.KING, 0); System.out.println("attempting victory"); return; }
	 * 
	 * }
	 * 
	 * randomize(); System.out.println("random");
	 * 
	 * }
	 * 
	 */

}
