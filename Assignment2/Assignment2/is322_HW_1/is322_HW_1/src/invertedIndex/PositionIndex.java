package invertedIndex;
import java.util.ArrayList;
import java.util.List;

public class PositionIndex extends Index5{

    public String PositionalIndex(String Phrase) {
        String result = "";

        String[] positionalList = Phrase.toLowerCase().split(" ");

        List<Posting> postingList = new ArrayList<>();
        for (String phrase : positionalList) {
            if (index.containsKey(phrase)) {
                postingList.add(index.get(phrase).pList);
            } else {
                return "No results found for " + Phrase;
            }
        }

        if (!postingList.isEmpty()) {
            Posting firstPosting = postingList.getFirst();
            while (firstPosting != null) {
                for (int position : firstPosting.positions) {
                    boolean found = true;
                    for (int wordNum = 1; wordNum < postingList.size(); wordNum++) {
                        Posting currPosting = postingList.get(wordNum);

                        while (currPosting != null && currPosting.docId != firstPosting.docId) {
                            currPosting = currPosting.next;
                        }

                        if (currPosting == null) {
                            found = false;
                            break;
                        }

                        if (!currPosting.positions.contains(position + wordNum)) {
                            found = false;
                            break;
                        }
                    }
                    if (found) {
                        String docTitle = sources.get(firstPosting.docId).title;
                        int docLength = sources.get(firstPosting.docId).length;
                        result += "\t" + firstPosting.docId + " - " + docTitle + " - " + docLength + "\n";
                        break;
                    }
                }
                firstPosting = firstPosting.next;
            }
        } else {
            return "No results found for " + Phrase;
        }

        return result;
    }

}
