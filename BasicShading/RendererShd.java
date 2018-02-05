package BasicShading;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class RendererShd
{
    public static void main(String[] args)
    {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Container pane = frame.getContentPane();
        pane.setLayout(new BorderLayout());

        JButton changeView = new JButton("view");
        changeView.setSize(23, 23);
        pane.add(changeView);

        JSlider horizontalSlider = new JSlider(-180, 180, -90);
        pane.add(horizontalSlider, BorderLayout.SOUTH);

        JSlider verticalSlider = new JSlider(SwingConstants.VERTICAL, -180, 180, 0);
        pane.add(verticalSlider, BorderLayout.EAST);

        JSlider rollSlider = new JSlider(SwingConstants.VERTICAL, -180, 180, -90);
        pane.add(rollSlider, BorderLayout.WEST);

        JSlider FoVSlider = new JSlider(0, 200, 100);
        pane.add(FoVSlider, BorderLayout.NORTH);

        ArrayList<Triangle> tris = CSVReader.Seperate();

        JPanel renderPanel = new JPanel()
        {
            public void paintComponent(Graphics g)
            {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.GRAY);
                g2.fillRect(0, 0, getWidth(), getHeight());


                // transformation matrices

                double axisX = Math.toRadians(horizontalSlider.getValue());
                Matrix4 rotationX = new Matrix4(new double[] {
                        Math.cos(axisX), 0, -Math.sin(axisX), 0,
                        0, 1, 0, 0,
                        Math.sin(axisX), 0, Math.cos(axisX), 0,
                        0, 0, 0, 1
                });

                double axisY = Math.toRadians(verticalSlider.getValue());
                Matrix4 rotationY = new Matrix4(new double[] {
                        1, 0, 0, 0,
                        0, Math.cos(axisY), Math.sin(axisY), 0,
                        0, -Math.sin(axisY), Math.cos(axisY), 0,
                        0, 0, 0, 1
                });

                double axisZ = Math.toRadians(rollSlider.getValue());
                Matrix4 rotationZ = new Matrix4(new double[] {
                        Math.cos(axisZ), -Math.sin(axisZ), 0, 0,
                        Math.sin(axisZ), Math.cos(axisZ), 0, 0,
                        0, 0, 1, 0,
                        0, 0, 0, 1
                });

                Matrix4 finalize = new Matrix4(new double[] {
                        1, 0, 0, 0,
                        0, 1, 0, 0,
                        0, 0, 1, 0,
                        0, 0, -400, 1
                });

                double viewportWidth = getWidth();
                double viewportHeight = getHeight();
                double fovAngle = Math.toRadians(177 + (FoVSlider.getValue() * 0.01f));
                double fov = Math.tan(fovAngle / 2) * 170;

                Matrix4 transform = rotationX
                        .multiply(rotationY)
                        .multiply(rotationZ)
                        .multiply(finalize)
                        ;

                BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);

                // z-Buffer initialization

                double[] zBuffer = new double[img.getWidth() * img.getHeight()];

                for (int q = 0; q < zBuffer.length; q++) {
                    zBuffer[q] = Double.NEGATIVE_INFINITY;
                }

                for (Triangle tr :tris)
                {
                    Vertex v1 = transform.transform(tr.v1);
                    Vertex v2 = transform.transform(tr.v2);
                    Vertex v3 = transform.transform(tr.v3);

                    Vertex ab = new Vertex(v2.x - v1.x, v2.y - v1.y, v2.z - v1.z, v2.w - v1.w);
                    Vertex ac = new Vertex(v3.x - v1.x, v3.y - v1.y, v3.z - v1.z, v3.w - v1.w);
                    Vertex norm = new Vertex(
                            ab.y * ac.z - ab.z * ac.y,
                            ab.z * ac.x - ab.x * ac.z,
                            ab.x * ac.y - ab.y * ac.x,
                            1
                    );

                    double normalLength = Math.sqrt(norm.x * norm.x + norm.y * norm.y + norm.z * norm.z);
                    norm.x /= normalLength;
                    norm.y /= normalLength;
                    norm.z /= normalLength;

                    double angleCos = Math.abs(norm.z);

                    v1.x = v1.x / (-v1.z) * fov * 2;
                    v1.y = v1.y / (-v1.z) * fov * 2;
                    v2.x = v2.x / (-v2.z) * fov * 2;
                    v2.y = v2.y / (-v2.z) * fov * 2;
                    v3.x = v3.x / (-v3.z) * fov * 2;
                    v3.y = v3.y / (-v3.z) * fov * 2;

                    v1.x += viewportWidth / 2;
                    v1.y += viewportHeight / 2;
                    v2.x += viewportWidth / 2;
                    v2.y += viewportHeight / 2;
                    v3.x += viewportWidth / 2;
                    v3.y += viewportHeight / 2;

                    int minX = (int) Math.max(0, Math.ceil(Math.min(v1.x, Math.min(v2.x, v3.x))));
                    int maxX = (int) Math.min(img.getWidth() - 1, Math.floor(Math.max(v1.x, Math.max(v2.x, v3.x))));
                    int minY = (int) Math.max(0, Math.ceil(Math.min(v1.y, Math.min(v2.y, v3.y))));
                    int maxY = (int) Math.min(img.getHeight() - 1, Math.floor(Math.max(v1.y, Math.max(v2.y, v3.y))));

                    double triangleArea = (v1.y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - v1.x);

                    for (int y = minY; y <= maxY; y++)
                    {
                        for (int x = minX; x <= maxX; x++)
                        {
                            double b1 = ((y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - x)) / triangleArea;
                            double b2 = ((y - v1.y) * (v3.x - v1.x) + (v3.y - v1.y) * (v1.x - x)) / triangleArea;
                            double b3 = ((y - v2.y) * (v1.x - v2.x) + (v1.y - v2.y) * (v2.x - x)) / triangleArea;

                            if (b1 >= 0 && b1 <= 1 && b2 >= 0 && b2 <= 1 && b3 >= 0 && b3 <= 1)
                            {
                                double depth = b1 * v1.z + b2 * v2.z + b3 * v3.z;
                                int zIndex = y * img.getWidth() + x;
                                if (zBuffer[zIndex] < depth) {
                                    //img.setRGB(x, y, shadeAngle(tr.color, angleCos).getRGB());
                                    img.setRGB(x, y, shadeDepth(tr.color, angleCos, depth).getRGB());
                                    zBuffer[zIndex] = depth;
                                    //System.out.println(depth);
                                }
                            }
                        }
                    }
                }
                g2.drawImage(img, 0, 0, null);
            }
        };
        pane.add(renderPanel, BorderLayout.CENTER);

        horizontalSlider.addChangeListener(e -> renderPanel.repaint());
        verticalSlider.addChangeListener(e -> renderPanel.repaint());
        rollSlider.addChangeListener(e -> renderPanel.repaint());
        FoVSlider.addChangeListener(e -> renderPanel.repaint());

        frame.setSize(900, 900);
        frame.setVisible(true);
    }

    private static Color shadeAngle(Color color, double shade)
    {
        double redLinear = Math.pow(color.getRed(), 2.4) * shade;
        double greenLinear = Math.pow(color.getGreen(), 2.4) * shade;
        double blueLinear = Math.pow(color.getBlue(), 2.4) * shade;

        int red = (int) Math.pow(redLinear, 1/2.4);
        int green = (int) Math.pow(greenLinear, 1/2.4);
        int blue = (int) Math.pow(blueLinear, 1/2.4);

        return new Color(red, green, blue);
    }

    private static Color shadeDepth(Color color, double shade, double depth)
    {
        //double high = -370;
        //double low = -411;
        double high = -395;
        double low = -403;
        double dfr = high-low;

        double cPrc = (Math.abs(depth - high))/(dfr/100);
        int cVal = (int)(255-(255*(cPrc/100)));

        int red = Math.max(0, Math.min(255, cVal));
        int green = Math.max(0, Math.min(255, cVal));
        int blue = Math.max(0, Math.min(255, cVal));

        double redLinear = Math.pow(color.getRed(), 2.4) * shade;
        double greenLinear = Math.pow(color.getGreen(), 2.4) * shade;
        double blueLinear = Math.pow(color.getBlue(), 2.4) * shade;

        red += (int) Math.pow(redLinear, 1/2.4);
        green += (int) Math.pow(greenLinear, 1/2.4);
        blue += (int) Math.pow(blueLinear, 1/2.4);


        return new Color(red/2, green/2, blue/2);
    }
}