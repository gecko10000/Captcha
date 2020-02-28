package me.kyllian.captcha.captchas;

import me.kyllian.captcha.CaptchaPlugin;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.ThreadLocalRandom;

public class TextCaptcha implements Captcha {

    private CaptchaPlugin plugin;
    private Player player;
    private BufferedImage image;
    private String answer;

    public TextCaptcha(CaptchaPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
        this.answer = RandomStringUtils.randomAlphabetic(5).toLowerCase();
        String[] split = answer.split("");

        Canvas canvas = new Canvas();
        canvas.setSize(128, 128);

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        canvas.paint(graphics);

        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, 128, 128);

        int lines = ThreadLocalRandom.current().nextInt(10, 25);
        while (lines != 0) {
            graphics.setColor(getRandomColor());
            lines--;
            graphics.drawLine(getRandomCoordinate(), getRandomCoordinate(), getRandomCoordinate(), getRandomCoordinate());
        }

        final String TYPE_TEXT = "Type the text you see";
        graphics.drawString(TYPE_TEXT, 128 / 2 - ((int) graphics.getFontMetrics().getStringBounds(TYPE_TEXT, graphics).getWidth()) / 2, 10);
        graphics.setFont(new Font("Arial", Font.PLAIN, 34));

        for (int i = 0; i != split.length; i++) {
            AffineTransform original = graphics.getTransform();
            int rotation = ThreadLocalRandom.current().nextInt(0, 45);
            graphics.rotate(Math.toRadians(rotation) * (ThreadLocalRandom.current().nextBoolean() ? 1 : -1), 10 + (20 * (i + 1)), 64);
            graphics.setColor(getRandomColor());
            graphics.drawString(split[i], (20 * (i + 1)), 70);
            graphics.setTransform(original);
        }
        graphics.dispose();

    }

    public int getRandomCoordinate() {
        return ThreadLocalRandom.current().nextInt(0, 128);
    }

    public Color getRandomColor() {
        return new Color(ThreadLocalRandom.current().nextInt(255), ThreadLocalRandom.current().nextInt(255), ThreadLocalRandom.current().nextInt(255));
    }

    @Override
    public CaptchaType getType() {
        return CaptchaType.TEXTCAPTCHA;
    }

    @Override
    public String getAnswer() {
        return this.answer;
    }

    @Override
    public void send() {
        plugin.getMapHandler().sendMap(player, image);
    }
}