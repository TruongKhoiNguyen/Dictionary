package shared;

import javax.speech.Central;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VoiceSpeaker implements AutoCloseable {

    List<String> error = new ArrayList<>();
    Synthesizer synthesizer;

    public VoiceSpeaker() {
        try {
            // set properties directly instead of placing property file on home
            System.setProperty("freetts.voices",
                    "com.sun.speech.freetts.en.us" + ".cmu_us_kal.KevinVoiceDirectory");

            Central.registerEngineCentral("com.sun.speech.freetts"
                    + ".jsapi.FreeTTSEngineCentral");

            synthesizer = Central.createSynthesizer(new SynthesizerModeDesc(Locale.US));

            synthesizer.allocate();
            synthesizer.resume();

        } catch (Exception e) {
            error.add(e.getMessage());
        }
    }

    /**
     * This should be used before calling speak, if error size is 0 then continue, otherwise
     * stop calling other methods.
     */
    public List<String> getError() {
        return error;
    }

    /**
     * Remember to check if the object of this class has been properly initiated.
     * @param args words
     */
    public void speak(String args) {
        try {
            synthesizer.speakPlainText(args, null);
            synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);

        } catch (Exception e) {
            error.add(e.getMessage());
        }
    }

    /** Automatically deallocate synthesizer. */
    @Override
    public void close() {
        try {
            if (synthesizer != null) {
                synthesizer.deallocate();
            }
        } catch (Exception ignored) {}
    }

}
