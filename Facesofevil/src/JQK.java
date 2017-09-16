import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class JQK {

	enum Card {
		JACK, QUEEN, KING

	}

	private static boolean victory = false;
	private static List<List<Card>> cards = new ArrayList<>();
	private static Random randy = new Random();
	private static Scanner scan = new Scanner(System.in);

	public static void randomize() {
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
			int pile = randy.nextInt(3);

			cards.get(pile).add(inOrder.get(i));
		}

	}

	public static void main(String[] args) {
		System.out.println("Start");
		randomize();
//		for (int i = 0; i < 3; i++) {
//			cards.add(new ArrayList<Card>());
//		}
//		cards.get(0).add(Card.KING);
//		cards.get(2).add(Card.QUEEN);
//		cards.get(0).add(Card.JACK);
		print();
		while (!victory) {
			solve();
			print();
			checkWin();
			manageInput();
		}
		System.out.println("victory acheived");
		print();

	}

	private static void manageInput() {

		String input = scan.nextLine();

		if (input.equals("q")) {
			System.exit(0);
		}

		if (input.equals("r")) {
			randomize();
		}

	}

	/**
	 * 
	 * Thoughts:
	 * 
	 * My initial thoughts were:
	 * "First I will get all the cards laid, out, so they are easy to work with."
	 * "then I'll get them in order, and then put them back together."
	 * 
	 * This idea seemed good because it would allow the program to have 3 modes.
	 * If the program detects three face up cards, it can easily switch them
	 * into any order. That mode is easy.
	 * 
	 * The problem is getting to and from the state with three face up cards.
	 * When there are 2 piles, there's no way to know which pile has two cards.
	 * So I'll have to devise a strategy that turns two piles into three, no
	 * matter where the third card is.
	 * 
	 * The same problem rears its head when re-assembling the cards. In order to
	 * put the in order in the leftmost pile, I must have [Jack, Queen] []
	 * [King] first. This would revert the program to the mode where it tries to
	 * lay out the cards.
	 * 
	 * Instead, it might be wiser to rely on modes that have to do with what
	 * kinds of card are showing, instead of the number of showing cards, or a
	 * combination of the two.
	 * 
	 * _______________________________________________________________________
	 * 
	 * I propose a new mode: "If you can see a Jack". I'm going to test and see
	 * if I can write code that does things "if it can see a jack" before I
	 * think more. If a Jack is showing, it means the program is not very far
	 * along. It doesn't mean the jack is in the right place though.
	 * 
	 * Maybe making the program behave correctly when the Jack is correctly or
	 * incorrectly buried seems less impossible than detecting whether [Jack,
	 * Queen] [] [King] is an accident. Mostly because the latter seems entirely
	 * impossible
	 * ________________________________________________________________________
	 * 
	 * 
	 * if jack is visible, try to put it on left. if there's something there,
	 * put it in the middle. if its already in the middle, move stuff on the
	 * left to the far right
	 * 
	 * 
	 * If I expose the jack, it will get moved to the right place. Can I move
	 * the Queen so that if the jack is not in the right place, it becomes
	 * exposed?
	 * 
	 * If the jack has already been exposed, there are 6 possibilities.
	 * 
	 * 
	 * _______________________________________________________
	 * 
	 * been trying to solve on a case by case basis for a few hours now. 
	 * i read this paper once about gray codes. They made their permutations into a graph
	 * and used pathfinding techniques on them to get an answer
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

	/**
	 * solve
	 */
	private static void solve() {
		EnumSet<Card> visible = visible();
		if (visible.contains(Card.JACK)) {

			// deal with a special case:
			if (cards.get(0).isEmpty() && compareAt(Card.KING, 2) && compareAt(Card.JACK, 1)) {
				move(Card.KING, 0);
				System.out.println("move the king to 0 for special case (looking for queen)");
				return;
			}

			// deal with another special case:
			if (compareAt(Card.JACK, 0) && compareAt(Card.QUEEN, 1) && cards.get(2).isEmpty()) {
				move(Card.JACK, 2);
				System.out.println("move the Jack to 2 for special case (looking for king)");
				return;
			}
			

			// Always moving the jack to 2 from 1
			// makes it easy to move cards from 0 to 1,
			// but more importantly, it makes the special case possible.
			if (compareAt(Card.JACK, 1)) {
				move(Card.JACK, 2);
				System.out.println("move the jack to 2 to simplify");
				return;
			}

			// If the jack is visible, and the left is empty, then the jack is
			// on the right.
			// Try to move it to the left slot.
			if (cards.get(0).isEmpty()) {
				move(Card.JACK, 0);
				System.out.println("Jack put in place");
				return;
			}		

			// if the left slot isn't empty,
			// the jack is there, or something's in the way.

			// if the jack is in the right place, try and put the
			// queen on it
			if (compareAt(Card.JACK, 0)) {

				/**
				 * J Q ?
				 * 
				 * 
				 */
				if (visible.contains(Card.QUEEN)) {
					
					/**
					 * specific case J,K,Q
					 */
					if(compareAt(Card.KING, 1)){
						move(Card.QUEEN, 0);
						System.out.println("specific case: Queen put in place");
						return;
					}
					
					if (compareAt(Card.QUEEN, 2)) {
						move(Card.QUEEN, 1);
						System.out.println("Preparing queen");
						return;
					}

					move(Card.QUEEN, 0);
					System.out.println("Queen put in place");
					return;

				}
				// if we have a jack and a king, the queen is
				// hidden, so I need
				// a system to ensure it gets uncovered.
				/**
				 * J X K
				 * 
				 * There's only two ways to get to this case. I spawn there, or
				 * the jack was moved, but the queen was under the king.
				 */

				if (visible.contains(Card.KING)) {
					// if the king is in the middle, check under it. /
					if (compareAt(Card.KING, 1)) {
						move(Card.KING, 2);
						System.out.println("moving king to look for queen");
						return;

					}

					// if the king is on the right, move the jack
					move(Card.JACK, 1);
					System.out.println("moving jack to look for queen");
					return;
				}

				// I realize here ^ that:
				// in the case of [] [J] [Q, K],
				// the algorithm would fail, and keep moving the
				// jack from 1 to 0
				// and back. So a special case is created for
				// just this arrangement.

				/**
				 * J X X
				 */

				// There's something in the way
				
				
			
			} else {
				move(top(0), 1);
				System.out.println("There's something in the way");
				return;
			}

		}

		if (visible.contains(Card.QUEEN) && visible.contains(Card.KING)) {
			if (compareAt(Card.QUEEN, 0)) {

				move(Card.KING, 0);
				System.out.println("attempting victory");
				return;
			}

		}

		randomize();
		System.out.println("random");

	}

	private static boolean compareAt(Card card, int num) {
		Card target = top(num);
		if (target == null) {
			return false;
		}

		return card.equals(target);

	}

	private static void moveToEmpty(Card target) {
		// find which slot is empty
		for (int i = 0; i < 3; i++) {
			if (cards.get(i).isEmpty()) {
				move(target, i);
				return;
			}
		}

	}

	private static Card top(int index) {

		if (!cards.get(index).isEmpty()) {
			return cards.get(index).get(cards.get(index).size() - 1);
		} else {
			return null;
		}
	}

	private static void move(Card target, int i) {
		for (List<Card> l : cards) {
			l.remove(target);
		}

		cards.get(i).add(target);
	}

	private static EnumSet<Card> visible() {
		List<Card> visible = new ArrayList<>();
		for (List<Card> l : cards) {
			if (!l.isEmpty()) {
				visible.add(l.get(l.size() - 1));
			}
		}

		EnumSet<Card> result = EnumSet.copyOf(visible);
		return result;

	}

	private static void print() {
		System.out.println(cards);

	}

	private static void checkWin() {
		List<Card> first = cards.get(0);

		if (first != null && first.size() == 3 && first.get(0).equals(Card.JACK) && first.get(1).equals(Card.QUEEN)
				&& first.get(2).equals(Card.KING)) {
			victory = true;
		}
	}

}
