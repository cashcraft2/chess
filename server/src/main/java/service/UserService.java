//package service;
//import jdk.jshell.spi.ExecutionControl;
//
//
//public class UserService {
//    record RegisterRequest (String username, String password, String email){}
//    record LoginRequest (String username, String password){}
//    record LogoutRequest (){}
//
//
//    record RegisterResult (String username, String authToken, String message){}
//    record LoginResult (String username, String password, String message) {}
//    record LogoutResult (String message){}
//
//    public UserService(DataAccess dataAccess){
//        this.dataAccess = dataAccess;
//    }
//
//
//    public RegisterResult register(RegisterRequest registerRequest){
//        // 1. Verify the input (make sure it isn't empty or null)
//        if (registerRequest == null){
//            throw new
//        }
//        // 1b Validate the passed in authToken (only applies to some services)
//        // 2. Check to make sure the requested username is not taken
//        // 3. create a new User model object: User u = newUser(...)
//        // 4. Insert new user into the database by calling UserDao.createUser(u)
//        // 5. Login the user (create a new AuthToken model object and insert it into the database)
//        // 6. Create a RegisterResult and return it
//    }
//    public LoginResult login(LoginRequest loginRequest) {}
//    public void logout(LogoutRequest logoutRequest) {}
//}
