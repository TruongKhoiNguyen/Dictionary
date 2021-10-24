package cli;

import shared.DictionaryManager;
import shared.VoiceSpeaker;
import shared.Word;

import java.util.List;

public class DictionaryCLIApp {
    public static void main(String[] args) {
        final var dictionaryCommandLine = new DictionaryCommandLine();
        dictionaryCommandLine.run();
    }
}
