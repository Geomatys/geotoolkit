package com.mycompany.rtree2d;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JFrame;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.index.tree.JTreePanel;
import org.geotoolkit.index.tree.Node2D;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.index.tree.TreeFactory;
import org.geotoolkit.index.tree.TreeUtils;
import org.geotoolkit.index.tree.basic.SplitCase;
import org.geotoolkit.util.converter.Classes;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws InterruptedException
    {
        
        
        
        
        
        int time = 1;
//        Tree arbre = TreeFactory.createBasicRTree2D(SplitCase.LINEAR, 4);
//        Tree arbre = TreeFactory.createStarRTree2D(4);//declenchement split ou ajout a revoir
        Tree arbre = TreeFactory.createHilbertRTree2D(4, 2);
        int compteur = 0;
        
//        for(;compteur<=500000;compteur++){
//            double signeX = (Math.random()<0.5)?-1:1;
//            double signeY = (Math.random()<0.5)?1:-1;
//            double x = 200*Math.random()*signeX;
//            double y = 120*Math.random()*signeY;
//            arbre.insert(new Ellipse2D.Double(x, y, 0.5, 0.5));
//        }
        JFrame fen = new JFrame();
        JTreePanel pan = new JTreePanel(arbre, null);
        fen.add(pan);
        fen.setSize(1600, 900);
        fen.setLocationRelativeTo(null);
        fen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fen.setVisible(true);
        fen.setTitle("R-Tree");
        Thread.sleep(time);
        ///////////////////////////////////////////////////////////////////////////////////////
        List<Shape> lData = new ArrayList<Shape>();
        for(int j= -120;j<=120;j+=4){
            for(int i = -200;i<=200;i+=4){
                lData.add(new Ellipse2D.Double(i, j, 1, 1));
                compteur++;
            }
        }
        
//////        int test = 0;
        
        ///////////////////////////////////////////////////////////////////////////////////////////
//        System.out.println("ok pour la premiere vague");
//        
//        for(int j= 121;j>=-119;j-=2){
//            for(int i = 201;i>=-199;i-=2){
//                Shape s = new Ellipse2D.Double(i, j, 0.5, 0.5);
//                arbre.insert(s);
//                compteur++;
////                pan.setArbre(arbre);
////                pan.repaint();
////                Thread.sleep(time);
//            }
//        }
////        
////        System.out.println("ok pour la deuxieme vague");
//////        
//        for(int j= -196;j<=204;j+=2){
//            for(int i = 124;i>=-116;i-=2){
//                Shape s = new Ellipse2D.Double(j, i, 0.5, 0.5);
//                arbre.insert(new Entry(s, s.getBounds2D()));
//                compteur++;
////                pan.setArbre(arbre);
////                pan.repaint();
////                Thread.sleep(time);
//            }
//        }
//        System.out.println("ok pour la troisieme vague");
//        ok pour la troisieme
//        System.out.println("le compteur = "+compteur);
//        System.out.println("max element = "+(((HilbertLeaf)((HilbertRTree)arbre).getTreeTrunk())).getAllEntry().size());
        
        
//        Shape s1 = new Ellipse2D.Double(-60, -21, 5, 5);
//        Shape s2 = new Ellipse2D.Double(-60, 0, 5, 5);
//        Shape s3 = new Ellipse2D.Double(-60, 21, 5, 5);
//        Shape s4 = new Ellipse2D.Double(-60, 45, 5, 5);
//        Shape s5 =new Ellipse2D.Double(-60, 60, 5, 5);
//        Shape s6 = new Ellipse2D.Double(-45, 60, 5, 5);
//        Shape s7 = new Ellipse2D.Double(-21, 60, 5, 5);
//        Shape s8 =new Ellipse2D.Double(0, 60, 5, 5);
//        Shape s9 = new Ellipse2D.Double(21, 60, 5, 5);
//        Shape s10 = new Ellipse2D.Double(45, 60, 5, 5);
//        Shape s11 = new Ellipse2D.Double(60, 60, 5, 5);
//        Shape s12 = new Ellipse2D.Double(60, 45, 5, 5);
//        Shape s13 = new Ellipse2D.Double(60, 21, 5, 5);
//        Shape s14 = new Ellipse2D.Double(60, 0, 5, 5);
//        Shape s15 = new Ellipse2D.Double(60, -21, 5, 5);
//        Shape s16 = new Ellipse2D.Double(60, -45, 5, 5);
//        Shape s17 = new Ellipse2D.Double(60, -60, 5, 5);
//        Shape s18 = new Ellipse2D.Double(45, -60, 5, 5);
//        Shape s19 = new Ellipse2D.Double(21, -60, 5, 5);
//        Shape s20= new Ellipse2D.Double(0, -60, 5, 5);
//        Shape s21= new Ellipse2D.Double(-21, -60, 5, 5);
//        Shape s22 = new Ellipse2D.Double(-21, 45, 5, 5);
//        Shape s23 = new Ellipse2D.Double(-21, -21, 5, 5);
//        Shape s24 = new Ellipse2D.Double(-21, 0, 5, 5);
//        Shape s25 = new Ellipse2D.Double(-21, 21, 5, 5);
//        Shape s26 = new Ellipse2D.Double(0, 21, 5, 5);
//        Shape s27 = new Ellipse2D.Double(21, 21, 5, 5);
//        Shape s28 = new Ellipse2D.Double(21, 0, 5, 5);
//        Shape s29 = new Ellipse2D.Double(21, -21, 5, 5);
//        Shape s30 = new Ellipse2D.Double(0, -21, 5, 5);
//        Shape s31 = new Ellipse2D.Double(0, 0, 5, 5);
//        Shape s32 = new Ellipse2D.Double(-60, -45, 5, 5);
//        lData.add(s1); lData.add(s2); lData.add(s3); lData.add(s4);
//        lData.add(s5); lData.add(s6); lData.add(s7); lData.add(s8);
//        lData.add(s9); lData.add(s10);lData.add(s11);lData.add(s12);
//        lData.add(s13);lData.add(s14);lData.add(s15);lData.add(s16);
//        lData.add(s17);lData.add(s18);lData.add(s19);lData.add(s20);
//        lData.add(s24);lData.add(s23);lData.add(s22);lData.add(s21);
//        lData.add(s25);lData.add(s26);lData.add(s27);lData.add(s28);
//        lData.add(s29);lData.add(s30);lData.add(s31);lData.add(s32);
//        compteur+=32;
        
        Collections.shuffle(lData);
        for(Shape sh : lData){
            arbre.insert(sh);
        }
        
//        for(int j= -120;j<=120;j+=4){
//            for(int i = -200;i<=200;i+=4){
//                arbre.delete(new Ellipse2D.Double(i, j, 1, 1));
//                compteur--;
//            }
//        }
        
        
            ////////////affiner delete methode !!!!!!!!
            
            
//            arbre.delete(s1);
//            pan.setTree(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(s2);
//            pan.setTree(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(s3);
//            pan.setTree(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(s4);
//            pan.setTree(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(s5);
//            pan.setTree(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(s6);
//            pan.setTree(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(s7);
//            pan.setTree(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(s8);
//            pan.setTree(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(s9);
//            pan.setTree(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(s10);
//            pan.setTree(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(s11);
//            pan.setTree(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(s12);
//            pan.setTree(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(s13);
//            pan.setTree(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(s14);
//            pan.setTree(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(s15);
//            pan.setTree(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(s16);
//            pan.setTree(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(s17);
//            pan.setTree(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(s18);
//            pan.setTree(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(s19);
//            pan.setTree(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(s20);
//            pan.setTree(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(s21);
//            pan.setTree(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(s22);
//            pan.setTree(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(s23);
//            pan.setTree(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(s24);
//            pan.setTree(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(s25);
//            pan.setTree(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(s26);
//            pan.setTree(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(s27);
//            pan.setTree(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(s28);
//            pan.setTree(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(s29);
//            pan.setTree(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(s30);
//            pan.setTree(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(s31);
//            pan.setTree(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(s32);
//            pan.setTree(arbre);
//            pan.repaint();
//            Thread.sleep(time);
            
            Collections.shuffle(lData);
            for(Shape sh : lData){
                compteur--;
                if(compteur == 100){
                    time = 100;
                }
                arbre.delete(sh);
                pan.setTree(arbre);
                pan.repaint();
                Thread.sleep(time);
            }
            
        Rectangle2D searc = new Rectangle2D.Double(-10, -50, 50, 50);
        List<Shape> lEbis = new ArrayList<Shape>();
        long timeBase = System.nanoTime();
        arbre.search(searc, lEbis);
        long timeSearch = System.nanoTime();
        
        System.out.println(arbre);
        
        System.out.println("/////////////////////////////////////////////////////");
        System.out.println("le compteur = "+(compteur));
        List<Shape> lE = new ArrayList<Shape>();
        System.out.println("enveloppe de root = "+arbre.getRoot().getBoundary());
        arbre.search(arbre.getRoot().getBoundary().getBounds2D(), lE);
        System.out.println("compteur reeel = "+lE.size());
        System.out.println("timeBase   = "+timeBase);
        System.out.println("timeSearch = "+timeSearch);
        System.out.println("le temps de recherche est de (en nano): "+(timeSearch-timeBase));
        System.out.println("le temps de recherche est de (en ms): "+(timeSearch-timeBase)*10E-7);
        System.out.println("taille de la list de recherche = "+lE.size());
        System.out.println("/////////////////////////////////////////////////////");
//        
//        JFrame fen = new JFrame();
//        JTreePanel pan = new JTreePanel(arbre, lEbis);
//        fen.add(pan);
//        fen.setSize(1600, 900);
//        fen.setLocationRelativeTo(null);
//        fen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        fen.setVisible(true);
//        fen.setTitle("R-Tree");
//        Thread.sleep(time);
        
        
    }
}
