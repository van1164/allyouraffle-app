import SwiftUI
import shared
import GoogleMobileAds

struct MyPageView: View {
    @StateObject private var observer = MyPageObserver()
    @State private var infoUpdated = false
    @Binding var goRoot : Bool
    var body: some View {
        ZStack {
            if observer.loading {
                LoadingScreen()
            } else {
                if let data = observer.userInfo {
                    NavigationStack {
                        VStack {
                            MyPage(userInfo: data,goRoot: $goRoot,infoUpdated: $infoUpdated)
                            //                                .navigationBarTitle("마이 페이지")
                        }
                    }
                }
            }
        }
        .onAppear {
            observer.initUserInfo()
        }
        .onChange(of: infoUpdated) { update in
            if update{
                observer.getUserInfo()
                infoUpdated = false
            }
        }
        .toast(isPresented: observer.error != nil, message: $observer.error){
            observer.setErrorNull()
        }
    }
}

struct MyPage: View {
    var userInfo: UserInfoResponse
    @Binding var goRoot : Bool
    @Binding var infoUpdated : Bool
    @State private var showSetAddress = false
    @State private var showSetPhoneNumber = false
    @State private var goRaffleHistory = false
    @State private var goChangeAddress = false
    @State private var goChangePhoneNumber = false
    var body: some View {
        NavigationStack {
            ScrollView{
                VStack(alignment: .center) {
                    Text("마이 페이지")
                        .font(.largeTitle)
                        .padding(.top, 40)
                        .foregroundColor(Color("Main"))
                        .bold()
                    
                    
                    // 사용자 프로필 이미지 로딩
                    if(userInfo.profileImageUrl == nil){
                        Image(systemName:"person.circle.fill")
                            .resizable().scaledToFit()
                            .frame(width: 100, height: 100)
                            .clipShape(Circle())
                    }
                    else{
                        AsyncImage(url: URL(string: userInfo.profileImageUrl!)) { image in
                            image
                                .resizable()
                                .scaledToFit()
                        } placeholder: {
                            Image(systemName:"person.circle.fill")
                        }
                        .frame(width: 100, height: 100)
                        .clipShape(Circle())
                    }
                    
                    Spacer(minLength: 16)
                    Text(userInfo.nickname)
                        .font(.title)
                        .fontWeight(.bold)
                        .padding(.top, 8)
                    
                    Spacer(minLength: 16)
                    UserActionButton(label: "주소 변경",imageName: "pencil") {
                        showSetAddress.toggle()
                    }
                    UserActionButton(label: "휴대폰번호 변경",imageName: "pencil") {
                        showSetPhoneNumber.toggle()
                    }
                    UserActionButton(label: "래플 이력",imageName: "list.star") {
                        goRaffleHistory.toggle()
                    }

                    Spacer(minLength: 15)
                    
                    LogoutButton(goRoot: $goRoot)
                    Spacer(minLength: 15)
                    AdBannerView(adUnitID: "ca-app-pub-7372592599478425/7107858150")
                        .frame(width: GADAdSizeBanner.size.width, height: GADAdSizeBanner.size.height)
                    Spacer(minLength: 100)
                    
                    BottomInfo()
                }
                //            .navigationDestination(isPresented: $goRoot){
                //                LoginView().navigationBarBackButtonHidden(true)
                //            }
                .padding()
            }
            .navigationDestination(isPresented: $goRaffleHistory){
                RaffleHistoryView()
            }
            
            .sheet(isPresented: $showSetAddress) {
                ChangeAddressDialog(showSetAddress: $showSetAddress,goChangeAddress: $goChangeAddress, userInfo: userInfo)
            }
            .sheet(isPresented: $showSetPhoneNumber) {
                ChangePhoneDialog(showSetPhoneNumber: $showSetPhoneNumber, goChangePhoneNumber: $goChangePhoneNumber,userInfo: userInfo)
            }
            
            .sheet(isPresented: $goChangeAddress, onDismiss: {
                print("DISMISS")
                infoUpdated = true
            }) {
                SetAddressView(userInfo: userInfo,isModified: true,onFinished: {
                    infoUpdated = true
                })
            }
            .sheet(isPresented: $goChangePhoneNumber, onDismiss: {
                print("DISMISS")
                infoUpdated = true
            }) {
                UserPhoneNumberMainView(isModified: true,onFinished: {
                    infoUpdated = true
                })
            }
        }
    }
}

struct ChangePhoneDialog: View {
    @Binding var showSetPhoneNumber: Bool
    @Binding var goChangePhoneNumber: Bool
    var userInfo: UserInfoResponse
    
    var body: some View {
        VStack {
            Text("휴대폰 번호 변경")
                .font(.system(size: 40))
                .foregroundColor(Color("Main"))
                .bold()
                .padding(.bottom,20)
            
            Text("현재 번호: \(userInfo.phoneNumber ?? "없음")")
                .font(.subheadline)
            
            Text("휴대폰 번호를 변경하시겠습니까?")
            
            HStack {
                Button(action: {
                    showSetPhoneNumber = false
                }) {
                    Text("취소")
                        .bold()
                        .foregroundColor(.white) // 글씨 색 변경
                        .padding()
                        .background(Color.red) // 배경 색
                        .cornerRadius(10) // 모서리 둥글게
                }
                .padding()
                
                Button(action: {
                    showSetPhoneNumber = false
                    goChangePhoneNumber = true
                    
                }) {
                    Text("변경하기")
                        .bold()
                        .foregroundColor(.white) // 글씨 색 변경
                        .padding()
                        .background(Color("Main")) // 배경 색
                        .cornerRadius(10) // 모서리 둥글게
                }
                .padding()
            }
        }
        .padding()
    }
}

struct ChangeAddressDialog: View {
    @Binding var showSetAddress: Bool
    @Binding var goChangeAddress : Bool
    var userInfo: UserInfoResponse
    
    var body: some View {
        VStack {
            Text("주소 변경")
                .font(.system(size: 40))
                .foregroundColor(Color("Main"))
                .bold()
                .padding(.bottom,20)
            
            Text("현재 주소: \(userInfo.address?.address ?? "") \(userInfo.address?.detail ?? "")")
                .font(.subheadline)
            
            Text("우편번호: \(userInfo.address?.postalCode ?? "없음")")
                .font(.subheadline)
            
            Text("주소를 변경하시겠습니까?")
            
            HStack {
                Button(action: {
                    showSetAddress = false
                }) {
                    Text("취소")
                        .bold()
                        .foregroundColor(.white) // 글씨 색 변경
                        .padding()
                        .background(Color.red) // 배경 색
                        .cornerRadius(10) // 모서리 둥글게
                }
                .padding()
                
                Button(action: {
                    showSetAddress = false
                    goChangeAddress = true
                }) {
                    Text("변경하기")
                        .bold()
                        .foregroundColor(.white) // 글씨 색 변경
                        .padding()
                        .background(Color("Main")) // 배경 색
                        .cornerRadius(10) // 모서리 둥글게
                }
                .padding()
                
            }
        }
        .padding()
    }
}

struct UserActionButton: View {
    var label: String
    var imageName : String
    var onClick: () -> Void
    
    var body: some View {
        Button(action: onClick) {
            HStack {
                Text(label)
                    .foregroundColor(.black)
                    .bold()
                Spacer()
                Image(systemName: imageName)
                    .foregroundColor(.black)
            }
            .padding()
            .background(Color.primary.opacity(0.1))
            .cornerRadius(8)
        }
        .padding(.vertical, 8)
    }
}
