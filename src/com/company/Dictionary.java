package com.company;

public class Dictionary {
    private Word[] dictionary = new Word[100];
    private int count = 0;

    public void addWord(String wordTarget, String wordExplain) {
        dictionary[count] = new Word(wordTarget, wordExplain);
        count++;
    }

    public void removeWord(String wordTarget) {
        for (int i = 0; i < count; i++) {
            if (dictionary[i].getWordTarget().equals(wordTarget)) {
                System.arraycopy(dictionary, i + 1, dictionary, i, count - 1 - i);
                dictionary[count - 1] = null;
                count--;

                break;
            }
        }
    }

}
