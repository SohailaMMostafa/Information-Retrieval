/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package invertedIndex;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author ehab
 */
public class Index5 {

    //--------------------------------------------
    private Map<Integer, double[]> docVectors = new HashMap<>();
    int N = 0;
    public Map<Integer, SourceRecord> sources;  // store the doc_id and the file name.

    public HashMap<String, DictEntry> index; // THe inverted index

    public Map<Integer,Integer>docLengths = new HashMap<>();

    //--------------------------------------------

    SortedScore sortedScore;

    //--------------------------------------------
    public Index5() {
        sources = new HashMap<Integer, SourceRecord>();
        index = new HashMap<String, DictEntry>();
    }

    public void setN(int n) {
        N = n;
    }


    //---------------------------------------------
    public void printPostingList(Posting p) {
        // Iterator<Integer> it2 = hset.iterator();
        System.out.print("[");
        while (p != null) {
            /// -4- **** complete here ****
            // fix get rid of the last comma
            System.out.print("" + p.docId);
            p = p.next;
            if (p != null){
                System.out.print(',');
            }
        }
        System.out.println("]");
    }

    //---------------------------------------------
    public void printDictionary() {
        Iterator it = index.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            DictEntry dd = (DictEntry) pair.getValue();
            System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "]       =--> ");
            printPostingList(dd.pList);
        }
        System.out.println("------------------------------------------------------");
        System.out.println("*** Number of terms = " + index.size());
    }

    //----------------------------------------------------------------------------
    public int buildIndex(String ln, int fid) {
        int flen = 0;

        String[] words = ln.split("\\W+");
        //  String[] words = ln.replaceAll("(?:[^a-zA-Z0-9 -]|(?<=\\w)-(?!\\S))", " ").toLowerCase().split("\\s+");
        flen += words.length;
        for (String word : words) {
            word = word.toLowerCase();
            if (stopWord(word)) {
                continue;
            }
            word = stemWord(word);
            // check to see if the word is not in the dictionary
            if (!index.containsKey(word)) {
                index.put(word, new DictEntry());
            }
            // add document id to the posting list
            if (!index.get(word).postingListContains(fid)) {
                index.get(word).doc_freq += 1; //set doc freq to the number of doc that contain the term
                if (index.get(word).pList == null) {
                    index.get(word).pList = new Posting(fid);
                    index.get(word).last = index.get(word).pList;
                } else {
                    index.get(word).last.next = new Posting(fid);
                    index.get(word).last = index.get(word).last.next;
                }
            } else {
                index.get(word).last.dtf += 1;
            }
            //set the term_fteq in the collection
            index.get(word).term_freq += 1;
            if (word.equalsIgnoreCase("lattice")) {

                System.out.println("  <<" + index.get(word).getPosting(1) + ">> " + ln);
            }

        }
        return flen;
    }

    //-----------------------------------------------
    public void buildIndex(String[] files) {  // from disk not from the internet
        int fid = 0;
        for (String fileName : files) {
            try (BufferedReader file = new BufferedReader(new FileReader(fileName))) {
                if (!sources.containsKey(fileName)) {
                    sources.put(fid, new SourceRecord(fid, fileName, fileName, "notext"));
                }
                String ln;
                int flen = 0;
                while ((ln = file.readLine()) != null) {
                    /// -2- **** complete here ****
                    ///**** hint   flen +=  ________________(ln, fid);
                    flen += indexOneLine(ln, fid);
                }
                sources.get(fid).length = flen;

            } catch (IOException e) {
                System.out.println("File " + fileName + " not found. Skip it");
            }
            fid++;
        }
        //   printDictionary();
    }


    //----------------------------------------------------------------------------
    public int indexOneLine(String ln, int fid) {
        int flen = 0;

        String[] words = ln.split("\\W+");
        //   String[] words = ln.replaceAll("(?:[^a-zA-Z0-9 -]|(?<=\\w)-(?!\\S))", " ").toLowerCase().split("\\s+");
        flen += words.length;
        for (String word : words) {
            word = word.toLowerCase();
            if (stopWord(word)) {
                continue;
            }
            word = stemWord(word);
            // check to see if the word is not in the dictionary
            // if not add it
            if (!index.containsKey(word)) {
                index.put(word, new DictEntry());
            }
            // add document id to the posting list
            if (!index.get(word).postingListContains(fid)) {
                index.get(word).doc_freq += 1; //set doc freq to the number of doc that contain the term
                if (index.get(word).pList == null) {
                    index.get(word).pList = new Posting(fid);
                    index.get(word).last = index.get(word).pList;
                } else {
                    index.get(word).last.next = new Posting(fid);
                    index.get(word).last = index.get(word).last.next;
                }
            } else {
                index.get(word).last.dtf += 1;
            }
            //set the term_fteq in the collection
            index.get(word).term_freq += 1;
            if (word.equalsIgnoreCase("lattice")) {

                System.out.println("  <<" + index.get(word).getPosting(1) + ">> " + ln);
            }

        }
        return flen;
    }

    //----------------------------------------------------------------------------
    boolean stopWord(String word) {
        if (word.equals("the") || word.equals("to") || word.equals("be") || word.equals("for") || word.equals("from") || word.equals("in")
                || word.equals("a") || word.equals("into") || word.equals("by") || word.equals("or") || word.equals("and") || word.equals("that")) {
            return true;
        }
        if (word.length() < 2) {
            return true;
        }
        return false;

    }
