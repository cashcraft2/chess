package service;

public class ClearService {

    public static record ClearRequest () {}
    public static record ClearResult(boolean success, String message) {}

    public ClearResult clear(ClearRequest clearRequest){
        //implement clear of database

    }
}
