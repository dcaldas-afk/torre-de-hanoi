import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenuScreen menu = new MainMenuScreen();
            menu.show();
        });
    }
}