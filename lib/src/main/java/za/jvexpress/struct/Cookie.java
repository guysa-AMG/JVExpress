package za.jvexpress.struct;

public record Cookie(String name,String value){

    public String toCookieFormat(){
        return String.format("%s=%s",this.name,this.value);
    }
}