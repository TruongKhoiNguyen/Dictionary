package com.company;

import java.util.ArrayList;

public class Dictionary {
    private ArrayList<Word> dictionary;

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

}
