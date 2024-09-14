import SwiftUI
import shared
import Kingfisher
import GoogleMobileAds

struct DetailView : View{
    @Environment(\.presentationMode) var presentationMode
    @State private var refreshing = false
    @State private var buttonClicked = false
    
    var itemId: String
    var isFree: Bool
    @ObservedObject var observer : DetailObserver
    init(itemId : String, isFree : Bool){
        self.itemId = itemId
        self.isFree = isFree
        observer = DetailObserver(isFree: isFree, jwt: loadJwt()!, id: itemId)
    }
    
    var body: some View {
        VStack {
            if (observer.loading || observer.raffleDetail == nil) {
                LoadingScreen()
            } else {
                ScrollView {
                    VStack {
                        if let data = observer.raffleDetail {
                            RaffleDetailBody(observer: observer, isFree: isFree)
                        }
                    }
                    .padding(.top, 50)
                }
                .refreshable {
                    observer.initRaffleDetail()
                }
                .overlay(
                    BottomButton(isFree: isFree,observer : observer, itemId: itemId, buttonClickedState: $buttonClicked)
                        .padding(.bottom, 10),
                    alignment: .bottom
                )
            }
        }
        .onAppear{
            observer.initRaffleDetail()
        }
        .toast(isPresented: observer.error != nil, message: $observer.error){
            observer.setErrorNull()
        }
        .onChange(of: buttonClicked) { _ in
            handleButtonClick()
        }
        .onChange(of: observer.raffleEnd) { ended in
            if ended {
                observer.setError(message: "래플이 종료되었습니다. 새로운 래플에 응모해주세요")
                self.presentationMode.wrappedValue.dismiss()
            }
        }
        .alert(isPresented: $observer.purchaseFail){
            Alert(title: Text("응모 실패!"), message: Text("응모에 실패하였습니다."),
                  dismissButton: .default(Text("확인")){
                observer.setFailFalse()
//                observer.loadTickets()
                observer.initRaffleDetail()
            })
        }
        .alert(isPresented: $observer.purchaseSuccess){
            Alert(title: Text("응모 완료!"), message: Text("응모가 완료되었습니다.\n구매내역은 마이페이지에서 확인 가능합니다.\n당첨시 메일로 당첨내역이 발송됩니다."),
                  dismissButton: .default(Text("확인")){
                observer.setSuccessFalse()
                observer.initRaffleDetail()
            })
        }
    }
    
    
    private func handleButtonClick() {
        guard buttonClicked else { return }
        if observer.ticketCount == -1 {
            observer.setError(message: "응모권 조회과정에서 오류가 발생하였습니다.")
            buttonClicked  = false
        } else if observer.ticketCount <= 0 {
            observer.setError(message: "응모권이 부족합니다.")
            buttonClicked = false
        } else {
            observer.purchaseWithTicket{
                buttonClicked = false
            }
        }
    }
}

struct RaffleDetailBody: View {
    @ObservedObject var observer : DetailObserver
    var isFree: Bool
    
    var body: some View {
        if let raffle = observer.raffleDetail {
            VStack {
                KFImage(URL(string: raffle.item.imageUrl))
                    .placeholder{
                        ProgressView()
                    }
                    .resizable()
                    .aspectRatio(1,contentMode: .fit)
                    .scaledToFit()
                    .frame(width: UIScreen.main.bounds.width * 0.7)
                    .padding(.bottom, 30)
                
                
                Text(raffle.item.name)
                    .font(.system(size: 35))
                    .bold()
                    .multilineTextAlignment(.center)
                
                ProgressView(value: Float(raffle.currentCount), total: Float(raffle.totalCount))
                    .progressViewStyle(LinearProgressViewStyle(tint: isFree ? Color.blue : Color.green))
                    .frame(height: 20)
                    .cornerRadius(12)
                
                HStack{
                    Text(String(raffle.currentCount) + "/" + String(raffle.totalCount))
                        .font(.system(size: 25))
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .padding(.leading, 2)
                        .bold()
                    Spacer()
                    Text("\(Int((Float(raffle.currentCount) / Float(raffle.totalCount)) * 100))%")
                        .font(.system(size: 25))
                        .frame(maxWidth: .infinity, alignment: .trailing)
                        .padding(.trailing, 2)
                        .bold()
                }
                Spacer().frame(height: 15)
                
                AdBannerView(adUnitID: "ca-app-pub-7372592599478425/2941354189")
                    .frame(width: GADAdSizeBanner.size.width, height: GADAdSizeBanner.size.height)
                
                ForEach(raffle.item.imageList, id: \.self) { image in
                    KFImage(URL(string: image.imageUrl))
                        .placeholder{
                            ProgressView()
                        }
                        .resizable()
                        .scaledToFit()
                    
                }
                
                Spacer().frame(height: 200)
                
                BottomInfo()
            }
            .padding(.horizontal, 30)
            .onAppear{
                print("TEST")
            }
        }
    }
}



struct BottomButton: View {
    var isFree: Bool
    @ObservedObject var observer: DetailObserver
    var itemId: String
    @Binding var buttonClickedState: Bool
    
    var body: some View {
        VStack {
            Spacer()
            if isFree {
                NewViewAdButton(
                    observer: observer,
                    itemId: itemId,
                    buttonClickedState: $buttonClickedState
                )
            } else {
                NewViewAdButton(
                    observer: observer,
                    itemId: itemId,
                    buttonClickedState: $buttonClickedState
                )
                //                PurchaseButton()
            }
        }
        .padding(5)
    }
}

struct NewViewAdButton: View {
    @ObservedObject var observer: DetailObserver
    var itemId: String
    @Binding var buttonClickedState: Bool
    
    var body: some View {
        Button(action: {
            buttonClickedState = true
            // Add your action here, e.g., viewModel.performAction()
        }) {
            HStack {
                Spacer()
                Text(buttonClickedState ? "응모중..." : "응모 하기")
                    .font(.system(size: 19))
                    .foregroundColor(.white)
                    .bold()
                HStack {
                    SVGView(svgName: "ticket_white",w:5,h:5) // Replace with your custom icon
                        .frame(width: 30, height: 30)
                    Text(String(observer.ticketCount))
                        .font(.system(size: 25))
                        .foregroundColor(.white)
                }
                Spacer()
            }
            .padding()
            .background(buttonClickedState ? Color.gray : Color("Main")) // Change to your desired color
            .cornerRadius(10)
            .frame(maxWidth: .infinity)
            .disabled(buttonClickedState)
        }
        .padding(.horizontal,20)
        .frame(maxWidth: .infinity)
    }
}

struct PurchaseButton: View {
    
    var body: some View {
        // Implement your PurchaseButton UI here
        Text("Purchase Button") // Placeholder
    }
}
