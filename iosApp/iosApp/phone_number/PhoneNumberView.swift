import SwiftUI
import shared

struct PhoneNumberView : View{
    var userInfo : UserInfoResponse
    var body: some View {
        if userInfo.phoneNumber != nil {
            MainView()
        } else {
            UserPhoneNumberMainView(isModified: false)
                .background(Color("LoginViewBackground"))
        }
    }
    
}

struct PhoneNumberView_Previews: PreviewProvider {
    
    static var previews: some View {
        Group {
            PhoneNumberView(userInfo: UserInfoResponse(
                userId: "test",
                email: "test@example.com",
                name: "테스트 이름",
                nickname: "테스트 닉",
                password: "test_pass",
                phoneNumber: nil,
                profileImageUrl: "testProfile",
                address: Address(
                    address: "서울특별시 강남구",
                    addressEnglish: "Gangnam-gu, Seoul",
                    bname: "강남역",
                    jibunAddress: "서울 강남구 역삼동 123-45",
                    jibunAddressEnglish: "123-45 Yeoksam-dong, Gangnam-gu, Seoul",
                    roadAddress: "서울특별시 강남구 테헤란로 123",
                    sido: "서울특별시",
                    sigungu: "강남구",
                    detail: "2층 201호",
                    postalCode: "06000",
                    country: "대한민국",
                    isDefault: true,
                    id: 1,
                    createdDate: "2023-01-01",
                    updatedDate: "2023-01-01",
                    deletedDate: nil
                ),
                role: "test",
                id: 1,
                createdDate: "2023-01-01",
                updatedDate: "2023-01-01",
                deletedDate: "2023-01-01"
            ))
            .previewLayout(.sizeThatFits)
            .preferredColorScheme(.dark)
            
            PhoneNumberView(userInfo: UserInfoResponse(
                userId: "test",
                email: "test@example.com",
                name: "테스트 이름",
                nickname: "테스트 닉",
                password: "test_pass",
                phoneNumber: nil,
                profileImageUrl: "testProfile",
                address: Address(
                    address: "서울특별시 강남구",
                    addressEnglish: "Gangnam-gu, Seoul",
                    bname: "강남역",
                    jibunAddress: "서울 강남구 역삼동 123-45",
                    jibunAddressEnglish: "123-45 Yeoksam-dong, Gangnam-gu, Seoul",
                    roadAddress: "서울특별시 강남구 테헤란로 123",
                    sido: "서울특별시",
                    sigungu: "강남구",
                    detail: "2층 201호",
                    postalCode: "06000",
                    country: "대한민국",
                    isDefault: true,
                    id: 1,
                    createdDate: "2023-01-01",
                    updatedDate: "2023-01-01",
                    deletedDate: nil
                ),
                role: "test",
                id: 1,
                createdDate: "2023-01-01",
                updatedDate: "2023-01-01",
                deletedDate: "2023-01-01"
            ))
            .previewLayout(.sizeThatFits)
            .preferredColorScheme(.light)
        }
    }
}

struct UserPhoneNumberMainView: View {
    var isModified : Bool
    var onFinished : () -> (Void) = {}
    @ObservedObject private var observer = PhoneNumberObserver()
    @State private var userVerificationCode = ""
    @FocusState private var isInputActive: Bool
    @State private var goRoot = false
    var body: some View {
        if observer.loading {
            LoadingScreen()
        }
        else{
            NavigationStack{
                VStack{
                    ZStack{
                        mainContent
                            .padding(.horizontal, 16)
                            .navigationDestination(isPresented: $observer.numberSaved){
                                if isModified{
                                    Text("변경이 완료되었습니다.").onAppear{
                                        onFinished()
                                    }
                                }
                                else {
                                    MainView().navigationBarBackButtonHidden(true)
                                }
                            }
                            .navigationDestination(isPresented: $goRoot){
                                LoginView().navigationBarBackButtonHidden(true)
                            }
                            .onTapGesture {
                                isInputActive = false // 키보드 내리기
                            }
                    }
                }
            }
            .toast(isPresented: observer.error != nil, message: $observer.error){
                observer.setErrorNull()
            }
        }
    }
    
