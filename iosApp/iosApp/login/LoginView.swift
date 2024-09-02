import SwiftUI
import shared
import AuthenticationServices
import FirebaseCore
import FirebaseAuth
import GoogleSignIn


struct LoginView: View {
    var loginObserver = LoginObserver()
    
    var body: some View {
        if checkJwt(), let jwt = loadJwt(){
            let userInfo = loginObserver.getUserInfo(jwt:jwt)
            AddressView(userInfo: userInfo)
        }else{
            LoginViewBody(loginObserver: loginObserver)
        }
    }
    
    func checkJwt() -> Bool{
        guard let refreshToken = loadRefreshToken() else {
            return false
        }
        return loginObserver.refresh(refreshToken: refreshToken)
        
    }
}

struct LoginViewBody: View {
    //    var authResultLauncher: () -> Void
    @ObservedObject var loginObserver : LoginObserver
    @State private var isSignedIn = false
    var body: some View {
        VStack {
            Text("AllYouRaffle")
                .font(.custom("jua", size: 54))
                .multilineTextAlignment(.center)
                .padding()
                .padding(.top,60)
                .foregroundColor(Color("Main")) // 글자색 설정
                .shadow(color: .gray, radius: 2, x: 2, y: 2) // 그림자 설정
            Spacer()
            GoogleLoginBtn(loginObserver: loginObserver,isSignedIn: $isSignedIn)
            
            AppleLoginBtn().padding(.bottom ,130)
        }
        .padding(.top, 30)
        .fullScreenCover(isPresented: $isSignedIn) {
            if let jwt = loadJwt(){
                let userInfo = loginObserver.getUserInfo(jwt:jwt)
                AddressView(userInfo: userInfo)
            }
        }
    }
    
}

struct AppleLoginBtn : View {
    var body: some View {
        SignInWithAppleButton(.signIn) { request in
            request.requestedScopes = [.fullName, .email]
        } onCompletion: { result in
            switch result {
            case .success(let authResults):
                // Handle successful login
                print("Logged in: \(authResults)")
            case .failure(let error):
                // Handle error
                print("Error: \(error.localizedDescription)")
            }
        }
        .signInWithAppleButtonStyle(.black)
        .frame(height: 60)
        .padding(.leading,60)
        .padding(.trailing,60)
    }
}


struct LoginViewPreview: PreviewProvider {
    static var previews: some View {
        LoginView()
    }
}

struct GoogleLoginBtn: View {
    var loginObserver : LoginObserver
    @Binding var isSignedIn : Bool
    @State private var googleError = false
    @State private var loginState = false
    var body: some View {
        Button(action: {
            signIn()
        }) {
            Image("GoogleLogin")
                .resizable()
                .scaledToFit()
                .frame(width: .infinity)
                .shadow(radius: 4)
        }
        .padding(.leading,60)
        .padding(.trailing,60)
        .alert(isPresented: $googleError){
            Alert(title: Text("구글 로그인 실패"),message: Text("구글 로그인 실패"),dismissButton: .default(Text("확인")))
        }
        
    }
    
    
    func signIn() {
        guard let clientID = FirebaseApp.app()?.options.clientID else { return }
        
        let config = GIDConfiguration(clientID: clientID)
        GIDSignIn.sharedInstance.configuration = config
        
        guard let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
              let rootViewController = windowScene.windows.first(where: { $0.isKeyWindow })?.rootViewController else {
            print("Root ViewController is not found")
            return
        }
        
        GIDSignIn.sharedInstance.signIn(withPresenting: rootViewController) { user, error in
            if let error = error {
                googleError = true
                return
            }
            
            guard let user : GIDSignInResult = user else { return }
            
            //            // 로그인 성공
            //            isUserSignedIn = true
            print("User signed in successfully: \(user.description)")
            if let id = user.user.userID,
               let email = user.user.profile?.email,
               let name = user.user.profile?.name{
                var response = loginObserver.googleSignIn(email: email, displayName: name, id: id, profileImageUrl: nil)
                
                if response == nil{
                    googleError = true
                    return
                }
                
                saveJwt(jwt: response!.jwt)
                saveRefreshToken(refreshToken: response!.refreshToken)
                isSignedIn = true
                loginState = true
            }
            else{
                googleError = true
            }
        }
    }
}
