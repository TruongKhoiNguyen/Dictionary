package main.java;

import java.util.Date;

public record Word(String keyWord,
                   String description,
                   String pronunciation,
                   Date addedDate){}
