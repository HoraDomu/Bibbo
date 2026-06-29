package com.bibbo;

import com.bibbo.util.Utils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

/**
 * Creates a stress-test database with 100k nodes at bibbo_stress.db.
 * Does NOT touch your real bibbo.db.
 *
 * Run: ./gradlew.bat seedTest
 *
 * To test: rename bibbo.db → bibbo_real.db, rename bibbo_stress.db → bibbo.db, launch Bibbo.
 * To restore: reverse the renames.
 */
public class SeedTool {

    private static final int TOTAL_NODES   = 100_000;
    private static final int CLUSTER_A_END = 50_000;

    public static void main(String[] args) throws Exception {
        Path dir = Utils.dataDir();
        Files.createDirectories(dir);
        Path dbPath = dir.resolve("bibbo_stress.db");

        System.out.println("Creating stress database at: " + dbPath);
        System.out.println("Inserting " + TOTAL_NODES + " nodes...");

        long t0 = System.currentTimeMillis();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath.toAbsolutePath())) {
            try (Statement s = conn.createStatement()) {
                s.execute("PRAGMA journal_mode=WAL");
                s.execute("PRAGMA synchronous=NORMAL");
                s.execute("""
                    CREATE TABLE IF NOT EXISTS nodes (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        title TEXT NOT NULL, body TEXT NOT NULL,
                        color_idx INTEGER NOT NULL,
                        pos_x REAL NOT NULL, pos_y REAL NOT NULL,
                        created TEXT NOT NULL
                    )""");
                s.execute("""
                    CREATE TABLE IF NOT EXISTS edges (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        source_id INTEGER NOT NULL,
                        target_id INTEGER NOT NULL,
                        UNIQUE(source_id, target_id)
                    )""");
                s.execute("DELETE FROM nodes");
                s.execute("DELETE FROM edges");
            }

            conn.setAutoCommit(false);

            // Insert nodes in one transaction
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO nodes (title, body, color_idx, pos_x, pos_y, created) VALUES (?,?,?,?,?,?)")) {
                for (int i = 1; i <= TOTAL_NODES; i++) {
                    int cluster = i <= CLUSTER_A_END ? 1 : 2;
                    double baseX = cluster == 1 ? -5000.0 : 5000.0;
                    double angle = (i % 1000) / 1000.0 * Math.PI * 2;
                    double rad   = (i / 1000) * 200.0 + 300.0;
                    ps.setString(1, "Node " + i);
                    ps.setString(2, "Content for node " + i + ". Cluster " + cluster + ".");
                    ps.setInt(3, i % 8);
                    ps.setDouble(4, baseX + Math.cos(angle) * rad);
                    ps.setDouble(5, Math.sin(angle) * rad);
                    ps.setString(6, "January 1, 2026");
                    ps.addBatch();
                    if (i % 5000 == 0) {
                        ps.executeBatch();
                        System.out.println("  nodes: " + i + "/" + TOTAL_NODES);
                    }
                }
                ps.executeBatch();
            }
            conn.commit();

            System.out.println("Nodes done in " + (System.currentTimeMillis() - t0) + "ms");
            System.out.println("Inserting edges...");

            conn.setAutoCommit(false);
            long t1 = System.currentTimeMillis();

            // Cluster A: chain 1→2→3→...→50000
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT OR IGNORE INTO edges (source_id, target_id) VALUES (?,?)")) {
                for (int i = 1; i < CLUSTER_A_END; i++) {
                    ps.setLong(1, i);
                    ps.setLong(2, i + 1);
                    ps.addBatch();
                    if (i % 5000 == 0) ps.executeBatch();
                }
                ps.executeBatch();
            }
            conn.commit();

            conn.setAutoCommit(false);

            // Cluster B: chain 50001→50002→...→100000
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT OR IGNORE INTO edges (source_id, target_id) VALUES (?,?)")) {
                for (int i = CLUSTER_A_END + 1; i < TOTAL_NODES; i++) {
                    ps.setLong(1, i);
                    ps.setLong(2, i + 1);
                    ps.addBatch();
                    if (i % 5000 == 0) ps.executeBatch();
                }
                ps.executeBatch();
            }
            conn.commit();
        }

        long elapsed = System.currentTimeMillis() - t0;
        System.out.println("Done in " + elapsed + "ms");
        System.out.println();
        System.out.println("Test database: " + dir.resolve("bibbo_stress.db"));
        System.out.println();
        System.out.println("To stress test:");
        System.out.println("  1. Rename bibbo.db → bibbo_real.db");
        System.out.println("  2. Rename bibbo_stress.db → bibbo.db");
        System.out.println("  3. Launch Bibbo — scroll/zoom around 100k nodes");
        System.out.println("  4. Rename back when done");
    }
}
