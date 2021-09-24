package com.company;

import java.util.ArrayList;

public class Dictionary {
    private ArrayList<Word> dictionary = new ArrayList<>();

    public void addWord(String wordTarget, String wordExplain) {
       boolean duplicate = false;

       for (Word i : dictionary) {
           if (wordTarget.equals(i.getWordTarget())) {
               duplicate = true;
               break;
           }
       }

       if (!duplicate) {
           dictionary.add(new Word(wordTarget, wordExplain));
       }
    }

    public void removeWord(String wordTarget) {
        int position = -1;

        for (int i = 0; i < dictionary.size(); i++) {
            if (wordTarget.equals(dictionary.get(i).getWordTarget())) {
                position = i;
                break;
            }
        }

        if (position >= 0) {
            dictionary.remove(position);
        }
    }

    /** Get size of dictionary. */
    public int size() {
        return this.dictionary.size();
    }

    /** Get wordTarget given position. */
    public String getWordTarget(int position) {
        return dictionary.get(position).getWordTarget();
    }

    /** Get wordExplain given position. */
    public String getWordExplain(int position) {
        return dictionary.get(position).getWordExplain();
    }
}
