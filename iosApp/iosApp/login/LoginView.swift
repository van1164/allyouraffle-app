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
        GeometryReader { geometry in
            VStack{
                Text("AllYouRaffle")
                    .font(.custom("jua", size: min(geometry.size.width, geometry.size.height) * 0.15))
                    .frame(width: geometry.size.width, alignment: .center)
                    .minimumScaleFactor(0.1) // 글자가 너무 커지지 않도록 최소 크기 설정
                    .lineLimit(1) // 한 줄로 제한
                    .padding(.top,90)
                    .foregroundColor(Color("Main")) // 글자색 설정
                    .shadow(color: .gray, radius: 2, x: 2, y: 2) // 그림자 설정

                Spacer().padding(.horizontal,geometry.size.height)
                
                GoogleLoginBtn(loginObserver: loginObserver,isSignedIn: $isSignedIn)
                    .padding(.bottom ,5)
                    .padding(.horizontal,geometry.size.width * 0.1)
                AppleLoginBtn(loginObserver: loginObserver,isSignedIn: $isSignedIn)
                    .frame(height: geometry.size.height * 0.1)
                    .padding(.bottom ,geometry.size.height * 0.2)
                    .padding(.horizontal,geometry.size.width * 0.1)
            }
            .frame(width: geometry.size.width,height: geometry.size.height)
            .fullScreenCover(isPresented: $isSignedIn) {
                if let jwt = loadJwt(){
                    let userInfo = loginObserver.getUserInfo(jwt:jwt)
                    AddressView(userInfo: userInfo)
                }
            }
        }
    }
    
}

struct AppleLoginBtn : View {
    @ObservedObject var loginObserver : LoginObserver
    @Binding var isSignedIn : Bool
    var body: some View {
        GeometryReader { geometry in
            SignInWithAppleButton(
                .signIn,
                onRequest: { request in
                    // 요청을 사용자 지정합니다.
                    request.requestedScopes = [.fullName, .email]
                },
                onCompletion: { result in
                    switch result {
                    case .success(let authResults):
                        switch authResults.credential {
                        case let appleIDCredential as ASAuthorizationAppleIDCredential:
                            let userId = appleIDCredential.user
                            let email = appleIDCredential.email
                            let fullName = appleIDCredential.fullName
                            
                            if email != nil && fullName != nil {
                                let response = loginObserver.appleSignIn(email: email!, displayName: fullName!.givenName!, id: userId, profileImageUrl: nil, userId: userId)
                                
                                if response == nil{
                                    loginObserver.error = "애플로그인에 실패하였습니다."
                                    return
                                }
                                
                                saveJwt(jwt: response!.jwt)
                                saveRefreshToken(refreshToken: response!.refreshToken)
                                isSignedIn = true
                            }
                            else{
                                
                                
                                print("User ID: \(userId)")
                                print("Email: \(email ?? "No email")")
                                print("Full Name: \(fullName?.description ?? "No name")")
                                let response = loginObserver.appleLogin(userId: userId)
                                
                                if response == nil{
                                    loginObserver.error = "애플로그인에 실패하였습니다."
                                    return
                                }
                                saveJwt(jwt: response!.jwt)
                                saveRefreshToken(refreshToken: response!.refreshToken)
                                isSignedIn = true
                            }
                            // 애플은 userId로 로그인이 필요.
                            
                            
                            
                        default:
                            loginObserver.error = "애플로그인에 실패하였습니다."
                        }
                    case .failure( _):
                        // 오류 처리
                        loginObserver.error = "애플로그인에 실패하였습니다."
                    }
                }
            )
            .toast(isPresented: loginObserver.error != nil, message: $loginObserver.error){
                loginObserver.error = nil
            }
            .signInWithAppleButtonStyle(.black)
        }
    }
}


struct LoginViewPreview: PreviewProvider {
    static var previews: some View {
        LoginView()
    }
}

struct GoogleLoginBtn: View {
    @ObservedObject var loginObserver : LoginObserver
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
                .shadow(radius: 4)
        }
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
                let response = loginObserver.googleSignIn(email: email, displayName: name, id: id, profileImageUrl: nil)
                
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
