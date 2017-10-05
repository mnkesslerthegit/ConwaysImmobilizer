import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import Utility.Point;

public class State {

	/**
	 * I'll use this to decide which state to get to next. The x is which slot to
	 * select a card, and the y is that cards destination.
	 */
	private Point moveCommand;

	private List<List<Card>> cards = new ArrayList<>();

	boolean victory = false;

	private int depth;

	private int number;

	public State() {
		for (int i = 0; i < 3; i++) {
			cards.add(new ArrayList<Card>());
		}
	}

	/**
	 * Gives a state that only has the visible cards
	 * 
	 * @return
	 */
	public State facade() {
		State result = new State();
		for (int i = 0; i < cards.size(); i++) {
			Card next = top(i);
			if (next != null) {
				result.cards.get(i).add(top(i));
			}
		}
		return result;

	}

	public int hashCode() {
		return this.toString().hashCode();
	}

	/**
	 * deep copy of the other states cards list
	 * 
	 * @param other
	 */
	public State(State other) {
		for (int i = 0; i < 3; i++) {
			cards.add(new ArrayList<Card>());
			for (int q = 0; q < other.cards.get(i).size(); q++) {

				cards.get(i).add(other.cards.get(i).get(q));
			}
		}
		if (other.moveCommand != null) {
			this.moveCommand = new Point(other.moveCommand);
		} else {
			moveCommand = null;
		}

		depth = other.depth;
		number = other.number;
	}

	public boolean equals(Object other) {

		if (other == null)
			return false;

		if (other == this)
			return true;
		if (!(other instanceof State))
			return false;
		State s2 = (State) other;

		if (s2.cards.size() != cards.size()) {
			return false;
		}

		for (int i = 0; i < cards.size(); i++) {
			if (cards.get(i).size() != s2.cards.get(i).size()) {
				return false;
			}
			for (int j = 0; j < cards.get(i).size(); j++) {
				if (!cards.get(i).get(j).equals(s2.cards.get(i).get(j))) {
					return false;
				}
			}
		}
		return true;

	}

	public void add(Card next, int index) {
		cards.get(index).add(next);
	}

	public boolean isValid() {
		List<Card> all = new ArrayList<>();
		for (int i = 0; i < cards.size(); i++) {
			all.addAll(cards.get(i));

		}

		return all.containsAll(EnumSet.allOf(Card.class));

	}

	/**
	 * Gets the top card at the given index
	 * 
	 * @param index
	 * @return
	 */
	public Card top(int index) {

		if (!cards.get(index).isEmpty()) {
			return cards.get(index).get(cards.get(index).size() - 1);
		} else {
			return null;
		}
	}

	/**
	 * Moves a card from where it is, to the top of the stack at the given index.
	 * Doesn't care if the move is legal.
	 * 
	 * @param target
	 * @param i
	 */
	public void move(Card target, int i) {
		for (List<Card> l : cards) {
			l.remove(target);
		}

		cards.get(i).add(target);
	}

	/**
	 * Returns a set which shows all the cards the player could see
	 * 
	 * @return
	 */
	public EnumSet<Card> visible() {
		List<Card> visible = new ArrayList<>();
		for (List<Card> l : cards) {
			if (!l.isEmpty()) {
				visible.add(l.get(l.size() - 1));
			}
		}

		EnumSet<Card> result = EnumSet.copyOf(visible);
		return result;

	}

	public String toString() {
		return cards.toString();

	}

	public void checkWin() {
		List<Card> first = cards.get(0);

		if (first != null && first.size() == 3 && first.get(0).equals(Card.JACK) && first.get(1).equals(Card.QUEEN)
				&& first.get(2).equals(Card.KING)) {
			victory = true;
		}
	}

	/**
	 * Checks if the top card at the given index equals the given card
	 * 
	 * @param card
	 * @param num
	 * @return
	 */
	public boolean compareAt(Card card, int num) {
		Card target = top(num);
		if (target == null) {
			return false;
		}

		return card.equals(target);

	}

	/**
	 * Tries to move the target card to an empty space
	 * 
	 * @param target
	 * @return whether anything moved
	 */
	public boolean moveToEmpty(Card target) {
		// find which slot is empty
		for (int i = 0; i < 3; i++) {
			if (cards.get(i).isEmpty()) {
				move(target, i);
				return true;
			}
		}
		return false;

	}

	public void randomize() {
		cards.clear();
		for (int i = 0; i < 3; i++) {
			cards.add(new ArrayList<Card>());
		}
		List<Card> inOrder = new ArrayList<>();
		inOrder.add(Card.JACK);
		inOrder.add(Card.KING);
		inOrder.add(Card.QUEEN);
		Collections.shuffle(inOrder);
		for (int i = 0; i < cards.size(); i++) {
			int pile = JQK.randy.nextInt(3);

			cards.get(pile).add(inOrder.get(i));
		}

	}

	public Point getMoveCommand() {
		return moveCommand;
	}

	public void setMoveCommand(Point moveCommand) {
		this.moveCommand = moveCommand;
	}

	/**
	 * assumes a move command is already set
	 */
	public boolean move() {
		Card target = top(this.moveCommand.getX());
		if (target != null) {
			move(target, this.moveCommand.getY());
			return true;
		}
		return false;
	}

	public void setDepth(int depth) {
		this.depth = depth;

	}

	public int getDepth() {
		return depth;
	}

	public void setNumber(int i) {
		this.number = i;
	}

	public int getNumber() {
		return number;
	}

}
