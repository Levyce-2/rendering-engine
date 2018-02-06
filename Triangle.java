package CameraTest;

import java.awt.*;

public class Triangle
{
    public Vertex v1;
    public Vertex v2;
    public Vertex v3;

    Triangle(Vertex v1, Vertex v2, Vertex v3)
    {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    @Override
    public String toString()
    {
        return this.v1 + " " + this.v2 + " " + this.v3;
    }
}