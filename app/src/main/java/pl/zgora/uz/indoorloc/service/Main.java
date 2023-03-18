package pl.zgora.uz.indoorloc.service;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Wczytanie punktów i odległości
        double xa = 15.966;
        double ya = 0;
        double za = 10.646;
        System.out.println("Podaj odległość od punktu A (r):");
        double r = 10.7030;
        System.out.println("Podaj współrzędne punktu B (xb, yb, zb):");
        double xb = 0;
        double yb = 0;
        double zb = 0;
        System.out.println("Podaj odległość od punktu B (s):");
        double s = 9.36687;
        System.out.println("Podaj współrzędne punktu C (xc, yc, zc):");
        double xc = 8.4316;
        double yc = 10.93;
        double zc = 4.6427;
        System.out.println("Podaj odległość od punktu C (t):");
        double t = 8.58196;

        // Obliczenie długości boków trójkąta ABC
        double dAB = Math.sqrt(Math.pow(xb-xa, 2) + Math.pow(yb-ya, 2) + Math.pow(zb-za, 2));
        double dAC = Math.sqrt(Math.pow(xc-xa, 2) + Math.pow(yc-ya, 2) + Math.pow(zc-za, 2));
        double dBC = Math.sqrt(Math.pow(xc-xb, 2) + Math.pow(yc-yb, 2) + Math.pow(zc-zb, 2));

        // Obliczenie współrzędnych punktu przecięcia trzech sfer
        double x = (Math.pow(r, 2) - Math.pow(s, 2) + Math.pow(dAB, 2)) / (2 * dAB);
        double y = ((Math.pow(r, 2) - Math.pow(t, 2) + Math.pow(dAC, 2) - 2 * x * (xa - xc)) / (2 * (yc - ya)))
                - ((xa - xb) / (yc - ya)) * x;
        double z = Math.sqrt(Math.pow(r, 2) - Math.pow(x, 2) - Math.pow(y, 2));

        // Obliczenie współrzędnych punktu P
        double xp = x + xa;
        double yp = y + ya;
        double zp = z + za;

        // Wyświetlenie wyniku
        System.out.println("Punkt P ma współrzędne (" + xp + ", " + yp + ", " + zp + ")");
    }
}