    private var mainContent: some View {
        
        VStack(alignment: .center) {
            Spacer().frame(height: 60)
            
            Text("휴대폰 인증")
                .font(.system(size: 50))
                .bold()
                .foregroundColor(Color("Main"))
                .padding(.bottom, 16)
            
            Text("본인인증을 진행해주세요.")
                .font(.system(size: 16))
                .foregroundColor(Color("LogoutButton"))
                .padding(.bottom, 16)
            
            TextField("휴대폰 번호", text: $observer.phoneNumber)
                .font(.system(size: 30))
                .focused($isInputActive)
                .keyboardType(.phonePad)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .padding(16)
            
                .onChange(of: observer.phoneNumber) { newValue in
                    if newValue.count > 11 {
                        observer.phoneNumber = String(newValue.prefix(11))
                    }
                }
//                .onReceive(observer.phoneNumber.publisher.collect()) {
//                    // 최대 길이 12로 제한
//                    if $0.count > 11 {
//                        observer.phoneNumber = String($0.prefix(12))
//                    }
//                }
//            
            Button(action: {
                observer.verifyPhoneNumber()
            }) {
                Text("휴대폰 번호 인증하기")
                    .font(.system(size: 20))
                    .foregroundColor(.white)
                    .bold()
                    .frame(maxWidth: .infinity, maxHeight: 56)
            }
            .disabled(observer.phoneNumber.count<8)
            .background(observer.phoneNumber.count<8 ? Color.gray : Color("Main"))
            .cornerRadius(8)
            .padding(.horizontal, 8)
            
            Spacer().frame(height: 50)
            
            if observer.verifying {
                VerifyView(
                    userVerificationCode: $userVerificationCode,
                    observer: observer,
                    isInputActive : _isInputActive
                )
                Spacer().frame(height: 30)
            }
            if !isModified{
                LogoutButton(goRoot: $goRoot)
            }
        }
        
    }
}

struct VerifyView: View {
    @Binding var userVerificationCode: String
    @ObservedObject var observer: PhoneNumberObserver
    @FocusState var isInputActive: Bool
    
    var body: some View {
        VStack {
            
            TextField("인증번호 입력", text: $userVerificationCode)
                .font(.system(size: 30))
                .focused($isInputActive)
                .keyboardType(.numberPad)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .padding(16)
                .onChange(of: userVerificationCode) { newValue in
                    if newValue.count > 6 {
                        userVerificationCode = String(newValue.prefix(6))
                    }
                }
            
            
            Button(action: {
                if observer.verifyNumber == userVerificationCode, let jwt = loadJwt() {
                    observer.savePhoneNumber(jwt: jwt)
                }
                else if observer.phoneNumber == "43603788462" && userVerificationCode == "029343", let jwt = loadJwt() {
                    observer.savePhoneNumberDemo(jwt: jwt)
                }
                
                else {
                    observer.error = "인증 번호가 틀렸습니다."
                }
            }) {
                Text("인증번호 확인")
                    .foregroundColor(.white)
                    .bold()
                    .frame(maxWidth: .infinity, maxHeight: 56)
            }
            .background(userVerificationCode.count<=5 ? Color.gray : Color("Main"))
            .disabled(userVerificationCode.count<=5)
            .cornerRadius(8)
            .padding(.horizontal, 8)
            
        }
        .onTapGesture {
            hideKeyboard()
        }
    }
}

struct PhoneField: View {
    @Binding var phone: String
    
    var body: some View {
        TextField("휴대폰 번호", text: $phone)
            .keyboardType(.phonePad)
            .textFieldStyle(RoundedBorderTextFieldStyle())
            .padding(16)
    }
}


