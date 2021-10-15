package main.archives;

import java.util.ArrayList;

public class Dictionary {
    private final ArrayList<Word> dictionary;

    public Dictionary() {
        dictionary = new ArrayList<>();
    }

    public int size() {
        return this.dictionary.size();
    }

    public String getWordTarget(int position) {
        return dictionary.get(position).wordTarget();
    }

    public String getWordExplain(int position) {
        return dictionary.get(position).wordExplain();
    }

    public void addWord(String wordTarget, String wordExplain) {
        for (Word i : dictionary) {
           if (wordTarget.equals(i.wordTarget())) {
               return;
           }
       }

       dictionary.add(new Word(wordTarget, wordExplain));
    }

    public void removeWord(String wordTarget) {
        for (int i = 0; i < dictionary.size(); i++) {
            if (wordTarget.equals(dictionary.get(i).wordTarget())) {
                dictionary.remove(i);
                break;
            }
        }
    }
}
