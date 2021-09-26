package main.java;

public class Main {
    public static void main(String[] args) {
        Dictionary dictionary = new Dictionary();
        DictionaryManagement dictMan = new DictionaryManagement(dictionary);
        DictionaryCommandline dictCom = new DictionaryCommandline(dictionary);

        dictMan.insertFromFile();
        dictCom.showAllWords();
    }
}
