import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.swing.SwingWorker;
import javax.swing.JTextArea;

public class BackgroundTask extends SwingWorker<Void, String> {

    private int _value;
    private JTextArea _txtOutput;

    public BackgroundTask(int value, JTextArea txtOutput) {
        _value = value;
        _txtOutput = txtOutput;
    }

    @Override
    public Void doInBackground() {
        // -- time intensive start
        try {
            String cmd = "for (( i = " + _value + " ; $i > 0; i=i-1)) ; do echo $i ; sleep 1; done";
            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);

            Process process = builder.start();

            InputStream out = process.getInputStream();
            BufferedReader stdout = new BufferedReader(new InputStreamReader(out));

            String line = null;
            while ((line = stdout.readLine()) != null ) {
                if (isCancelled()) {
                    process.destroy();
                    return null;
                }
                publish(line);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // -- time intensive end
        return null;
    }

    @Override
    public void process(List<String> chunks) {
        _txtOutput.append(chunks.get(0) + System.getProperty("line.separator"));
    }

    @Override
    public void done() {
        _txtOutput.append("Finished! ");
    }
}
