package CameraTest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class CSVReader
{
    public static ArrayList<Triangle> Seperate()
    {
        String csvFile = "resources/vd_temp.txt";
        String line;
        String cvsSplitBy = ",";
        int step = 18;

        ArrayList<String> ar = new ArrayList<>();

        ArrayList<Triangle> tris = new ArrayList<>();

        Triangle tr1;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile)))
        {
            while ((line = br.readLine()) != null) {
                String[] values = line.split(cvsSplitBy);
                ar.addAll(Arrays.asList(values));
            }
        } catch (IOException e)
        {
            System.out.println(e);
        }
        int a = ar.size();

        for (int i = 0; i < a / step; i++)
        {
            tr1 = new Triangle(
                    new Vertex(Double.parseDouble(ar.get(i * step)), Double.parseDouble(ar.get(1 + i * step)), Double.parseDouble(ar.get(2 + i * step)), 1, Double.parseDouble(ar.get(4 + i * step)), Double.parseDouble(ar.get(5 + i * step))),
                    new Vertex(Double.parseDouble(ar.get(6 + i * step)), Double.parseDouble(ar.get(7 + i * step)), Double.parseDouble(ar.get(8 + i * step)), 1, Double.parseDouble(ar.get(10 + i * step)), Double.parseDouble(ar.get(11 + i * step))),
                    new Vertex(Double.parseDouble(ar.get(12 + i * step)), Double.parseDouble(ar.get(13 + i * step)), Double.parseDouble(ar.get(14 + i * step)), 1, Double.parseDouble(ar.get(16 + i * step)), Double.parseDouble(ar.get(17 + i * step)))
            );

            tris.add(tr1);
        }
        return tris;
    }
}