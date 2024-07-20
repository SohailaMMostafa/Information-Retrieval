/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package invertedIndex;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 *
 * @author ehab
 */
public class Test {

    public static void main(String[] args) throws IOException {
        Index5 index = new Index5();
        BiwordIndex biword = new BiwordIndex();
        PositionIndex position = new PositionIndex();
        //|**  change it to your collection directory
        //|**  in windows "C:\\tmp11\\rl\\collection\\"
        String files = "Assignment2/tmp11/tmp11/rl/collection/";

        File file = new File(files);
        //|** String[] 	list()
        //|**  Returns an array of strings naming the files and directories in the directory denoted by this abstract pathname.
        String[] fileList = file.list();

        fileList = index.sort(fileList);
        index.N = fileList.length;

        for (int i = 0; i < fileList.length; i++) {
            fileList[i] = files + fileList[i];
        }
        index.buildIndex(fileList);
        biword.buildBiWordIndex(fileList);
        biword.store("biword");
        biword.printDictionary();

//        String test3 = "data should plain greatest comif"; // data  should plain greatest comif
//        System.out.println("Boo0lean Model result = \n" + index.find_24_01(test3));

        String phrasetest1 = "information etrieval";
        System.out.println("BiWord Model result for '"+phrasetest1+"' = \n"
                + biword.searchBiWordIndex(phrasetest1.replace("\"", "").replace(" ", "_")));

        String phrasetest2 = "action space";
        System.out.println("BiWord Model result for '"+phrasetest2+"' = \n"
                + biword.searchBiWordIndex(phrasetest2.replace("\"", "").replace(" ", "_")));


        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String phrase = "";

//        do {
//            System.out.println("Print search phrase: ");
//            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//            phrase = in.readLine();
//            /// -3- **** complete here ****
//            System.out.println(index.find_24_01(phrase));
//        } while (!phrase.isEmpty());

        do {
            System.out.println("Enter your searching phrase: ");
            phrase = in.readLine();
            if (phrase.isEmpty()) break; // if the input is empty phrase, break.

            // Check if the phrase contains 2 words "Bi-word"
            if (phrase.startsWith("\"") && phrase.endsWith("\"")) {
                phrase = phrase.substring(1, phrase.length() - 1);
                System.out.println("BiWord Model result for '"+phrase+"' = \n" + position.PositionalIndex(phrase));
            } else if (!phrase.contains("\"")) {
                // if the input "phrase" is single word, use inverted index for searching
                System.out.println("Boolean Model result = \n" + index.find_24_01(phrase));
            } else {
                int indexOfFirstQoute = phrase.indexOf('\"');
                int indexOfSecondQoute = phrase.indexOf('\"', indexOfFirstQoute + 1);
                String biWord = phrase.substring(indexOfFirstQoute,indexOfSecondQoute + 1);
                phrase = phrase.replace(biWord,"").replace(" ","");
                biWord = biWord.substring(1, biWord.length() - 1).replace(" ", "_");
                String[] resOfBiWord = biword.searchBiWordIndex(biWord).split("\n");
                String[] resOfRevertedIndex = index.find_24_01(phrase).split("\n");
                System.out.println("Boolean Model result = \n");
                for(String word:resOfBiWord){
                    if(Arrays.asList(resOfRevertedIndex).contains(word)){
                        System.out.println(word);
                    }
                    else System.out.println("not found");
                }
            }
        } while (true);
    }
}
