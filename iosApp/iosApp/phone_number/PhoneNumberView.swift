import SwiftUI
import shared

struct PhoneNumberView : View{
    var userInfo : UserInfoResponse
    //    @ObservableObject private var addressObserver = AddressObserver()
    
    var body: some View {
        if userInfo.phoneNumber != nil {
            MainView()
        } else {
            UserPhoneNumberMainView()
        }
    }
    
}

struct UserPhoneNumberMainView: View {
    @ObservedObject private var observer = PhoneNumberObserver()
    @State private var userVerificationCode = ""
    @FocusState private var isInputActive: Bool
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
                            .background(Color.white)
                            .navigationDestination(isPresented: $observer.numberSaved){
                                MainView().navigationBarBackButtonHidden(true)
                            }
                            .onTapGesture {
                                isInputActive = false // 키보드 내리기
                            }
                    }
                }
            }
            .toast(isPresented: observer.error != nil, message: $observer.error)
        }
    }
    
    private var mainContent: some View {
        
        VStack(alignment: .center) {
            Spacer().frame(height: 60)
            
            Text("휴대폰 인증")
                .font(.system(size: 40))
                .bold()
                .foregroundColor(Color("Main"))
                .padding(.bottom, 16)
            
            Text("본인인증을 진행해주세요.")
                .font(.system(size: 16))
                .foregroundColor(Color(hex: "#424242"))
                .padding(.bottom, 32)
            
            TextField("휴대폰 번호", text: $observer.phoneNumber)
                .font(.system(size: 30))
                .focused($isInputActive)
                .keyboardType(.phonePad)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .padding(16)
                .onReceive(observer.phoneNumber.publisher.collect()) {
                    // 최대 길이 12로 제한
                    if $0.count > 11 {
                        observer.phoneNumber = String($0.prefix(12))
                    }
                }
            
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
            LogoutButton()
            
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
                } else {
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


