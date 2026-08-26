package dev.rodrigosambade.race;

import java.awt.GridLayout;
import java.time.Duration;
import java.util.random.RandomGenerator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public final class RaceApp {

    private static final int FINISH = 100;
    private static final Duration TICK = Duration.ofMillis(100);

    private RaceApp() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RaceApp::show);
    }

    private static void show() {
        JFrame frame = new JFrame("Liebre y tortuga");
        JProgressBar hareProgress = createProgressBar();
        JProgressBar tortoiseProgress = createProgressBar();
        JLabel status = new JLabel("Preparados…", SwingConstants.CENTER);
        JButton startButton = new JButton("Iniciar carrera");

        frame.setContentPane(createContent(
                hareProgress,
                tortoiseProgress,
                status,
                startButton));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(520, 260);
        frame.setLocationRelativeTo(null);

        startButton.addActionListener(event -> startRace(
                startButton,
                hareProgress,
                tortoiseProgress,
                status));

        frame.setVisible(true);
    }

    private static JProgressBar createProgressBar() {
        JProgressBar progressBar = new JProgressBar(0, FINISH);
        progressBar.setStringPainted(true);
        return progressBar;
    }

    private static JPanel createContent(
            JProgressBar hareProgress,
            JProgressBar tortoiseProgress,
            JLabel status,
            JButton startButton) {
        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        panel.add(new JLabel("Liebre"));
        panel.add(hareProgress);
        panel.add(new JLabel("Tortuga"));
        panel.add(tortoiseProgress);
        panel.add(status);
        panel.add(startButton);
        return panel;
    }

    private static void startRace(
            JButton startButton,
            JProgressBar hareProgress,
            JProgressBar tortoiseProgress,
            JLabel status) {
        startButton.setEnabled(false);
        status.setText("Corriendo…");

        RaceEngine engine = new RaceEngine(
                FINISH,
                TICK,
                RandomGenerator.getDefault());

        engine.start(snapshot -> SwingUtilities.invokeLater(() -> {
            hareProgress.setValue(snapshot.hare());
            tortoiseProgress.setValue(snapshot.tortoise());

            if (snapshot.winner() != null) {
                status.setText("Ganador: " + snapshot.winner());
            }
        })).whenComplete((winner, error) -> {
            engine.close();
            SwingUtilities.invokeLater(() -> {
                startButton.setEnabled(true);
                if (error != null) {
                    status.setText("La carrera terminó con un error");
                }
            });
        });
    }
}
