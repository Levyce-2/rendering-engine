package BasicShading;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class CSVReader
{
    public static ArrayList<Triangle> Seperate()
    {
        String csvFile = "resources/vd_fox.txt";
        String line = "";
        String cvsSplitBy = ",";

        ArrayList<String> ar = new ArrayList<>();

        ArrayList<Triangle> tris = new ArrayList<>();

        Triangle tr1;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile)))
        {
            while ((line = br.readLine()) != null)
            {
                String[] values = line.split(cvsSplitBy);
                ar.addAll(Arrays.asList(values));
            }
        }
        catch (IOException e)
        {
            System.out.println(e);
        }
        int a = ar.size();

        for(int i = 0; i < a / 15; i++)
        {
            tr1 = new Triangle(new Vertex(Double.parseDouble(ar.get(i * 15)), Double.parseDouble(ar.get(1 + i* 15)), Double.parseDouble(ar.get(2 + i* 15)), 1),
                    new Vertex(Double.parseDouble(ar.get(4 + i* 15)), Double.parseDouble(ar.get(5 + i* 15)), Double.parseDouble(ar.get(6 + i* 15)), 1),
                    new Vertex(Double.parseDouble(ar.get(8 + i* 15)), Double.parseDouble(ar.get(9 + i* 15)), Double.parseDouble(ar.get(10 + i* 15)), 1),
                    new Color(Integer.parseInt(ar.get(12 + i* 15)), Integer.parseInt(ar.get(13 + i* 15)), Integer.parseInt(ar.get(14 + i* 15))));

            tris.add(tr1);
        }
        return tris;
    }
}