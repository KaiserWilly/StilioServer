import fx.FrameGUI;

/**
 * Created by james on 1/27/2017.
 */
public class Test {
    public static void main(String[] args) {
        try {
            FrameGUI.run(args);
            FrameGUI.setScene("Create");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
