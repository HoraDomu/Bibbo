package com.bibbo.util;

import com.bibbo.model.Node;
import java.util.List;

public final class QuadTree {

    private static final double THETA = 0.8;

    private final double x0, y0, x1, y1;
    private double cx, cy, mass;
    private Node body;
    private QuadTree nw, ne, sw, se;

    private QuadTree(double x0, double y0, double x1, double y1) {
        this.x0 = x0; this.y0 = y0;
        this.x1 = x1; this.y1 = y1;
    }

    public static QuadTree build(List<Node> nodes) {
        if (nodes.isEmpty()) return null;
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        for (Node n : nodes) {
            if (n.x < minX) minX = n.x;
            if (n.y < minY) minY = n.y;
            if (n.x > maxX) maxX = n.x;
            if (n.y > maxY) maxY = n.y;
        }
        QuadTree root = new QuadTree(minX - 100, minY - 100, maxX + 100, maxY + 100);
        for (Node n : nodes) root.insert(n);
        return root;
    }

    private void insert(Node n) {
        if (mass == 0) {
            body = n;
            cx = n.x; cy = n.y; mass = 1;
            return;
        }
        cx = (cx * mass + n.x) / (mass + 1);
        cy = (cy * mass + n.y) / (mass + 1);
        mass++;
        if (body != null) {
            Node old = body; body = null;
            subdivide();
            childFor(old).insert(old);
        }
        childFor(n).insert(n);
    }

    private void subdivide() {
        double mx = (x0 + x1) * 0.5, my = (y0 + y1) * 0.5;
        nw = new QuadTree(x0, y0, mx, my);
        ne = new QuadTree(mx, y0, x1, my);
        sw = new QuadTree(x0, my, mx, y1);
        se = new QuadTree(mx, my, x1, y1);
    }

    private QuadTree childFor(Node n) {
        double mx = (x0 + x1) * 0.5, my = (y0 + y1) * 0.5;
        if (n.x < mx) return n.y < my ? nw : sw;
        else           return n.y < my ? ne : se;
    }

    public void force(Node n, double repulsion, double minDist,
                      double[] fx, double[] fy, int idx) {
        if (mass == 0) return;
        double dx = n.x - cx, dy = n.y - cy;
        double distSq = dx * dx + dy * dy;
        if (distSq < 0.01) return;
        double width = x1 - x0;
        // Barnes-Hut criterion: treat cluster as point if far enough, or if leaf
        if (body != null || width * width < THETA * THETA * distSq) {
            if (body == n) return; // skip self
            double dist = Math.sqrt(distSq);
            if (dist < minDist) {
                double t = 1.0 - dist / minDist;
                double f = repulsion * t * t;
                double ux = dx / dist, uy = dy / dist;
                fx[idx] += ux * f;
                fy[idx] += uy * f;
            }
            return;
        }
        if (nw != null) nw.force(n, repulsion, minDist, fx, fy, idx);
        if (ne != null) ne.force(n, repulsion, minDist, fx, fy, idx);
        if (sw != null) sw.force(n, repulsion, minDist, fx, fy, idx);
        if (se != null) se.force(n, repulsion, minDist, fx, fy, idx);
    }
}
