package com.mycompany.rtree2d;

import com.mycompany.basicrtree.BasicRTree;
import com.mycompany.hilbeRTree.Bound;
import com.mycompany.hilbeRTree.HilbertRTree;
import com.mycompany.hilbeRTree.HilbertNode;
import com.mycompany.starRTree.StarRTree;
import com.mycompany.utilsRTree.Entry;
import com.mycompany.utilsRTree.Rtree;
import com.mycompany.utilsRTree.SplitCase;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws InterruptedException
    {
        
        JFrame fen = new JFrame();
        Panel pan = new Panel();
        fen.add(pan);
        fen.setSize(1600, 900);
        fen.setLocationRelativeTo(null);
        fen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fen.setVisible(true);
        fen.setTitle("R-Tree");
        int time = 1;
        Rtree arbre = new HilbertRTree(4, 2);
//        Rtree arbre = new StarRTree(16);
//        Rtree arbre = new BasicRTree(16, SplitCase.LINEAR);
        int compteur = 0;
////          
        
        for(;compteur<=100000;compteur++){
            double x = 200*((Math.random()*2)-1);
            double y = 120*((Math.random()*2)-1);
            Shape s = new Ellipse2D.Double(x, y, 0.5, 0.5);
            arbre.insert(new Entry(s, s.getBounds2D()));
        }
        
        
//        for(int j= -120;j<=120;j+=2){
//            for(int i = -200;i<=200;i+=2){
//                Shape s = new Ellipse2D.Double(i, j, 0.5, 0.5);
//                arbre.insert(new Entry(s, s.getBounds2D()));
//                compteur++;
////                pan.setArbre(arbre);
////                pan.repaint();
////                Thread.sleep(time);
//            }
//        }
//        
////        System.out.println("ok pour la premiere vague");
////        
//        for(int j= 121;j>=-119;j-=2){
//            for(int i = 201;i>=-199;i-=2){
//                Shape s = new Ellipse2D.Double(i, j, 0.5, 0.5);
//                arbre.insert(new Entry(s, s.getBounds2D()));
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
        
            Shape s1 = new Ellipse2D.Double(-60, -21, 5, 5);
            arbre.insert(new Entry(s1, s1.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            System.out.println("1");
            Shape s2 = new Ellipse2D.Double(-60, 0, 5, 5);
            arbre.insert(new Entry(s2, s2.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            System.out.println("2");
            Shape s3 = new Ellipse2D.Double(-60, 21, 5, 5);
            arbre.insert(new Entry(s3, s3.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            System.out.println("3");
            Shape s4 = new Ellipse2D.Double(-60, 45, 5, 5);
            arbre.insert(new Entry(s4, s4.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            System.out.println("4");
            Shape s5 =new Ellipse2D.Double(-60, 60, 5, 5);
            arbre.insert(new Entry(s5, s5.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            System.out.println("5");
            Shape s6 = new Ellipse2D.Double(-45, 60, 5, 5);
            arbre.insert(new Entry(s6, s6.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            System.out.println("6");
            Shape s7 = new Ellipse2D.Double(-21, 60, 5, 5);
            arbre.insert(new Entry(s7, s7.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            System.out.println("7");
            Shape s8 =new Ellipse2D.Double(0, 60, 5, 5);
            arbre.insert(new Entry(s8, s8.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            System.out.println("8");
            Shape s9 = new Ellipse2D.Double(21, 60, 5, 5);
            arbre.insert(new Entry(s9, s9.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            System.out.println("9");
            Shape s10 = new Ellipse2D.Double(45, 60, 5, 5);
            arbre.insert(new Entry(s10, s10.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            System.out.println("10");
            Shape s11 = new Ellipse2D.Double(60, 60, 5, 5);
            arbre.insert(new Entry(s11, s11.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            System.out.println("11");
            Shape s12 = new Ellipse2D.Double(60, 45, 5, 5);
            arbre.insert(new Entry(s12, s12.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            System.out.println("12");
            
            Shape s13 = new Ellipse2D.Double(60, 21, 5, 5);
            arbre.insert(new Entry(s13, s13.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            System.out.println("13");
            Shape s14 = new Ellipse2D.Double(60, 0, 5, 5);
            arbre.insert(new Entry(s14, s14.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            System.out.println("14");
            Shape s15 = new Ellipse2D.Double(60, -21, 5, 5);
            arbre.insert(new Entry(s15, s15.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
            Shape s16 = new Ellipse2D.Double(60, -45, 5, 5);
            arbre.insert(new Entry(s16, s16.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
            Shape s17 = new Ellipse2D.Double(60, -60, 5, 5);
            arbre.insert(new Entry(s17, s17.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
            Shape s18 = new Ellipse2D.Double(45, -60, 5, 5);
            arbre.insert(new Entry(s18, s18.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
            Shape s19 = new Ellipse2D.Double(21, -60, 5, 5);
            arbre.insert(new Entry(s19, s19.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
            Shape s20= new Ellipse2D.Double(0, -60, 5, 5);
            arbre.insert(new Entry(s20, s20.getBounds2D()));///spklit declencher apres ici
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
            Shape s21= new Ellipse2D.Double(-21, -60, 5, 5);
            arbre.insert(new Entry(s21, s21.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
            Shape s22 = new Ellipse2D.Double(-21, 45, 5, 5);
            arbre.insert(new Entry(s22, s22.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
            Shape s23 = new Ellipse2D.Double(-21, -21, 5, 5);
            arbre.insert(new Entry(s23, s23.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
            Shape s24 = new Ellipse2D.Double(-21, 0, 5, 5);
            arbre.insert(new Entry(s24, s24.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
            Shape s25 = new Ellipse2D.Double(-21, 21, 5, 5);
            arbre.insert(new Entry(s25, s25.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
            Shape s26 = new Ellipse2D.Double(0, 21, 5, 5);
            arbre.insert(new Entry(s26, s26.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
            Shape s27 = new Ellipse2D.Double(21, 21, 5, 5);
            arbre.insert(new Entry(s27, s27.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
            Shape s28 = new Ellipse2D.Double(21, 0, 5, 5);
            arbre.insert(new Entry(s28, s28.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
            Shape s29 = new Ellipse2D.Double(21, -21, 5, 5);
            arbre.insert(new Entry(s29, s29.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
            Shape s30 = new Ellipse2D.Double(0, -21, 5, 5);
            arbre.insert(new Entry(s30, s30.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
            Shape s31 = new Ellipse2D.Double(0, 0, 5, 5);
            arbre.insert(new Entry(s31, s31.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
            Shape s32 = new Ellipse2D.Double(-60, -21, 5, 5);
            arbre.insert(new Entry(s32, s32.getBounds2D()));
            pan.setArbre(arbre);
            pan.repaint();
//            Thread.sleep(time); 
            
            
            
            ////////////////////////////////////////////////////////
            //////////////////////////////////////////////////////
            
//            for(int j= -120;j<=120;j+=20){
//                for(int i = -200;i<=200;i+=15){
//                    Shape s = new Ellipse2D.Double(i, j, 5, 5);
////                    if(j==120&&i==190){
////                        System.out.println("");
////                    }
//                    arbre.delete(new Entry(s, s.getBounds2D()));
//                    compteur--;
//                    pan.setArbre(arbre);
//                    pan.repaint();
//                    Thread.sleep(20);
//                }
//            }
//            
//            for(int j= 110;j>=-110;j-=10){
//                for(int i = 195;i>=-190;i-=15){
//                    Shape s = new Ellipse2D.Double(i, j, 5, 5);
//                    arbre.delete(new Entry(s, s.getBounds2D()));
//                    compteur++;
//                    pan.setArbre(arbre);
//                    pan.repaint();
//                    Thread.sleep(20);
//                }
//            }
//            
//            for(int j= -195;j<=195;j+=10){
//                for(int i = 115;i>=-115;i-=10){
//                    Shape s = new Ellipse2D.Double(j, i, 5, 5);
//                    arbre.delete(new Entry(s, s.getBounds2D()));
//                    compteur++;
//                    pan.setArbre(arbre);
//                    pan.repaint();
//                    Thread.sleep(20);
//                }
//            }
            
//            
//            arbre.delete(new Entry(s27, s27.getBounds2D()));//
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(new Entry(s29, s29.getBounds2D()));//
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(new Entry(s30, s30.getBounds2D()));//
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(new Entry(s23, s23.getBounds2D()));//
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(new Entry(s31, s31.getBounds2D()));//
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(new Entry(s19, s19.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(new Entry(s17, s17.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(new Entry(s13, s13.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(new Entry(s14, s14.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(new Entry(s15, s15.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(new Entry(s10, s10.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            
//            arbre.delete(new Entry(s28, s28.getBounds2D()));//
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(new Entry(s26, s26.getBounds2D()));//
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(new Entry(s25, s25.getBounds2D()));//
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(new Entry(s24, s24.getBounds2D()));//
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(new Entry(s22, s22.getBounds2D()));//
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(new Entry(s21, s21.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(new Entry(s18, s18.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(new Entry(s16, s16.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(new Entry(s11, s11.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(new Entry(s12, s12.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            arbre.delete(new Entry(s9, s9.getBounds2D()));
//            pan.setArbre(arbre);
//            pan.repaint();
//            Thread.sleep(time);
//            System.out.println("this is the end");
        Rectangle2D searc = new Rectangle2D.Double(-10, -50, 50, 50);
        List<Entry> lE = new ArrayList<Entry>();
        long timeBase = System.nanoTime();
        arbre.search(searc, lE);
        long timeSearch = System.nanoTime();
        
            
            
//        System.out.println(arbre);
        System.out.println("/////////////////////////////////////////////////////");
        System.out.println("le compteur = "+(compteur+32));
//        System.out.println("max element = "+(((HilbertLeaf)((HilbertRTree)arbre).getTreeTrunk())).getAllEntry().size());
        System.out.println("timeBase   = "+timeBase);
        System.out.println("timeSearch = "+timeSearch);
        System.out.println("le temps de recherche est de : "+(timeSearch-timeBase));
        System.out.println("taille de la list de recherche = "+lE.size());
        System.out.println("/////////////////////////////////////////////////////");
//        Rectangle2D searc = new Rectangle2D.Double(10, 50, 100, 150);
//        HilbertLeaf hl = (((HilbertLeaf)((HilbertRTree)arbre).getTreeTrunk()));
//        List<Bound> lE = new ArrayList<Bound>(hl.getAllEntry());
//        Rectangle2D rect1 = hl.getBounds2D();
//        Rectangle2D rtest = hl.getEnveloppeMin(lE);
//        System.out.println("rect1 = "+rect1);
//        System.out.println("rtest = "+rtest);
////        arbre.search(searc, lE);
//        System.out.println("lE size : "+lE.size());
    }
}
