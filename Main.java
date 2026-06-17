import javax.swing.SwingUtilities;
// Desenvolvido por Ana Julia, Decio Caldas e Luiza Accioly
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenuScreen menu = new MainMenuScreen();
            menu.show();
        });
    }
}
