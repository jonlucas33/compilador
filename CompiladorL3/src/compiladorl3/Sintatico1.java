package compiladorl3;

import java.io.FileNotFoundException;

import javax.management.RuntimeErrorException;

public class Sintatico1 {
    private Lexico lexico;
    private Token token;
    
    public Sintatico1(Lexico lexico){
        this.lexico=lexico;
    }
    
    //Estado inicial
    public void S() throws FileNotFoundException{ 
        this.token = this.lexico.nextToken();
        if (!token.getLexema().equals("main")) {
            this.lexico.getColumnAndLine(this.token.getLexema());
            throw new RuntimeException("Main não declarado: " + this.token.getLexema());
        }
        this.token = this.lexico.nextToken();
        if (!token.getLexema().equals("(")) {
            this.lexico.getColumnAndLine(this.token.getLexema());
            throw new RuntimeException("Parênteses não foi aberto: " + this.token.getLexema());
        }
        this.token = this.lexico.nextToken();
        if (!token.getLexema().equals(")")) {
            this.lexico.getColumnAndLine(this.token.getLexema());
            throw new RuntimeException("Parênteses não foi fechado: " + this.token.getLexema());
        }
        this.token=this.lexico.nextToken();

        B();

        if(this.token.getTipo() == Token.TIPO_FIM_CODIGO){
            System.out.println("Código finalizado.");
        }else{
             this.lexico.getColumnAndLine(this.token.getLexema());
             throw new RuntimeException("Erro! Era para ser um identificador " + "ou numero proximo de "+ this.token.getLexema());
        }
    }
    
    //Bloco/case
    private void B() throws FileNotFoundException  {

        if (!this.token.getLexema().equals("{")) {
            this.lexico.getColumnAndLine(this.token.getLexema());
            throw new RuntimeException("Chave não foi aberta: " + this.token.getLexema());
        }

        this.token = this.lexico.nextToken();

        CM();
        
        if (!this.token.getLexema().equals("}")) {
            this.lexico.getColumnAndLine(this.token.getLexema());
            throw new RuntimeException("Chave não foi fechada: " + this.token.getLexema());
        }

        this.token = this.lexico.nextToken();
    }

    //Comando
    private void CM() throws FileNotFoundException {
        if (this.token.getLexema().equals("int") ||
                this.token.getLexema().equals("float") ||
                this.token.getLexema().equals("char")) {
            //Para declaração - DEC()
            DEC();
            CM();
        } else if (this.token.getLexema().equals("{") ||
                this.token.getTipo() == Token.TIPO_IDENTIFICADOR) {
            //Para comando dentro dos blocos - CB()
            CB();
            CM();
        } else if (this.token.getLexema().equals("if")) {
            //Para operador Relacional - PR()
            PR();
        } else if (this.token.getLexema().equals("while")) {
            //Para while/loop - WH()
            WH();
        } else if (this.token.getLexema().equals("}")) {
            return;
        } else {
            this.lexico.getColumnAndLine(this.token.getLexema());
            throw new RuntimeException("Error, comando esperado: " + this.token.getLexema());
        }
    }

    //Declaração
    private void DEC() throws FileNotFoundException {
        if (this.token.getLexema().equals("int") ||
                this.token.getLexema().equals("float") ||
                this.token.getLexema().equals("char")) {
                //Para Declaração - Declaracao() 
                Declaracao();
        } else {
            this.lexico.getColumnAndLine(this.token.getLexema());
            throw new RuntimeException("Error, comando esperado: " + this.token.getLexema());
        }
    }

    //Declaração de variável
    private void Declaracao() throws FileNotFoundException {
        if (!(this.token.getLexema().equals("int") ||
                this.token.getLexema().equals("float") ||
                this.token.getLexema().equals("char"))) {
                    this.lexico.getColumnAndLine(this.token.getLexema());
                    throw new RuntimeException("Declaração de variável errada: " + this.token.getLexema());
        }

        this.token = this.lexico.nextToken();

        //Identificador pós declaração do tipo de variável
        if (this.token.getTipo() != Token.TIPO_IDENTIFICADOR) {
            this.lexico.getColumnAndLine(this.token.getLexema());
            throw new RuntimeException("Error, identificador esperado: " + this.token.getLexema());
        }

        this.token = this.lexico.nextToken();

        //Ponto e vírgula pós identificador
        if (!this.token.getLexema().equalsIgnoreCase(";")) {
            this.lexico.getColumnAndLine(this.token.getLexema());
            throw new RuntimeException("Error, Ponto e vírgula (';') esperado: " + this.token.getLexema());
        }
        this.token = this.lexico.nextToken();
    }

    //Comando dentro dos blocos
    private void CB() throws FileNotFoundException {
        if (this.token.getLexema().equals("{")) {
            B();
        } else if (this.token.getTipo() == Token.TIPO_IDENTIFICADOR) {
            //Método para atribuição => variável = valor
            Atribuicao();
        } else{
            this.lexico.getColumnAndLine(this.token.getLexema());
            throw new RuntimeException("Comando errado " +this.token.getLexema());
        }
    }

