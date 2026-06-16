import javax.swing.*;//imports para a interface bonitinha
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

import java.util.List;

public class GameScreen {

    //definiçoes globais
    private final GameEngine engine;
    private static final int NUM_DISKS = 7;

    //criando janela
    private final JFrame frame;

    //o label de timer e de crononometro do jogo
    private JLabel lblTimer;
    private JLabel lblMoves;
    private javax.swing.Timer swingTimer;
    private int elapsedSeconds = 0;

    // estado incial do jogo
    private int selectedPeg = -1; //-1 quando nenhuma torre foi selecionada
    private GamePanel gamePanel;  //painel que desenha as torres

    // estado inicial para a mecanica de arrastar o discos
    private int dragDisk    = -1;   //valor do disco sendo arrastado inicia com -1
    private int dragFromPeg = -1;   //torre de origem
    private int dragX       = 0;    //posição atual do mouse X
    private int dragY       = 0;    //posição atual do mouse Y

    // array das cores dos discos
 private static final Color[] DISK_COLORS = {
    new Color(229, 62,  62),  // disco 1 — vermelho
    new Color(56,  161, 105), // disco 2 — verde
    new Color(49,  130, 206), // disco 3 — azul
    new Color(214, 158, 46),  // disco 4 — amarelo
    new Color(128, 90,  213), // disco 5 — roxo
    new Color(237, 100, 166), // disco 6 — rosa
    new Color(72,  187, 120), // disco 7 — esmeralda
};

    //configuraçao das torres
    private static final int PANEL_W    = 900;
    private static final int PANEL_H    = 420;
    private static final int PEG_AREA_W = PANEL_W / 3;   // 300px por torre
    private static final int PEG_H      = 270;           // altura do mastro
    private static final int DISK_H     = 28;            // altura de cada disco
    private static final int MAX_DISK_W = (int)(PEG_AREA_W * 0.80);
    private static final int MIN_DISK_W = (int)(PEG_AREA_W * 0.25);

    public GameScreen(JFrame frame) {//construtor que instancia os discos
        this.frame = frame;
        this.engine = new GameEngine(NUM_DISKS);
    }

