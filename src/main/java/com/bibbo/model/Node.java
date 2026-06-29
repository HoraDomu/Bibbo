package com.bibbo.model;

import javafx.scene.paint.Color;

public class Node {
    public long id;
    public String title;
    public String body;
    public Color color;
    public double x, y;
    public double vx, vy;
    public boolean dragging;
    public boolean dirty;

    public Node(long id, String title, String body, Color color, double x, double y) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.color = color;
        this.x = x;
        this.y = y;
    }
}
