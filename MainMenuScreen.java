import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class MainMenuScreen {

    private JFrame frame;

    public MainMenuScreen() {
        frame = new JFrame("Torre de Hanói");//construtor da janela
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null); //centraliza na tela do monitor
    }

    public void show() {//chama tudo q foi grafucamente construído 
        frame.setContentPane(buildPanel());
        frame.setVisible(true);
    }

    //tela incial
    private JPanel buildPanel() {

        JPanel bg = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                    0, 0,           new Color(26, 26, 46),//gradiente de azuis, (escuro e um pouco mais calro)
                    getWidth(), getHeight(), new Color(15, 52, 96) 
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bg.setOpaque(true);

        bg.add(buildTitlePanel(), BorderLayout.NORTH);
        bg.add(buildButtonPanel(), BorderLayout.EAST);
        bg.add(buildFooter(),      BorderLayout.SOUTH);

        return bg;
    }

    //Label do título
    private JPanel buildTitlePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false); 
        panel.setBorder(new EmptyBorder(40, 0, 0, 0));

        JLabel title = new JLabel("Torre de Hanói");
        title.setFont(new Font("Georgia", Font.BOLD, 56));
        title.setForeground(new Color(226, 201, 126));//dourado

    

        //posicionamento do título na tela
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setOpaque(false);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
       
        box.add(title);
        panel.add(box);
        return panel;
    }

    //botoes do canto direito
    private JPanel buildButtonPanel() {
        //boxLayout Y_AXIS empilha os botoes verticalmente
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 0, 60)); // margem

        // Centraliza verticalmente
        panel.add(Box.createVerticalGlue());

        JButton btnIniciar   = createMenuButton("INICIAR",    new Color(226, 201, 126), new Color(26, 26, 46));
        JButton btnSobre     = createMenuButton("SOBRE",      new Color(74, 85, 104),   new Color(226, 232, 240));
        JButton btnComoJogar = createMenuButton("COMO JOGAR", new Color(74, 85, 104),   new Color(226, 232, 240));

        //ao apertar INICIAR
        btnIniciar.addActionListener(e -> {
            GameScreen game = new GameScreen(frame);
            frame.setContentPane(game.buildPanel());
            frame.revalidate(); //redesenhar a janela
            frame.repaint();
            game.startTimer();
        });

        //ao apertar SOBRE
        btnSobre.addActionListener(e -> openSobreDialog());

        //ao apertar COMO JOGAR
        btnComoJogar.addActionListener(e -> openComoJogarDialog());

        panel.add(btnIniciar);
        panel.add(Box.createVerticalStrut(16));
        panel.add(btnSobre);
        panel.add(Box.createVerticalStrut(16));
        panel.add(btnComoJogar);

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    // rodapé, bonitinho, mas opcional
    private JPanel buildFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 14, 0));

        JLabel footer = new JLabel("UFS · DCOMP · AED 1");
        footer.setFont(new Font("Arial", Font.PLAIN, 11));
        footer.setForeground(new Color(113, 128, 150));
        panel.add(footer);
        return panel;
    }

    //cria layout para os botoes
    private JButton createMenuButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                //cr muda quando mouse passa e cima
                Color currentBg = getModel().isRollover()
                    ? bg.brighter()
                    : bg;

                g2.setColor(currentBg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));

            
                if (getModel().isRollover()) {
                    g2.setColor(new Color(226, 201, 126));
                    g2.setStroke(new BasicStroke(2f));
                    g2.draw(new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()-2, 14, 14));
                }

                g2.dispose();
                super.paintComponent(g); 
            }
        };

        btn.setFont(new Font("Arial", Font.BOLD, 15));
        btn.setForeground(fg);
        btn.setPreferredSize(new Dimension(220, 52));
        btn.setMaximumSize(new Dimension(220, 52));
        btn.setAlignmentX(Component.RIGHT_ALIGNMENT);

        // remove a aparência padrão do sistema
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return btn;
    }

    //janela SOBRE
    private void openSobreDialog() {
    JDialog dialog = new JDialog(frame, "Sobre", true);
    dialog.setSize(520, 420);
    dialog.setLocationRelativeTo(frame);
    dialog.setResizable(false);

    JPanel panel = new JPanel(new BorderLayout(0, 16));
    panel.setBackground(new Color(26, 26, 46));
    panel.setBorder(new EmptyBorder(30, 30, 30, 30));

    JLabel titulo = new JLabel("Sobre o Jogo", SwingConstants.CENTER);
    titulo.setFont(new Font("Georgia", Font.BOLD, 22));
    titulo.setForeground(new Color(226, 201, 126));

    JTextArea info = new JTextArea(
        "Torre de Hanói \n\n" +
        "Jogo desenvolvido por:\n" +
        "  • Ana Júlia Fontes\n" +
        "  • Décio Caldas\n" +
        "  • Luiza Accioly\n\n" +
        "Para componente avaliativo da disciplina de Algoritmos e Estruturas de Dados 1, ministrada pela Profa. Maily Faro\n" +
        "\nUniversidade Federal de Sergipe — DCOMP\n" +
        "2026.1"
    );
    info.setFont(new Font("Arial", Font.PLAIN, 13));
    info.setForeground(new Color(226, 232, 240));
    info.setBackground(new Color(26, 26, 46));
    info.setEditable(false);
    info.setFocusable(false);
    info.setLineWrap(true);
    info.setWrapStyleWord(true);

    JButton fechar = createMenuButton("Fechar", new Color(74, 85, 104), new Color(226, 232, 240));
    fechar.addActionListener(e -> dialog.dispose());

    JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    btnPanel.setOpaque(false);
    btnPanel.add(fechar);

    panel.add(titulo,  BorderLayout.NORTH);
    panel.add(info,    BorderLayout.CENTER);
    panel.add(btnPanel, BorderLayout.SOUTH);

    dialog.setContentPane(panel);
    dialog.setVisible(true);
}

    //janela COMO JOGAR 
    private void openComoJogarDialog() {
        JDialog dialog = new JDialog(frame, "Como Jogar", true);
        dialog.setSize(480, 460);
        dialog.setLocationRelativeTo(frame);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(new Color(26, 26, 46));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel titulo = new JLabel("Como Jogar", SwingConstants.CENTER);
        titulo.setFont(new Font("Georgia", Font.BOLD, 22));
        titulo.setForeground(new Color(226, 201, 126));

        JTextArea regras = new JTextArea(
            "OBJETIVO\n" +
            "Mova todos os discos da Torre A para qualquer outra torre,\n" +
            "mantendo-os em ordem crescente.\n\n" +
            "REGRAS\n" +
            "1. Apenas um disco pode ser movido por vez.\n" +
            "2. Só o disco do TOPO de uma torre pode ser movido.\n" +
            "3. Um disco MAIOR nunca pode ser colocado sobre um MENOR.\n\n" +
            "CONTROLES\n" +
            "• Clique e segure no disco que deseja mover na torre de ORIGEM\n" +
            "• Arraste-o até a torre de DESTINO.\n" +
            "• Desfazer: cancela o último movimento.\n" +
            "• Refazer: reaplica um movimento desfeito.\n" +
            "• Resetar: volta ao estado inicial desta partida.\n" +
            "• Novo Jogo: inicia uma partida completamente nova.\n\n" +
            "PLACAR\n" +
            "O cronômetro e o contador de movimentos ficam no topo."
        );
        regras.setFont(new Font("Arial", Font.PLAIN, 13));
        regras.setForeground(new Color(226, 232, 240));
        regras.setBackground(new Color(26, 26, 46));
        regras.setEditable(false);
        regras.setFocusable(false);
        regras.setLineWrap(true);
        regras.setWrapStyleWord(true);

    
        JScrollPane scroll = new JScrollPane(regras);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        JButton fechar = createMenuButton("Fechar", new Color(74, 85, 104), new Color(226, 232, 240));
        fechar.addActionListener(e -> dialog.dispose());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setOpaque(false);
        btnPanel.add(fechar);

        panel.add(titulo,   BorderLayout.NORTH);
        panel.add(scroll,   BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }
}