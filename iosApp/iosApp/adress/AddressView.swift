import SwiftUI
import WebKit
import shared

struct AddressView : View{
    var userInfo : UserInfoResponse
    
    var body: some View {
        if userInfo.address != nil {
            PhoneNumberView(userInfo: userInfo)
        } else {
            SetAddressView(userInfo :userInfo, isModified: false)
        }
    }
}

struct SetAddressView: View {
    var userInfo: UserInfoResponse
    var isModified : Bool
    var onFinished : () -> (Void) = {}
    @ObservedObject var addressObserver = AddressObserver()
    @State private var showAddressSearch = false
    @State private var showAddressDetail = false
    @State private var goRoot = false
    
    var body: some View {
        NavigationStack {
            VStack {
                InputMain(showDetail: $showAddressSearch)
                    .navigationDestination(isPresented: $showAddressSearch){
                        AddressSearchWebView(onAddressLoaded: { addressInfo in
                            print("Address loaded")
                            addressObserver.setUserAddress(address: addressInfo)
                            showAddressSearch = false
                            showAddressDetail = true
                        })
                    }
                    .navigationDestination(isPresented: $showAddressDetail) {
                        AddressDetail(addressObserver: addressObserver, userAddress: addressObserver.addressInfo, userInfo: userInfo, isModified: isModified,onFinished: onFinished)
                    }
                    .navigationDestination(isPresented: $goRoot){
                        LoginView().navigationBarBackButtonHidden(true)
                    }
                if !isModified{
                    LogoutButton(goRoot: $goRoot)
                }
            }
            //            .fullScreenCover(isPresented: $showAddressSearch) {
            //
            //            }
        }
    }
}


struct InputMain: View {
    @Binding var showDetail: Bool
    
    var body: some View {
        VStack {
            Text("당첨 상품 배송을 위해 주소지 입력이 필요합니다.")
                .font(.largeTitle)
                .foregroundColor(.blue)
                .multilineTextAlignment(.center)
                .padding(.bottom, 16)
            
            Text("배송을 위해 주소를 입력해 주세요.")
                .font(.headline)
                .foregroundColor(.gray)
                .multilineTextAlignment(.center)
                .padding(.bottom, 32)
            
            Button(action: {
                showDetail = true
            }) {
                Text("주소 입력하기")
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.blue)
                    .cornerRadius(8)
            }
            .padding(.bottom, 30)
        }
        .padding(16)
        //        LogoutButton()
    }
}


struct AddressDetail: View {
    @ObservedObject var addressObserver: AddressObserver
    var userAddress: AddressInfo?
    var userInfo : UserInfoResponse
    var isModified : Bool
    var onFinished : () -> (Void)
    
    @State private var showAlert = false
    @State private var detail: String = ""
    @State private var addressSaved = false
    @State private var goRoot = false
    
    var body: some View {
        NavigationStack{
            VStack(alignment: .center) {
                if let userAddress = userAddress {
                    Text("주소 정보")
                        .font(.largeTitle)
                        .bold()
                        .foregroundColor(Color("Main"))
                    
                    Text("주소")
                        .font(.headline)
                        .padding(.top)
                    
                    Text(userAddress.address)
                        .font(.title)
                    
                    Text("우편번호")
                        .font(.headline)
                        .padding(.top)
                    
                    Text(userAddress.postalCode)
                        .font(.title)
                    
                    TextField("상세 주소 입력", text: $detail)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .padding(.top)
                    
                    Button(action: {
                        if let jwt = loadJwt(),addressObserver.saveUserAddress(jwt:jwt) {
                            // 저장 성공 시 전화번호 화면으로 이동
                            addressSaved = true
                        } else {
                            showAlert = true
                        }
                    }) {
                        Text("저장")
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding()
                            .bold()
                            .background(Color("Main"))
                            .cornerRadius(8)
                    }
                    .padding(.top)
                    .alert("실패", isPresented: $showAlert) {
                        Button("확인", role: .cancel) { }
                    } message: {
                        Text("저장에 실패하였습니다.")
                    }
                    if !isModified{
                        LogoutButton(goRoot: $goRoot)
                    }
                    } else {
                    Text("주소 입력에 실패하였습니다.")
                        .foregroundColor(.red)
                }
            }
            .padding()
            .navigationDestination(isPresented: $goRoot){
                LoginView().navigationBarBackButtonHidden(true)
            }
            .navigationDestination(isPresented: $addressSaved){
                if isModified{
                    Text("변경이 완료되었습니다!").onAppear{
                        onFinished()
                    }
                }
                else{
                    PhoneNumberView(userInfo: userInfo)
                }
            }
        }
    }
}

struct AddressSearchWebView: UIViewRepresentable {
    var onAddressLoaded: (AddressInfo) -> Void
    
    func makeUIView(context: Context) -> WKWebView {
        // WKWebViewConfiguration을 먼저 설정
        let preferences = WKWebpagePreferences()
        preferences.allowsContentJavaScript = true
        
        let configuration = WKWebViewConfiguration()
        configuration.defaultWebpagePreferences = preferences
        configuration.userContentController.add(context.coordinator, name: "iosListener")
        clearWebViewCache()
        // 구성된 configuration으로 WKWebView 생성
        let webView = WKWebView(frame: .zero, configuration: configuration)
        webView.navigationDelegate = context.coordinator
        webView.load(URLRequest(url: URL(string: "https://allyouraffle-ios.web.app")!))
        
        return webView
    }
    func clearWebViewCache() {
        let dataStore = WKWebsiteDataStore.default()
        let dataTypes = WKWebsiteDataStore.allWebsiteDataTypes()
        let sinceDate = Date(timeIntervalSince1970: 0)
        
        dataStore.removeData(ofTypes: dataTypes, modifiedSince: sinceDate) {
            print("WebView cache cleared.")
        }
    }
    func updateUIView(_ uiView: WKWebView, context: Context) {}
    
    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }
    
    class Coordinator: NSObject, WKNavigationDelegate, WKScriptMessageHandler {
        var parent: AddressSearchWebView
        
        init(_ parent: AddressSearchWebView) {
            self.parent = parent
        }
        
        func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
            webView.evaluateJavaScript("javascript:sample2_execDaumPostcode();")
        }
        
        func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
            print(message.name == "iosListener")
            print(message.body as! [String : String?])
            if message.name == "iosListener", let body = message.body as? [String: String] {
                print("XXXXXXXXXXXXXXX")
                let addressInfo = AddressInfo(
                    address: body["address"] ?? "",
                    addressEnglish: body["addressEnglish"] ?? "",
                    bname: body["bname"] ?? "",
                    jibunAddress: body["jibunAddress"] ?? "",
                    jibunAddressEnglish: body["jibunAddressEnglish"] ?? "",
                    roadAddress: body["roadAddress"] ?? "",
                    sido: body["sido"] ?? "",
                    sigungu: body["sigungu"] ?? "",
                    postalCode: body["postalCode"] ?? "",
                    country: body["country"] ?? "",
                    detail: nil
                )
                print(addressInfo)
                parent.onAddressLoaded(addressInfo)
            }
        }
    }
}


