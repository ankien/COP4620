public class CodeObject {
    private String code;
    private String temp;
    private String type;

    public CodeObject(){

    }

    public CodeObject( String code, String temp, String type){
        this.code = code;
        this.temp = temp;
        this.type = type;
    }

    public String getCode(){
        return this.code;
    }

    public String getTemp(){
        return this.temp;
    }

    public String getType(){
        return this.type;
    }

}
