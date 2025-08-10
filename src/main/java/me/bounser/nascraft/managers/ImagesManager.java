package me.bounser.nascraft.managers;

import me.bounser.nascraft.Nascraft;
import me.bounser.nascraft.config.Config;
import org.bukkit.configuration.file.FileConfiguration;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ImagesManager {

    private static ImagesManager instance;

    public static ImagesManager getInstance() { return instance == null ? instance = new ImagesManager() : instance; }


    public BufferedImage getImage(String identifier) {

        FileConfiguration items = Config.getInstance().getItemsFileConfiguration();

        BufferedImage image = null;
        String imageName;
        String imagePath = Nascraft.getInstance().getDataFolder().getPath() + "/images/" + identifier + ".png";

        try (InputStream input = Files.newInputStream(new File(imagePath).toPath())) {

            image = ImageIO.read(input);

        } catch (IOException ignored) {
            // No image specified.
        } catch (IllegalArgumentException e) {
            Nascraft.getInstance().getLogger().info("Invalid argument for image: " + identifier);
        }

        if (image != null) return image;

        if (items.contains("items." + identifier + ".item-stack.type")) {

            imageName = items.getString("items." + identifier + ".item-stack.type").toLowerCase() + ".png";
            imagePath = "1-21-4-materials/minecraft_" + imageName;

            try (InputStream input = Nascraft.getInstance().getResource(imagePath)) {
                if (input != null) {
                    image = ImageIO.read(input);
                }
            } catch (IOException ignored) {
            } catch (IllegalArgumentException ignored) {
            }

            return image != null ? image : generatePlaceholder(identifier);
        }

        imageName = identifier.replaceAll("\\d", "").toLowerCase() + ".png";
        imagePath = "1-21-4-materials/minecraft_" + imageName;

        try (InputStream input = Nascraft.getInstance().getResource(imagePath)) {
            if (input != null) {
                image = ImageIO.read(input);
            }
        } catch (IOException ignored) {
        } catch (IllegalArgumentException ignored) {
        }

        return image != null ? image : generatePlaceholder(identifier);
    }

    public static byte[] getBytesOfImage(BufferedImage image) {
        ByteArrayOutputStream baosBalance = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baosBalance);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return baosBalance.toByteArray();
    }

    private BufferedImage generatePlaceholder(String identifier) {
        int size = 32;
        BufferedImage placeholder = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = placeholder.createGraphics();
        try {
            int hash = Math.abs(identifier.hashCode());
            Color bg = new Color((hash >> 16) & 0x7F, (hash >> 8) & 0x7F, hash & 0x7F, 255);
            g2d.setColor(bg);
            g2d.fillRect(0, 0, size, size);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
            String letter = identifier.isEmpty() ? "?" : String.valueOf(Character.toUpperCase(identifier.charAt(0)));
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(letter);
            int textAscent = fm.getAscent();
            g2d.drawString(letter, (size - textWidth) / 2, (size + textAscent) / 2 - 4);
        } finally {
            g2d.dispose();
        }
        return placeholder;
    }

}
