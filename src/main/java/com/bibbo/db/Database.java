package com.bibbo.db;

import com.bibbo.model.Edge;
import com.bibbo.model.Node;
import javafx.scene.paint.Color;

import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private static final Color[] COLORS = {
        Color.rgb(255, 107, 107), Color.rgb(255, 159, 67),
        Color.rgb(255, 206, 84),  Color.rgb(46,  213, 115),
        Color.rgb(30,  144, 255), Color.rgb(147, 51,  234),
        Color.rgb(236, 72,  153), Color.rgb(20,  184, 166),
    };

    private final Connection conn;

    public Database(Path dbPath) throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath.toAbsolutePath());
        conn.setAutoCommit(true);
        init();
    }

    private void init() throws SQLException {
        try (Statement s = conn.createStatement()) {
            s.execute("""
                CREATE TABLE IF NOT EXISTS nodes (
                    id        INTEGER PRIMARY KEY AUTOINCREMENT,
                    title     TEXT    NOT NULL,
                    body      TEXT    NOT NULL,
                    color_idx INTEGER NOT NULL,
                    pos_x     REAL    NOT NULL,
                    pos_y     REAL    NOT NULL,
                    created   TEXT    NOT NULL
                )
                """);
            s.execute("""
                CREATE TABLE IF NOT EXISTS edges (
                    id        INTEGER PRIMARY KEY AUTOINCREMENT,
                    source_id INTEGER NOT NULL,
                    target_id INTEGER NOT NULL,
                    UNIQUE(source_id, target_id)
                )
                """);
        }
    }

    public List<Node> loadNodes() throws SQLException {
        List<Node> list = new ArrayList<>();
        try (Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery(
                 "SELECT id, title, body, color_idx, pos_x, pos_y FROM nodes ORDER BY id")) {
            while (rs.next()) {
                long id   = rs.getLong(1);
                String title = rs.getString(2);
                String body  = rs.getString(3);
                int ci   = rs.getInt(4) % COLORS.length;
                double x = rs.getDouble(5);
                double y = rs.getDouble(6);
                list.add(new Node(id, title, body, COLORS[ci], x, y));
            }
        }
        return list;
    }

    public List<Edge> loadEdges() throws SQLException {
        List<Edge> list = new ArrayList<>();
        try (Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery("SELECT source_id, target_id FROM edges")) {
            while (rs.next()) {
                list.add(new Edge(rs.getLong(1), rs.getLong(2)));
            }
        }
        return list;
    }

    public long insertNode(String title, String body, int colorIdx,
                           double x, double y, String created) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO nodes (title, body, color_idx, pos_x, pos_y, created) VALUES (?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, title);
            ps.setString(2, body);
            ps.setInt(3, colorIdx);
            ps.setDouble(4, x);
            ps.setDouble(5, y);
            ps.setString(6, created);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.getLong(1);
            }
        }
    }

    public void updateNode(long id, String title, String body) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE nodes SET title=?, body=? WHERE id=?")) {
            ps.setString(1, title);
            ps.setString(2, body);
            ps.setLong(3, id);
            ps.executeUpdate();
        }
    }

    public void updatePosition(long id, double x, double y) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE nodes SET pos_x=?, pos_y=? WHERE id=?")) {
            ps.setDouble(1, x);
            ps.setDouble(2, y);
            ps.setLong(3, id);
            ps.executeUpdate();
        }
    }

    public void deleteNode(long id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM nodes WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM edges WHERE source_id=? OR target_id=?")) {
            ps.setLong(1, id);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    public void deleteEdgesFrom(long sourceId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM edges WHERE source_id=?")) {
            ps.setLong(1, sourceId);
            ps.executeUpdate();
        }
    }

    public void insertEdge(long sourceId, long targetId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT OR IGNORE INTO edges (source_id, target_id) VALUES (?,?)")) {
            ps.setLong(1, sourceId);
            ps.setLong(2, targetId);
            ps.executeUpdate();
        }
    }

    public void deleteAllNodes() throws SQLException {
        try (Statement s = conn.createStatement()) {
            s.execute("DELETE FROM nodes");
        }
    }

    public void deleteAllEdges() throws SQLException {
        try (Statement s = conn.createStatement()) {
            s.execute("DELETE FROM edges");
        }
    }

    public void close() {
        try { conn.close(); } catch (SQLException ignored) {}
    }
}
