package com.bibbo.util;

import com.bibbo.model.Node;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class Utils {

    private static final DateTimeFormatter DATE_FMT =
        DateTimeFormatter.ofPattern("MMMM d, yyyy");

    private Utils() {}

    public static String dateString() {
        return LocalDate.now().format(DATE_FMT);
    }

    public static List<String> parseLinks(String body) {
        List<String> links = new ArrayList<>();
        int start = 0;
        while (true) {
            int open = body.indexOf("[[", start);
            if (open == -1) break;
            int close = body.indexOf("]]", open + 2);
            if (close == -1) break;
            String tag = body.substring(open + 2, close).trim();
            if (!tag.isEmpty()) links.add(tag);
            start = close + 2;
        }
        return links;
    }

    public static String normalize(String s) {
        return String.join(" ", s.trim().split("\\s+")).toLowerCase();
    }

    public static double pointSegmentDist(double px, double py,
                                           double ax, double ay,
                                           double bx, double by) {
        double abx = bx - ax, aby = by - ay;
        double lenSq = abx * abx + aby * aby;
        if (lenSq < 0.001) {
            double dx = px - ax, dy = py - ay;
            return Math.sqrt(dx * dx + dy * dy);
        }
        double t = Math.max(0, Math.min(1,
            ((px - ax) * abx + (py - ay) * aby) / lenSq));
        double dx = px - (ax + abx * t), dy = py - (ay + aby * t);
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static double nodeRadius(int conns) {
        return 5.0 + (1.0 - Math.exp(-conns / 6.0)) * 6.0;
    }

    public static double edgeRestLen(long a, long b) {
        long lo = Math.min(a, b);
        long hi = Math.max(a, b);
        // Java long overflow wraps identically to Rust wrapping_mul/wrapping_add
        long h = lo * 6364136223846793005L + hi;
        double t = (h & 0xFFL) / 255.0;
        return 145.0 * (0.80 + t * 0.40);
    }

    public static double[] spawnPos(int n, double cx, double cy) {
        long s = (long)(n + 1);
        long ax = s * 2654435761L + 1013904223L;
        long ay = ax * 2654435761L + 1013904223L;
        double rx = (ax & 0xFFL) / 255.0 - 0.5;
        double ry = (ay & 0xFFL) / 255.0 - 0.5;
        return new double[]{ cx + rx * 220.0, cy + ry * 220.0 };
    }

    /** Returns [title, body]. */
    public static String[] parseMdFile(String content, String filename) {
        String[] lines = content.split("\n", -1);
        String title = null;
        int titleLine = -1;
        for (int i = 0; i < lines.length; i++) {
            String t = lines[i].trim();
            if (t.startsWith("# ")) {
                title = t.substring(2).trim();
                titleLine = i;
                break;
            } else if (!t.isEmpty()) {
                // first non-empty line not a heading — use filename stem
                title = filename.replaceFirst("\\.[^.]+$", "");
                break;
            }
        }
        if (title == null) title = "";
        String body;
        if (titleLine >= 0) {
            StringBuilder sb = new StringBuilder();
            boolean seenContent = false;
            for (int i = titleLine + 1; i < lines.length; i++) {
                if (!seenContent && lines[i].trim().isEmpty()) continue;
                seenContent = true;
                if (sb.length() > 0) sb.append('\n');
                sb.append(lines[i]);
            }
            body = sb.toString().stripTrailing();
        } else {
            body = content.trim();
        }
        return new String[]{ title, body };
    }

    public static Path dataDir() {
        String os = System.getProperty("os.name", "").toLowerCase();
        Path base;
        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            base = appData != null
                ? Path.of(appData)
                : Path.of(System.getProperty("user.home"), "AppData", "Roaming");
        } else if (os.contains("mac")) {
            base = Path.of(System.getProperty("user.home"),
                           "Library", "Application Support");
        } else {
            String xdg = System.getenv("XDG_DATA_HOME");
            base = xdg != null
                ? Path.of(xdg)
                : Path.of(System.getProperty("user.home"), ".local", "share");
        }
        return base.resolve("Bibbo");
    }

    public static List<String> connectionReasons(Node a, Node b) {
        List<String> aTags = parseLinks(a.body);
        List<String> bTags = parseLinks(b.body);
        String aTitle = normalize(a.title);
        String bTitle = normalize(b.title);
        List<String> reasons = new ArrayList<>();

        for (String tag : aTags) {
            String tn = normalize(tag);
            if (tn.equals(bTitle) || bTags.stream().anyMatch(bt -> normalize(bt).equals(tn))) {
                String s = "[[" + tag + "]]";
                if (!reasons.contains(s)) reasons.add(s);
            }
        }
        for (String tag : bTags) {
            String tn = normalize(tag);
            if (tn.equals(aTitle) || aTags.stream().anyMatch(at -> normalize(at).equals(tn))) {
                String s = "[[" + tag + "]]";
                if (!reasons.contains(s)) reasons.add(s);
            }
        }
        return reasons;
    }
}
