package main.java;

public class Word {
    private int id;
    private String word;
    private String description;
    private String pronounce;
    private String date_add;

    public Word(int id) {
        this.id = id;
    }

    public Word(int id, String word, String description, String pronounce, String dateadd) {
        this.id = id;
        this.word = word;
        this.description = description;
        this.pronounce = pronounce;
        this.date_add = dateadd;
    }

    public Word(String word, String description, String pronounce) {
        this.id = 0;
        this.word = word;
        this.description = description;
        this.pronounce = pronounce;
        this.date_add = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPronounce() {
        return pronounce;
    }

    public void setPronounce(String pronounce) {
        this.pronounce = pronounce;
    }

    public String getDate_add() {
        return date_add;
    }

    public void setDate_add(String date_add) {
        this.date_add = date_add;
    }

    @Override
    public String toString() {
        return this.word + "/" + this.pronounce + "/\n" + this.description + ".";
    }
}

