package CameraTest;

public class Vertex
{
    double x;
    double y;
    double z;
    double w;
    double u;
    double v;

    Vertex(double x, double y, double z, double w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    Vertex(double x, double y, double z, double w, double u, double v)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        this.u = u;
        this.v = v;
    }
    @Override
    public String toString()
    {
        return this.x + " " + this.y + " " + this.z;
    }
}