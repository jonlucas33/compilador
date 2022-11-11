package compiladorl3;

public class Sintatico1 {
    private Lexico lexico;
    private Token token;
    
    public Sintatico1(Lexico lexico){
        this.lexico=lexico;
    }
    
    public void S(){ //estado inicial
        this.token = this.lexico.nextToken();
        if (!token.getLexema().equals("main")) {
            throw new RuntimeException("Main não declarado: " + this.token.getLexema());
        }
        this.token = this.lexico.nextToken();
        if (!token.getLexema().equals("(")) {
            throw new RuntimeException(
                    "Parênteses não foi aberto: " + this.token.getLexema());
        }
        this.token = this.lexico.nextToken();
        if (!token.getLexema().equals(")")) {
            throw new RuntimeException(
                    "Parênteses não foi fechado: " + this.token.getLexema());
        }
        this.token=this.lexico.nextToken();
        this.E();
        if(this.token.getTipo() == Token.TIPO_FIM_CODIGO){
            System.out.println("Código finalizado.");
        }else{
             throw new RuntimeException("Erro! Era para ser um identificador " + "ou numero proximo de "+ this.token.getLexema());
        }
    }
    
    //Bloco/case
    private void B()  {

        if (!this.token.getLexema().equals("{")) {
            throw new RuntimeException("Error: Expected open braces, near" + this.token.getLexema());
        }

        this.token = this.lexico.nextToken();

        if (!this.token.getLexema().equals("}")) {
            throw new RuntimeException("Error: Expected close braces, near: " + this.token.getLexema());
        }

        this.token = this.lexico.nextToken();
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
