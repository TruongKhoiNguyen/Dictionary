package main.java;

import java.util.ArrayList;

public class Dictionary {
    private final ArrayList<Word> dictionary = new ArrayList<>();

    /** Add word. */
    public void addWord(String wordTarget, String wordExplain) {
        for (Word i : dictionary) {
           if (wordTarget.equals(i.wordTarget())) {
               return;
           }
       }

       dictionary.add(new Word(wordTarget, wordExplain));
    }

    /** Remove word */
    public void removeWord(String wordTarget) {
        for (int i = 0; i < dictionary.size(); i++) {
            if (wordTarget.equals(dictionary.get(i).wordTarget())) {
                dictionary.remove(i);
                break;
            }
        }
    }

    /** Get size of dictionary. */
    public int size() {
        return this.dictionary.size();
    }

    /** Get wordTarget given position. */
    public String getWordTarget(int position) {
        return dictionary.get(position).wordTarget();
    }

    /** Get wordExplain given position. */
    public String getWordExplain(int position) {
        return dictionary.get(position).wordExplain();
    }
}