//----------------------------------------------------------------------------

    String stemWord(String word) { //skip for now
        return word;
//        Stemmer s = new Stemmer();
//        s.addString(word);
//        s.stem();
//        return s.toString();
    }

    //----------------------------------------------------------------------------
    Posting intersect(Posting pL1, Posting pL2) {
///****  -1-   complete after each comment ****
//   INTERSECT ( p1 , p2 )
//          1  answer ←      {}
        Posting answer = null;
        Posting last = null;
//      2 while p1  != NIL and p2  != NIL

//          3 do if docID ( p 1 ) = docID ( p2 )

//          4   then ADD ( answer, docID ( p1 ))
        // answer.add(pL1.docId);

//          5       p1 ← next ( p1 )
//          6       p2 ← next ( p2 )

        //          7   else if docID ( p1 ) < docID ( p2 )

//          8        then p1 ← next ( p1 )
//          9        else p2 ← next ( p2 )

//      10 return answer
        return answer;
    }


    //---------------------------------
    String[] sort(String[] words) {  //bubble sort
        boolean sorted = false;
        String sTmp;
        //-------------------------------------------------------
        while (!sorted) {
            sorted = true;
            for (int i = 0; i < words.length - 1; i++) {
                int compare = words[i].compareTo(words[i + 1]);
                if (compare > 0) {
                    sTmp = words[i];
                    words[i] = words[i + 1];
                    words[i + 1] = sTmp;
                    sorted = false;
                }
            }
        }
        return words;
    }

    //==========================================================
    public String find_07a(String phrase) {
        System.out.println("-------------------------  find_07 -------------------------");

        String result = "";
        String[] words = phrase.split("\\W+");
        int len = words.length;
        SortedScore sortedScores = new SortedScore();

        double[] queryVector = new double[len];
        Map<Integer, double[]> docVectors = new HashMap<>();

        for (int i = 0; i < N; i++) {
            docVectors.put(i, new double[len]);
        }

        for (int i = 0; i < len; i++) {
            String term = words[i].toLowerCase();
            queryVector[i] = 0.0;

            if (!index.containsKey(term)) continue;
            DictEntry entry = index.get(term);
            double tdf = entry.doc_freq;
            tdf = tdf / docLengths.get(i);

            double idf = 1 + (Math.log10((double) tdf));

            queryVector[i] = idf;

            Posting p = entry.pList;
            while (p != null) {
                double[] docVector = docVectors.get(p.docId);
                docVector[i] = (1 + Math.log10((double) p.dtf)) * idf;
                docVectors.put(p.docId, docVector);
                p = p.next;
            }
        }

        for (int i = 0; i < N; i++) {
            double[] docVector = docVectors.get(i);
            double dotProduct = 0.0;
            double docNorm = 0.0;
            double queryNorm = 0.0;

            for (int j = 0; j < len; j++) {
                dotProduct += queryVector[j] * docVector[j];
                docNorm += docVector[j] * docVector[j];
                queryNorm += queryVector[j] * queryVector[j];
            }

            double cosineSimilarity = 0.0;
            if (docNorm != 0.0 && queryNorm != 0.0) {
                cosineSimilarity = dotProduct / (Math.sqrt(docNorm) * Math.sqrt(queryNorm));
            }

            sortedScores.insertScoreRecord(cosineSimilarity, "URL" + i, "Title" + i, "Description" + i);
        }

        int K = 10;
        result = sortedScores.printScores(K);

        return result;
    }

    /////---------------------------------
    public void searchLoop() {

        String phrase;
        do {
            System.out.println("Print search phrase: ");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            try {
                phrase = in.readLine();
                System.out.println(find_07a(phrase));
                //        find_08(phrase);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        } while (!phrase.isEmpty());

    }
    //---------------------------------

    public void store(String storageName) {
        try {
            String pathToStorage = "C:\\Users\\sohyl\\AI\\final term\\information retrevail\\Assignments\\Assignment3\\is322_HW_3\\test\\" + storageName;
            Writer wr = new FileWriter(pathToStorage);
            for (Map.Entry<Integer, SourceRecord> entry : sources.entrySet()) {
                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue().URL + ", Value = " + entry.getValue().title + ", Value = " + entry.getValue().text);
                wr.write(entry.getKey().toString() + ",");
                wr.write(entry.getValue().URL.toString() + ",");
                wr.write(entry.getValue().title.replace(',', '~') + ",");
                wr.write(entry.getValue().length + ","); //String formattedDouble = String.format("%.2f", fee );
                wr.write(String.format("%4.4f", entry.getValue().norm) + ",");
                wr.write(entry.getValue().text.toString().replace(',', '~') + "\n");
            }
            wr.write("section2" + "\n");

            Iterator it = index.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                DictEntry dd = (DictEntry) pair.getValue();
                //  System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "] <" + dd.term_freq + "> =--> ");
                wr.write(pair.getKey().toString() + "," + dd.doc_freq + "," + dd.term_freq + ";");
                Posting p = dd.pList;
                while (p != null) {
                    //    System.out.print( p.docId + "," + p.dtf + ":");
                    wr.write(p.docId + "," + p.dtf + ":");
                    p = p.next;
                }
                wr.write("\n");
            }
            wr.write("end" + "\n");
            wr.close();
            System.out.println("=============EBD STORE=============");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //=========================================
    public boolean storageFileExists(String storageName) {
        java.io.File f = new java.io.File("/home/ehab/tmp11/rl/" + storageName);
        if (f.exists() && !f.isDirectory())
            return true;
        return false;

    }

    //----------------------------------------------------
    public void createStore(String storageName) {
        try {
            String pathToStorage = "/home/ehab/tmp11/" + storageName;
            Writer wr = new FileWriter(pathToStorage);
            wr.write("end" + "\n");
            wr.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

//=====================================================================
