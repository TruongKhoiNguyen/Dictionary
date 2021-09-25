package main.java;

import main.java.Dictionary;
import main.java.DictionaryCommandline;
import main.java.DictionaryManagement;

public class Main {
    public static void main(String[] args) {
        Dictionary dictionary = new Dictionary();
        DictionaryManagement dictMan = new DictionaryManagement(dictionary);
        DictionaryCommandline dictCom = new DictionaryCommandline(dictionary);

        dictMan.insertFromCommandline();
        dictCom.showAllWords();
    }
}
