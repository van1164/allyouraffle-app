import Firebase
import GoogleSignIn
import shared


class LoginObserver: ObservableObject {
    private var loginViewModel = LoginViewModel()
    @Published var error : String? = nil
    
    init(){
        loginViewModel.error.subscribe{[weak self] err in
            self?.error = err as! String?
        }
    }
    
    func googleSignIn(email : String, displayName : String, id : String,profileImageUrl : String?) -> MobileLoginResponse?{
        return loginViewModel.googleLogin(email:email,displayName:displayName,id:id, profileImageUrl:profileImageUrl)
    }
    
    func appleSignIn(email : String, displayName : String, id : String,profileImageUrl : String?, userId : String) -> MobileLoginResponse?{
        return loginViewModel.appleRegister(email:email,displayName:displayName,id:id, profileImageUrl:profileImageUrl, userId:userId)
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
    
    func appleLogin(userId: String) -> MobileLoginResponse?{
        return loginViewModel.appleLogin(userId:userId)
    }
    
}
