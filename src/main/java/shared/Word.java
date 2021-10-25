package shared;

import java.util.Date;

public class Word {
    private int id;
    private String keyWord;
    private String description;
    private String pronunciation;
    private Date addedDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public Date getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }

    public Word(int id, String keyWord, String description, String pronunciation, Date addedDate) {
        this.id = id;
        this.keyWord = keyWord;
        this.description = description;
        this.pronunciation = pronunciation;
        this.addedDate = addedDate;
    }

    public Word(String keyWord, String description, String pronunciation, Date addedDate) {
        this.keyWord = keyWord;
        this.description = description;
        this.pronunciation = pronunciation;
        this.addedDate = addedDate;
    }

    public Word(String keyWord, String description, String pronunciation) {
        this.keyWord = keyWord;
        this.description = description;
        this.pronunciation = pronunciation;
        this.addedDate = null;
    }

    @Override
    public String toString() {
        return this.keyWord;
    }
}
