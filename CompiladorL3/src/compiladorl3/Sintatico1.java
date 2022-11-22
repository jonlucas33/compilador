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

        //Bloco/case - B()
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

        //Comando - CM()
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
        ER();

        this.token = this.lexico.nextToken();

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
        ER();

        this.token = this.lexico.nextToken();

        if (!token.getLexema().equals(")")) {
            this.lexico.getColumnAndLine(this.token.getLexema());
            throw new RuntimeException("Parênteses não foi fechado: " + this.token.getLexema());
        }
        this.token=this.lexico.nextToken();

        CM();
    }

    //Expressão relacional
    private void ER() throws FileNotFoundException {
        //Primeiro elemento da expressão pode ser um identificador|valor(Real ou Inteiro).

        //Primeiro elemento da expressão é um identificador?
        if (this.token.getTipo() == Token.TIPO_IDENTIFICADOR) {
            this.token = this.lexico.nextToken();

            //Segundo elemento tem que ser um operador relacional.
            if (this.token.getTipo() != Token.TIPO_OPERADOR_RELACIONAL) {
                this.lexico.getColumnAndLine(this.token.getLexema());
                throw new RuntimeException("Operador relacional esperado "+this.token.getLexema());
            }

            this.token = this.lexico.nextToken();

            //Terceiro elemento pode ser do tipo char|inteiro|real.
            if (this.token.getTipo() == Token.TIPO_CHAR) {
                return;
            } else if (this.token.getTipo() == Token.TIPO_INTEIRO) {
                return;
            } else if (this.token.getTipo() == Token.TIPO_REAL) {
                return;
            } else{
                this.lexico.getColumnAndLine(this.token.getLexema());
                throw new RuntimeException("Tipo de variáveis diferesntes "+this.token.getLexema());
            }

        //Primeiro elemento da expressão é um valor(Real ou Inteiro).?
        } else if (this.token.getTipo() == Token.TIPO_REAL ||
        this.token.getTipo() == Token.TIPO_INTEIRO) {
            this.token = this.lexico.nextToken();

            //Segundo elemento tem que ser umoperador relacional.
            if (this.token.getTipo() != Token.TIPO_OPERADOR_RELACIONAL) {
                this.lexico.getColumnAndLine(this.token.getLexema());
                throw new RuntimeException("Operador relacional esperado "+this.token.getLexema());
            }

            this.token = this.lexico.nextToken();

            //Terceiro elemento pode ser do tipo Identificador|Real|Inteiro.

            //Não pode ser do tipo char.
            if (this.token.getTipo() == Token.TIPO_CHAR) {
                this.lexico.getColumnAndLine(this.token.getLexema());
                throw new RuntimeException("Tipo de variáveis diferesntes "+this.token.getLexema());

            //Terceiro elemento pode ser do tipo Identificador,pode ter três tipos => char|real|inteiro.
            } else if (this.token.getTipo() == Token.TIPO_IDENTIFICADOR) {
                //Caso seja char.
                if (this.token.getLexema().equals("char")) {
                this.lexico.getColumnAndLine(this.token.getLexema());
                throw new RuntimeException("Tipo de variáveis diferentes "+this.token.getLexema());
                //Caso seja float.
                } else if (this.token.getLexema().equals("float")) {
                    return;
                //Caso seja inteiro.
                } else if (this.token.getLexema().equals("int")) {
                    return;
                } else{
                this.lexico.getColumnAndLine(this.token.getLexema());
                throw new RuntimeException("Tipo de variáveis diferentes "+this.token.getLexema());
                }

            //Terceiro elemento pode ser do tipo Real|Inteiro.
            } else if (this.token.getTipo() == Token.TIPO_REAL ||
            this.token.getTipo() == Token.TIPO_INTEIRO) {
                return;
            }

        //Primeiro elemento da expressão DIFERENTE de um identificador|valor(Real ou Inteiro).
        } else{
            this.lexico.getColumnAndLine(this.token.getLexema());
            throw new RuntimeException("Ínicio de expressão incorreto "+this.token.getLexema());
        }
    }
}
