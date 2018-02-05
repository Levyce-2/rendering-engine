package BasicShading;

public class Vertex
{
    double x;
    double y;
    double z;
    double w;

    Vertex(double x, double y, double z, double w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    @Override
    public String toString()
    {
        return this.x + " " + this.y + " " + this.z;
    }
}