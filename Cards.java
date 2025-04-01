import java.util.HashMap;

public class Cards {
    private HashMap<String, Integer> cards = new HashMap<>();
    private final String[] card_types = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};

    public Cards() {
        for (int i = 0; i < 13; i++) {
            cards.put(card_types[i], 8);
        }
    }

    private int getCardTotal() {
        int count = 0;
        for (int i = 0; i < 13; i++) {
            count += cards.get(card_types[i]);
        }
        return count;
    }

    public String[] drawCards(int n) throws IndexOutOfBoundsException {

        int cardCount = getCardTotal();
        if (n > cardCount) {
            throw new IndexOutOfBoundsException();
        }

        String[] drawnCards = new String[n];

        for (int j = 0; j < n; j++) {

            int random = (int) (Math.random() * cardCount);
            int num;

            for (int i = 0; i < 13; i++) {
                num = cards.get(card_types[i]);
                if (random < num) {
                    drawnCards[j] = card_types[i];
                    cards.replace(card_types[i], num - 1);
                    break;
                }
                random -= num;
            }
            cardCount--;
        }

        return drawnCards;
    }
}
