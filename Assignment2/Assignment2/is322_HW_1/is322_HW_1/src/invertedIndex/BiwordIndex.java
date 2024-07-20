package invertedIndex;
import java.io.*;

public class BiwordIndex extends Index5{

    public void buildBiWordIndex(String[] files) {
        int fid = 0;
        for (String fileName : files) {
            try (BufferedReader file = new BufferedReader(new FileReader(fileName))) {
                if (!sources.containsKey(fileName)) {
                    sources.put(fid, new SourceRecord(fid, fileName, fileName, "notext"));
                }
                String ln;
                String prevWord = null; // Store the previous word for bi-word indexing
                int flen = 0;
                while ((ln = file.readLine()) != null) {
                    String[] words = ln.split("\\W+"); // Split the line into words
                    for (int i = 0; i < words.length; i++) {
                        if (prevWord != null) {
                            String bigram = prevWord + "_" + words[i];
                            flen += indexOnBiWord(bigram, fid, i); // Function to index a bi-word and increment file length by word count
                        }
                        prevWord = words[i]; // Update the previous word to the current for the next iteration
                    }
                }
                sources.get(fid).length = flen;
            } catch (IOException e) {
                System.out.println("File " + fileName + " not found. Skip it");
            }
            fid++;
        }
    }

    public int indexOnBiWord(String biword, int fileId, int position) {
        biword = biword.toLowerCase();  // Normalize to lowercase

        // Split the bi-words into individual words to check for stop words
        String[] words = biword.split("\\W+");
        for (String word : words) {
            if (stopWord(word)) {
                return 0;  // if there word in the bi-words is stop word, just skip it
            }
        }

        // Stemming each word in the bi-words
        String stemmedWord = "";
        for (String word : words) {
            stemmedWord += stemWord(word) + " ";
        }
        stemmedWord = stemmedWord.trim();  // if there space in the word, remove it

        // check if the "stemmed" word is on the dict, if not -> add it
        if (!index.containsKey(stemmedWord)) {
            index.put(stemmedWord, new DictEntry());
        }

        // Add docId to the posting list
        if (!index.get(stemmedWord).postingListContains(fileId)) {
            index.get(stemmedWord).doc_freq += 1;  // Increment document frequency
            if (index.get(stemmedWord).pList == null) {
                index.get(stemmedWord).pList = new Posting(fileId);
                index.get(stemmedWord).last = index.get(stemmedWord).pList;
            } else {
                index.get(stemmedWord).last.next = new Posting(fileId);
                index.get(stemmedWord).last = index.get(stemmedWord).last.next;
            }
        } else {
            index.get(stemmedWord).last.dtf += 1;  // Increment term frequency in document
        }

        // Increment total term frequency in the collection
        index.get(stemmedWord).term_freq += 1;

        // If the bi-words includes a specific word, e.g., "lattice", you can debug or log here
        if (stemmedWord.contains("lattice")) {
            System.out.println("  <<" + index.get(stemmedWord).getPosting(1) + ">> " + biword);
        }

        return 2;  // Return 2 as the bi-words contains of 2 words "return true"
    }

    public String searchBiWordIndex(String biWordPhrase) {
        String result = "";
        // Normalize and process the bi-word phrase for searching
        biWordPhrase = biWordPhrase.toLowerCase().replace(" ", "_");

        // Retrieve the posting list for the bi-word directly
        Posting posting = index.containsKey(biWordPhrase) ? index.get(biWordPhrase).pList : null;

        // If no postings, return No results found
        if (posting == null) {
            return "No results found for '" + biWordPhrase.replace("_", " ") + "'.";
        }

        // Iterate through the postings and build the result string
        while (posting != null) {
            String docTitle = sources.get(posting.docId).title;
            int docLength = sources.get(posting.docId).length;
            result += "\t" + posting.docId + " - " + docTitle + " - " + docLength + "\n";
            posting = posting.next;
        }
        return result;
    }
}