    //constroi a jenela principal
    public JPanel buildPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(26, 26, 46));
        root.add(buildTopBar(),    BorderLayout.NORTH);
        root.add(buildGamePanel(), BorderLayout.CENTER);
        root.add(buildBottomBar(), BorderLayout.SOUTH);

        return root;
    }

    //construçao da barra superior
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(22, 33, 62)); // #16213e
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(45, 55, 72)),
            new EmptyBorder(12, 20, 12, 20)
        ));

        // botoes do lado esquerdo
        JPanel leftSide = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        leftSide.setOpaque(false);

        JButton btnMenu = createControlButton(" Menu");
        btnMenu.addActionListener(e -> {
            stopTimer();
            MainMenuScreen menu = new MainMenuScreen();
            frame.setContentPane(buildMenuPanel(menu));
            frame.revalidate();
            frame.repaint();
        });

        lblTimer = new JLabel("00:00");
        lblTimer.setFont(new Font("Courier New", Font.BOLD, 20));
        lblTimer.setForeground(new Color(104, 211, 145)); 

        lblMoves = new JLabel("Movimentos: 0");
        lblMoves.setFont(new Font("Arial", Font.PLAIN, 15));
        lblMoves.setForeground(new Color(226, 232, 240));

        leftSide.add(btnMenu);
        leftSide.add(lblTimer);
        leftSide.add(lblMoves);

        //botoes do lado direito
        JPanel rightSide = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightSide.setOpaque(false);

        JButton btnUndo = createControlButton(" Desfazer");
        JButton btnRedo = createControlButton(" Refazer");

        btnUndo.addActionListener(e -> {
            engine.undoMove();
            selectedPeg = -1;
            updateHUD();
            gamePanel.repaint();
        });

        btnRedo.addActionListener(e -> {
            engine.redoMove();
            selectedPeg = -1;
            updateHUD();
            gamePanel.repaint();
        });

        rightSide.add(btnUndo);
        rightSide.add(btnRedo);

        bar.add(leftSide,  BorderLayout.WEST);
        bar.add(rightSide, BorderLayout.EAST);

        return bar;
    }

    //auxiliar para criar o painel do menu sem criar nova janela
    private JPanel buildMenuPanel(MainMenuScreen menu) {
        //usando o mesmo frame
        MainMenuScreen fresh = new MainMenuScreen();
        //chama show() interno só para montar o painel
        JPanel menuPanel = new JPanel(new BorderLayout()) {
            @Override//sobrescreve o método
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(26, 26, 46),
                    getWidth(), getHeight(), new Color(15, 52, 96)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        SwingUtilities.invokeLater(() -> {
            fresh.show();
            frame.dispose(); 
        });
        return menuPanel; 
    }

    //tela principal do jogo
    private GamePanel buildGamePanel() {
    gamePanel = new GamePanel();
    gamePanel.setPreferredSize(new Dimension(PANEL_W, PANEL_H));

    // mousePressed: pega o disco do topo da torre clicada
    gamePanel.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            int peg = e.getX() / (gamePanel.getWidth() / 3);
            if (peg < 0 || peg > 2) return;
            if (!engine.isPegEmpty(peg)) {
                dragFromPeg = peg;
                dragDisk    = engine.getPegDisksAsList(peg)
                                    .get(engine.getPegDisksAsList(peg).size() - 1); 
                dragX = e.getX();
                dragY = e.getY();
                //remove visualmente o disco do topo para arrastar
                engine.pegs[dragFromPeg].pop(); 
                gamePanel.repaint();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (dragDisk == -1) return;

            int toPeg = e.getX() / (gamePanel.getWidth() / 3);
            toPeg = Math.max(0, Math.min(2, toPeg));

            // Tenta soltar na torre de destino
            boolean valid = false;
            if (toPeg != dragFromPeg) {
                if (engine.isPegEmpty(toPeg) ||
                    engine.getPegDisksAsList(toPeg).get(
                        engine.getPegDisksAsList(toPeg).size()-1) > dragDisk) {
                    engine.pegs[toPeg].push(dragDisk);
                    engine.history.makeMove(dragFromPeg, toPeg, dragDisk);
                    engine.moveCounter++;
                    valid = true;
                }
            }

            if (!valid) {//se o movimento nn for completo
                //devolve o disco para a torre original
                engine.pegs[dragFromPeg].push(dragDisk);
            }

            dragDisk    = -1;
            dragFromPeg = -1;
            updateHUD();
            gamePanel.repaint();

            if (valid && engine.checkWin()) {
                stopTimer();
                showWinDialog();
            }
        }
    });

    //atualiza posição do disco enquanto arrasta
    gamePanel.addMouseMotionListener(new MouseMotionAdapter() {
        @Override
        public void mouseDragged(MouseEvent e) {
            if (dragDisk == -1) return;
            dragX = e.getX();
            dragY = e.getY();
            gamePanel.repaint();
        }
    });

    return gamePanel;
}

    //botões da parte inferior da tela
    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 12));
        bar.setBackground(new Color(22, 33, 62));
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(45, 55, 72)));

        JButton btnReset   = createActionButton("Resetar Jogo", new Color(116, 66, 16),  new Color(254, 252, 191));
        JButton btnNewGame = createActionButton("Novo Jogo",    new Color(26,  54, 93),   new Color(190, 227, 248));

        btnReset.addActionListener(e -> {
            engine.reset();
            selectedPeg = -1;
            elapsedSeconds = 0;
            updateHUD();
            gamePanel.repaint();
        });

        btnNewGame.addActionListener(e -> {
            engine.newGame();
            selectedPeg = -1;
            elapsedSeconds = 0;
            updateHUD();
            gamePanel.repaint();
        });

        bar.add(btnReset);
        bar.add(btnNewGame);
        return bar;
    }

    //herda do jpanel pois reaproveita boa parte do layout
    private class GamePanel extends JPanel {

        GamePanel() {
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int pegAreaW = w / 3;
            int baseY    = h - 40;

            //background
            g2.setColor(new Color(26, 26, 46));
            g2.fillRect(0, 0, w, h);

            //base das torres
            g2.setColor(new Color(74, 85, 104));
            g2.fillRoundRect(30, baseY, w - 60, 14, 8, 8);

            //torres
            for (int i = 0; i < 3; i++) {
                int centerX = pegAreaW * i + pegAreaW / 2;

                
                if (i == selectedPeg) {
                    g2.setColor(new Color(226, 201, 126, 25));
                    g2.fillRoundRect(pegAreaW * i + 8, 8, pegAreaW - 16, h - 56, 12, 12);
                }

                
                Color pegColor = (i == selectedPeg)
                    ? new Color(226, 201, 126)  // dourado se selecionado
                    : new Color(113, 128, 150); // cinza normal
                g2.setColor(pegColor);
                g2.fillRoundRect(centerX - 7, baseY - PEG_H, 14, PEG_H, 7, 7);

                //Discos
                List<Integer> disks = engine.getPegDisksAsList(i);
                for (int d = 0; d < disks.size(); d++) {
                    drawDisk(g2, centerX, disks.get(d), d, baseY, i == selectedPeg);
                }

                //nome das torres
                g2.setColor(new Color(160, 174, 192));
                g2.setFont(new Font("Georgia", Font.BOLD, 20));
                String[] labels = {"A", "B", "C"};
                g2.drawString(labels[i], centerX - 7, baseY + 34);
            }

            if (dragDisk != -1) {
            double t = (double)(dragDisk - 1) / Math.max(NUM_DISKS - 1, 1);
            int diskW = (int)(MIN_DISK_W + t * (MAX_DISK_W - MIN_DISK_W));
            int x = dragX - diskW / 2;
            int y = dragY - DISK_H / 2;

            Color base = DISK_COLORS[Math.min(dragDisk - 1, DISK_COLORS.length - 1)];
            GradientPaint gp = new GradientPaint(0, y, base.brighter(), 0, y + DISK_H, base);
            g2.setPaint(gp);
            g2.fillRoundRect(x, y, diskW, DISK_H - 2, 12, 12);
            g2.setColor(new Color(226, 201, 126));
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(x, y, diskW, DISK_H - 2, 12, 12);
        g2.setStroke(new BasicStroke(1f));

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString(String.valueOf(dragDisk), dragX - 4, y + DISK_H - 8);
}
        }

       
         //Desenha um disco individual
         // g2contexto gráfico
         // centerX centro horizontal da torre
         //diskValue valor do disco (define largura e cor)
         // stackIndex posição na pilha (0 = mais baixo)
         // baseY Y da base das torres
         // selected se a torre está selecionada
        
        private void drawDisk(Graphics2D g2, int centerX, int diskValue, int stackIndex, int baseY, boolean selected) {

            //larguras proporcionais
            double t = (double)(diskValue - 1) / Math.max(NUM_DISKS - 1, 1);
            int diskW = (int)(MIN_DISK_W + t * (MAX_DISK_W - MIN_DISK_W));

            int x = centerX - diskW / 2;
            int y = baseY - (stackIndex + 1) * DISK_H - 2;

            Color base = DISK_COLORS[Math.min(diskValue - 1, DISK_COLORS.length - 1)];

            //efeito de volume
            GradientPaint gp = new GradientPaint(
                0, y,            base.brighter(),
                0, y + DISK_H - 2, base
            );
            g2.setPaint(gp);
            g2.fillRoundRect(x, y, diskW, DISK_H - 2, 12, 12);

            //bordas
            g2.setColor(selected ? new Color(226, 201, 126) : base.darker());
            g2.setStroke(new BasicStroke(selected ? 2.5f : 1.5f));
            g2.drawRoundRect(x, y, diskW, DISK_H - 2, 12, 12);
            g2.setStroke(new BasicStroke(1f)); // reseta espessura

            //numeraçao dos discos
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString(String.valueOf(diskValue), centerX - 4, y + DISK_H - 8);
        }
    }

 
    //cronometro de tempo de jogo
    public void startTimer() {
        swingTimer = new javax.swing.Timer(1000, e -> {
            elapsedSeconds++;
            updateTimerLabel();
        });
        swingTimer.start();
    }

    private void stopTimer() {
        if (swingTimer != null) swingTimer.stop();
    }

    private void updateTimerLabel() {
        int min = elapsedSeconds / 60;
        int sec = elapsedSeconds % 60;
        lblTimer.setText(String.format(" %02d:%02d", min, sec));
    }

    private void updateHUD() {
        updateTimerLabel();
        lblMoves.setText("Movimentos: " + engine.getMoveCounter());
    }

    //mensagem de vitória
    private void showWinDialog() {
        String msg = String.format(
            "Torre montada com sucesso!\n\nMovimentos: %d\nTempo: %s",
            engine.getMoveCounter(),
            lblTimer.getText().replace("  ", "")
        );

        Object[] options = {"Novo Jogo", "Menu Principal"};
        int choice = JOptionPane.showOptionDialog(
            frame, msg, "Parabéns! Você venceu!",
            JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
            null, options, options[0]
        );

        if (choice == 0) { // instanciar novo jogo
            engine.newGame();
            selectedPeg = -1;
            elapsedSeconds = 0;
            updateHUD();
            gamePanel.repaint();
            startTimer();
        } else { // chama menu Principal
            stopTimer();
            MainMenuScreen menu = new MainMenuScreen();
            menu.show();
            frame.dispose();
        }
    }

    //configuraçoes dos botoes
    private JButton createControlButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover()
                    ? new Color(74, 85, 104)
                    : new Color(45, 55, 72);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setForeground(new Color(226, 232, 240));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(6, 14, 6, 14));
        return btn;
    }

    private JButton createActionButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color current = getModel().isRollover() ? bg.brighter() : bg;
                g2.setColor(current);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setForeground(fg);
        btn.setPreferredSize(new Dimension(190, 44));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}