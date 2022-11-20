/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiladorl3;

import java.io.FileNotFoundException;

/**
 *
 * @author tarci
 */
public class CompiladorL3 {

    /**
     * @param args the command line arguments
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        // TODO code application logic here
        Lexico lexico = new Lexico("C:/Users/jllop/compilador-3/CompiladorL3/src/compiladorl3/codigo.txt");
        Sintatico1 sintatico = new Sintatico1(lexico);
        sintatico.S();
        /*Token t = null;
        while((t = lexico.nextToken()) != null){
            System.out.println(t.toString());
        }*/
    }
    
}
