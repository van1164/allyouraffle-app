import SwiftUI
import Lottie
import shared
import SVGKit
import UIKit
import GoogleSignIn

struct Logo: View {
    var fontSize: CGFloat = 40
    
    var body: some View {
        HStack {
            Text("AllYouRaffle")
                .font(.custom("Dancing Script", size: fontSize))
                .multilineTextAlignment(.center)
                .fontWeight(.heavy)
                .foregroundColor(Color("Main")) // 테마 색상에 맞춰 변경
                .shadow(color: Color.gray, radius: 3, x: 3, y: 3)
                .padding(16)
        }
        .frame(maxWidth: .infinity)
        .multilineTextAlignment(.center) // 텍스트 중앙 정렬
    }
}


struct Banner: View {
    var message: String
    var tickets: Int
    @Environment(\.colorScheme) var colorScheme
    var body: some View {
        HStack {
            HStack {
                LottieView(animationName: "ad_animation", loopMode: LottieLoopMode.loop)
                    .frame(width: 50, height: 50)
                    .scaleEffect(4.0)
                    .padding(.trailing, 4)
                
                Text(message)
                    .font(.system(size: 30))
                    .bold()
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            
            HStack {
                SVGView(svgName: colorScheme == .dark ? "ticket_white" : "ic_tickets",w:10,h:10) // Replace with your
                    .frame(width: 30, height: 30)
                Text(String(tickets))
                    .font(.system(size: 30))
                    .padding(.leading, 5)
                    .padding(.trailing,5)
                    .bold()
                
            }
        }
        .frame(height: 70)
        .padding(.horizontal,15)
        .padding(.vertical,5)
    }
}

// ImageButton Composable
struct ImageButton: View {
    let image: String
    let action: () -> Void
    
    var body: some View {
        Image(image)
            .resizable()
            .aspectRatio(contentMode: .fit)
            .onTapGesture {
                action()
            }
    }
}


// MainButton Composable
struct MainButton<Content: View>: View {
    let action: () -> Void
    let content: Content
    
    init(action: @escaping () -> Void, @ViewBuilder content: () -> Content) {
        self.action = action
        self.content = content()
    }
    
    var body: some View {
        Button(action: action) {
            content
                .foregroundColor(.white)
                .padding()
        }
        .background(Color.blue) // Update as per your color scheme
        .cornerRadius(8)
    }
}



// LoadingScreen Composable
struct LoadingScreen: View {
    var body: some View {
        ZStack{
            ProgressView("로딩 중...")
                .progressViewStyle(CircularProgressViewStyle(tint: Color("Main")))
                .font(.headline)
        }
    }
}

struct LodingScreenPreview: PreviewProvider {
    static var previews: some View {
        LoadingScreen()
    }
}



struct LogoutButton: View {
    @Binding var goRoot : Bool
    @State private var showDialog = false
    var body: some View {
        VStack {
            Text("다른 계정으로 로그인하기 =>")
                .foregroundColor(Color("LogoutButton").opacity(0.5))
                .bold()
                .onTapGesture {
                    showDialog = true
                }
            
        }
        .alert(isPresented: $showDialog) {
            Alert(
                title: Text(""),
                message: Text("로그아웃 하시겠습니까?"),
                primaryButton: .destructive(Text("로그아웃")) {
                    // Handle logout
                    clearToken()
                    GIDSignIn.sharedInstance.signOut()
                    goRoot = true
                    print("Logged out")
                },
                secondaryButton: .cancel(Text("취소"))
            )
        }
    }
}

// CustomDialog Composable
struct CustomDialog: View {
    let title: String
    let bodyText: String
    let buttonMessage: String
    let onDismiss: () -> Void
    
    var body: some View {
        VStack(spacing: 16) {
            Text(title)
                .font(.headline)
            Text(bodyText)
                .font(.subheadline)
            Button(action: onDismiss) {
                Text(buttonMessage)
                    .foregroundColor(.white)
                    .padding()
                    .frame(maxWidth: .infinity)
                    .background(Color.blue)
                    .cornerRadius(12)
            }
        }
        .padding()
        .background(Color.white)
        .cornerRadius(16)
        .shadow(radius: 8)
    }
}

struct LottieView: UIViewRepresentable {
    let animationName: String
    let loopMode: LottieLoopMode
    
    func makeUIView(context: Context) -> UIView {
        let view = UIView()
        let animationView = LottieAnimationView(name: animationName)
        animationView.loopMode = loopMode
        animationView.contentMode = .scaleAspectFit // 추가된 부분
        animationView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(animationView)
        
        // 애니메이션 뷰의 크기와 제약 조건 설정
        NSLayoutConstraint.activate([
            animationView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            animationView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            animationView.topAnchor.constraint(equalTo: view.topAnchor),
            animationView.bottomAnchor.constraint(equalTo: view.bottomAnchor)
        ])
        
        animationView.play() // 애니메이션 재생
        return view
    }
    
    func updateUIView(_ uiView: UIView, context: Context) {}
}

struct SVGView: UIViewRepresentable {
    var svgName: String
    var w : CGFloat
    var h : CGFloat
    
    func makeUIView(context: Context) -> SVGKFastImageView {
        let svgImage = SVGKImage(named: svgName)
        let svgView = SVGKFastImageView(svgkImage: svgImage)
        //        svgView?.frame.size =
        svgView?.image.size = CGSize(width: w, height: h)
        return svgView!
    }
    
    func updateUIView(_ uiView: SVGKFastImageView, context: Context) {}
}

struct BottomInfo: View {
    var body: some View {
        VStack(alignment: .leading) {
            VStack(alignment: .leading){
                Text("상호 : 올유레플")
                    .font(.system(size: 10))
                    .foregroundColor(Color.black)
                    .padding(1)
                Text("대표자 명 : 김시환")
                    .font(.system(size: 10))
                    .foregroundColor(Color.black)
                    .padding(1)
                Text("사업자 등록 번호 : 580-46-01046")
                    .font(.system(size: 10))
                    .foregroundColor(Color.black)
                    .padding(1)
                Text("문의 이메일 : allyouraffle.info@gmail.com")
                    .font(.system(size: 10))
                    .foregroundColor(Color.black)
                    .padding(1)
                Text("사업장 소재지 : 경기도 용인시 기흥구 서그내로 46-14")
                    .font(.system(size: 10))
                    .foregroundColor(Color.black)
                    .padding(1)
            }.padding(.leading,15)
                .padding(.vertical,5)
        }
        .frame(maxWidth: .infinity, alignment: .leading) // 왼쪽 정렬을 명시적으로 설정
        .background(Color(red: 244/255, green: 244/255, blue: 244/255))
        .cornerRadius(5)
        .padding(.top, 5)
    }
}
