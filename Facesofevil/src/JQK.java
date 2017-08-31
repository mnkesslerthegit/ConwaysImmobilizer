


import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class JQK {

	enum Card{
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
//		System.out.println("Start");
		randomize();
		print();
		while (!victory) {
			solve();
			checkWin();
		}
		print();

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
	 * if jack is visible, try to put it on left. 
	 * if there's something there, put it in the middle. 
	 * if its already in the middle,
	 * move stuff on the left to the far right
	 * 
	 * 
	 * If I expose the jack, it will get moved to the right place. 
	 * Can I move the Queen so that if the jack is not in the right place, it becomes exposed? 
	 * 
	 * If the jack has already been exposed, there are 6 possibilities. 
	 * 
	 * 
	 * 		 
	 * 
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
			//move jack to empty left
			if(cards.get(0).isEmpty()){ 
				Move(Card.JACK,0);
			}else{
				System.out.println(top(0));
				if(!top(0).equals(Card.JACK)){
					Move(top(0), 1);
				}else{
					//	Move(Card.JACK, 1);
				}
			}
		} else {
			randomize();
			System.out.println("random");
		}

		String input = scan.nextLine();
		print();
		if (input.equals("q")) {
			System.exit(0);
		}

	}

	private static Card top(int index){

		if(!cards.get(index).isEmpty()){
			return cards.get(index).get(cards.get(index).size()-1);
		}else{
			return null;
		}
	}

	private static void Move(Card target, int i) {
		for (List<Card> l : cards) {
			l.remove(target);
		}

		cards.get(i).add(target);
	}

	private static EnumSet<Card> visible() {
		List<Card> visible = new ArrayList<>();
		for (List<Card> l : cards) {
			if (!l.isEmpty()) {
				visible.add(l.get(l.size()-1));
			}
		}

		EnumSet<Card> result = EnumSet.copyOf(visible);
		return result;

	}

	private static void print() {
		System.out.println(cards);

	}

	private static void checkWin() {
		if (cards.get(0).equals(Card.JACK) && cards.get(1).equals(Card.QUEEN)
			&& cards.get(2).equals(Card.KING))
			victory = true;
	}



}
