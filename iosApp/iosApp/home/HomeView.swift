import SwiftUI
import Combine
import shared
import Lottie
import SVGKit
import GoogleMobileAds

struct HomeView: View {
    @ObservedObject var observer: HomeObserver
    @ObservedObject var rewardedViewModel : RewardedViewModel
    @State private var refreshing: Bool = false
    
    var body: some View {
        VStack{
            HomeScreenBody(observer: observer, refreshing: $refreshing, rewardedViewModel:rewardedViewModel).onAppear{
                print(observer.loading)
                print(observer.ticketCount != -1)
                
            }
        }
        .onAppear {
            print(observer.raffleList)
            observer.initHome(jwt: observer.jwt)
        }
        .toast(isPresented: observer.error != nil, message: $observer.error){
            observer.setErrorNull()
        }
    }
}

struct HomeScreenBody: View {
    @ObservedObject var observer: HomeObserver
    @Binding var refreshing: Bool
    @ObservedObject var rewardedViewModel : RewardedViewModel
    
    var body: some View {
        
        ScrollView {
            VStack() {
                TicketView(observer: observer,rewardedViewModel:rewardedViewModel)
                    .padding(.bottom,20)
                    .padding(.top,10)
                AdBannerView(adUnitID: "ca-app-pub-7372592599478425/8910537174")
                    .frame(width: GADAdSizeBanner.size.width, height: GADAdSizeBanner.size.height)
                PopularRankingView(observer: observer)
            }
            .padding(10)
        }.refreshable {
            observer.initRaffle()
        }
        
    }
}

struct PopularRankingView: View {
    @ObservedObject var observer: HomeObserver
    
    var body: some View {
        
        VStack {
            HStack(alignment:.center) {
                LottieView(animationName: "fire", loopMode: LottieLoopMode.loop)
                    .padding(.leading,15)
                    .frame(width: 60)
                Text("인기 래플")
                    .font(.system(size: 30))
                    .fontWeight(.bold)
                    .foregroundColor(Color("Text"))
            }.frame(maxWidth: .infinity, alignment: .leading)
                .frame(height: 50)
                .padding(.vertical,5)
            if observer.loading {
                LoadingScreen()
            }
            else {
                ForEach(observer.raffleList, id: \.id) { raffle in
                    ProductCard(raffle: raffle)
                }
            }
        }   .padding(3)
            .background(Color("ComponentBackground"))
            .cornerRadius(8)
            .shadow(radius: 2)
    }
}

struct TicketView: View {
    @ObservedObject var observer: HomeObserver
    @ObservedObject var rewardedViewModel : RewardedViewModel
    @State private var buttonClicked: Bool = false
    @Environment(\.colorScheme) var colorScheme
    
    var body: some View {
        VStack {
            if observer.ticketCount == -1 {
                LoadingScreen()
            } else {
                VStack(alignment: .center) {
                    Text("현재 응모권")
                        .font(.system(size: 24))
                        .fontWeight(.bold)
                        .foregroundColor(Color("Text"))
                        .padding(16)
                    
                    HStack {
                        SVGView(svgName: colorScheme == .dark ? "ticket_white" : "ic_tickets",w:10,h:10) // Replace with your custom icon
                            .frame(width: 40, height: 40)
                        Text("\(observer.ticketCount)")
                            .font(.system(size: 35))
                            .foregroundColor(Color("Text"))
                            .padding(.leading, 10)
                            .bold()
                    }
                    .frame(maxWidth: .infinity, alignment: .center)
                    .padding(.bottom,15)
                    
                    Button(action: {
                        if !buttonClicked {
                            if rewardedViewModel.rewardedAd != nil {
                                buttonClicked = true
                            } else {
                                rewardedViewModel.loadAd {
                                    print("fail!")
                                    rewardedViewModel.adLoading = false
                                    buttonClicked = false
                                    observer.setError(message: "광고가 모두 소진되었습니다.. ㅠㅠ 10분정도 이후에 시도해주세요.")
                                } success: {
                                    print("success")
                                    rewardedViewModel.adLoading = false
                                }
                                buttonClicked = true
                                rewardedViewModel.adLoading = true
                            }
                        }
                    }) {
                        Text(buttonClicked ? "로딩중..." : "광고 시청 후 응모권 획득")
                            .padding()
                            .font(.system(size:20))
                            .fontWeight(.bold)
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .background(buttonClicked ? Color.gray : Color("Main"))
                            .cornerRadius(12)
                    }
                    .disabled(buttonClicked)
                    .frame(maxWidth: .infinity, alignment: .center).padding(.bottom,10).padding(.horizontal,20)
                    //                .padding(10)
                    
                }
                .frame(maxWidth: .infinity, alignment: .center)
            }
        }
        .onChange(of: buttonClicked) { _ in checkAndExecute() }
        .onChange(of: rewardedViewModel.rewardedAd) { _ in checkAndExecute() }
        .onChange(of: rewardedViewModel.adLoading) { _ in checkAndExecute() }
        .frame(maxWidth: .infinity)
        .padding(3)
        .background(Color("ComponentBackground"))
        .cornerRadius(8)
        .shadow(radius: 5)
        .onAppear{
            rewardedViewModel.loadAd {
                print("fail!")
                rewardedViewModel.adLoading = false
                observer.setError(message: "광고가 모두 소진되었습니다.. ㅠㅠ 10분정도 이후에 시도해주세요.")
            } success: {
                print("success")
                rewardedViewModel.adLoading = false
            }
            rewardedViewModel.adLoading = true
        }
    }
    
    private func checkAndExecute() {
        if buttonClicked && rewardedViewModel.rewardedAd != nil && !rewardedViewModel.adLoading {
            rewardedViewModel.showAd {
                observer.ticketPlusOne(jwt: loadJwt()!)
                buttonClicked = false
                rewardedViewModel.loadAd {
                    rewardedViewModel.adLoading = false
                    buttonClicked = false
                    observer.setError(message: "광고가 모두 소진되었습니다.. ㅠㅠ 10분정도 이후에 시도해주세요.")
                } success: {
                    rewardedViewModel.adLoading = false
                }
            }
        }
    }
}


struct HomeViewPreview: PreviewProvider {
    static var previews: some View {
        Group {
            HomeView(observer: HomeObserver(viewModel: HomeViewModel(),jwt: loadJwt()!),rewardedViewModel: RewardedViewModel())
                .previewLayout(.sizeThatFits)
                .preferredColorScheme(.dark)
            
            HomeView(observer: HomeObserver(viewModel: HomeViewModel(),jwt: loadJwt()!),rewardedViewModel: RewardedViewModel())
                .previewLayout(.sizeThatFits)
                .preferredColorScheme(.light)
        }
    }
}