    //Atribuição de valores para variáveis
    private void Atribuicao() throws FileNotFoundException {
        this.token=this.lexico.nextToken();
        if (!this.token.getLexema().equals("=")) {
            this.lexico.getColumnAndLine(this.token.getLexema());
            throw new RuntimeException("Sinal de igual ('=') esperado " +this.token.getLexema());
        }

        this.token=this.lexico.nextToken();

        //Para dar valores as variáveis => variável = inteiro|real|char - ATR()
        ATR();

        if (!this.token.getLexema().equalsIgnoreCase(";")) {
            this.lexico.getColumnAndLine(this.token.getLexema());
            throw new RuntimeException("Error, Ponto e vírgula (';') esperado: " + this.token.getLexema());
        }
        this.token = this.lexico.nextToken();
    }

    //Possíveis atribuições para a variável
    private void ATR() {
        if (this.token.getTipo() == Token.TIPO_CHAR) {
            this.token=this.lexico.nextToken();
            ATR();
        } else if (this.token.getTipo() == Token.TIPO_INTEIRO) {
            this.token=this.lexico.nextToken();
            if (this.token.getLexema().equals("*") ||
             this.token.getLexema().equals("/") ||
             this.token.getLexema().equals("+") ||
             this.token.getLexema().equals("-")) {
                this.token=this.lexico.nextToken();
                ATR();
            } else if (this.token.getLexema().equalsIgnoreCase(";")) {
                return;
            }
        } else if (this.token.getTipo() == Token.TIPO_REAL) {
            this.token=this.lexico.nextToken();
            if (this.token.getLexema().equals("*") ||
             this.token.getLexema().equals("/") ||
             this.token.getLexema().equals("+") ||
             this.token.getLexema().equals("-")) {
                this.token=this.lexico.nextToken();
                ATR();
            } else if (this.token.getLexema().equalsIgnoreCase(";")) {
                return;
            }
        }
    }

    //Operador relacional - if
    private void PR() throws FileNotFoundException {
        if (!this.token.getLexema().equals("if")) {
            this.lexico.getColumnAndLine(this.token.getLexema());
            throw new RuntimeException("Palavra reservada errada: " +this.token.getLexema());            
        }

        this.token=this.lexico.nextToken();

        if (!this.token.getLexema().equals("(")) {
            this.lexico.getColumnAndLine(this.token.getLexema());
            throw new RuntimeException("Parentêses não foi aberto: " +this.token.getLexema());
        }

        this.token = this.lexico.nextToken();

        //Expressão relacional - ER()
        //ER();

        if (!token.getLexema().equals(")")) {
            this.lexico.getColumnAndLine(this.token.getLexema());
            throw new RuntimeException("Parênteses não foi fechado: " + this.token.getLexema());
        }
        this.token=this.lexico.nextToken();

        CM();

        if (this.token.getLexema().equals("else")) {
            this.token=this.lexico.nextToken();
            CM();
        } else {
            return;
        }
    }

    //While/loop
    private void WH() throws FileNotFoundException {
        if (!this.token.getLexema().equals("while")) {
            this.lexico.getColumnAndLine(this.token.getLexema());
            throw new RuntimeException("Palavra reservada errada: " + this.token.getLexema());
        }
        this.token=this.lexico.nextToken();

        if (!this.token.getLexema().equals("(")) {
            this.lexico.getColumnAndLine(this.token.getLexema());
            throw new RuntimeException("Parentêses não foi aberto: " +this.token.getLexema());
        }

        this.token = this.lexico.nextToken();

        //Expressão relacional - ER()
        //ER();

        if (!token.getLexema().equals(")")) {
            this.lexico.getColumnAndLine(this.token.getLexema());
            throw new RuntimeException("Parênteses não foi fechado: " + this.token.getLexema());
        }
        this.token=this.lexico.nextToken();

        CM();
    }


    private void E(){
        this.T();
        this.El();
    }
    
    private void El(){
        if(this.token.getTipo() == Token.TIPO_OPERADOR_ARITMETICO){
            this.OP();
            this.T();
            this.El();
        }else{
            
        }     
    }
    
    private void T(){
        if(this.token.getTipo() == Token.TIPO_IDENTIFICADOR || this.token.getTipo() == Token.TIPO_INTEIRO || this.token.getTipo() == Token.TIPO_REAL){
            this.token=this.lexico.nextToken();
        }else{
            throw new RuntimeException("Erro! Era para ser um identificador " + "ou numero proximo de "+ this.token.getLexema());
        }        
    }
    
    private void OP(){
        if(this.token.getTipo() == Token.TIPO_OPERADOR_ARITMETICO){
            this.token=this.lexico.nextToken();
        }else{
              throw new RuntimeException("Erro! Era para ser um operador" + " aritmetico(+/-/*/%/) proximo de "+ this.token.getLexema());
        }      
    }
}
