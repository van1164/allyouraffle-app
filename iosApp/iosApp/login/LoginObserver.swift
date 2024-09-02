import Firebase
import GoogleSignIn
import shared


class LoginObserver: ObservableObject {
    private var loginViewModel = LoginViewModel()
    
    func googleSignIn(email : String, displayName : String, id : String,profileImageUrl : String?) -> MobileLoginResponse?{
        return loginViewModel.googleLogin(email:email,displayName:displayName,id:id, profileImageUrl:profileImageUrl)
    }
    
    func refresh(refreshToken : String) -> Bool{
        do{
            guard let jwtResponse = loginViewModel.refresh(refreshToken: refreshToken) else {
                return false
            }
            
            saveJwt(jwt: jwtResponse.jwt)
            return true
        } catch {
            return false
        }
    }
    
    func getUserInfo(jwt:String) -> UserInfoResponse{
        return loginViewModel.getUserInfo(jwt:jwt)
    }
}
