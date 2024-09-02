import SwiftUI
import Combine
import shared
import Lottie
import SVGKit

struct HomeView: View {
    @ObservedObject var observer: HomeObserver
    @State private var refreshing: Bool = false
    
    var body: some View {
        VStack{
//            NavigationView{
                if observer.loading || observer.ticketCount == -1{
                    LoadingScreen()
                }
                else {
                    HomeScreenBody(observer: observer, refreshing: $refreshing).onAppear{
                        print(observer.loading)
                        print(observer.ticketCount != -1)
                    }
                }
//            }
        }
        .onAppear {
            print("XXXXXXXXX")
            print(observer.raffleList)
            observer.initHome(jwt: observer.jwt)
        }
        .toast(isPresented: observer.error != nil, message: $observer.error)
    }
}

//
struct HomeScreenBody: View {
    @ObservedObject var observer: HomeObserver
    @Binding var refreshing: Bool
    
    var body: some View {
        ScrollView {
            VStack() {
                TicketView(observer: observer)
                    .padding(.bottom,20)
                    .padding(.top,10)
                PopularRankingView(observer: observer)
            }
            .padding(10)
            //            .pullToRefresh(isShowing: $refreshing) {
            //                observer.refresh()
            //            }
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
                //                    .frame(width: 50, height: 50)
                Text("인기 래플")
                    .font(.system(size: 30))
                    .fontWeight(.bold)
                    .foregroundColor(.black)
            }.frame(maxWidth: .infinity, alignment: .leading)
                .frame(height: 50)
                .padding(.vertical,5)
            
            ForEach(observer.raffleList, id: \.id) { raffle in
                ProductCard(raffle: raffle)
            }
        }   .padding(3)
            .background(Color.white)
            .cornerRadius(8)
            .shadow(radius: 2)
    }
}

struct TicketView: View {
    @ObservedObject var observer: HomeObserver
    @State private var buttonClicked: Bool = false
    
    var body: some View {
        VStack {
            VStack(alignment: .center) {
                Text("현재 응모권")
                    .font(.system(size: 24))
                    .fontWeight(.bold)
                    .foregroundColor(.black)
                    .padding(16)
                
                HStack {
                    SVGView(svgName: "ic_tickets",w:10,h:10) // Replace with your custom icon
                        .frame(width: 40, height: 40)
                    Text("\(observer.ticketCount)")
                        .font(.system(size: 35))
                        .foregroundColor(.black)
                        .padding(.leading, 10)
                }
                .frame(maxWidth: .infinity, alignment: .center)
                .padding(.bottom,15)
                
                Button(action: {
                    buttonClicked.toggle()
                    if buttonClicked {
                        // observer.showAd()
                    }
                }) {
                    Text("광고 시청 후 응모권 획득")
                        .padding()
                        .font(.system(size:20))
                        .fontWeight(.bold)
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .background(Color("Main"))
                        .cornerRadius(12)
                }
                .frame(maxWidth: .infinity, alignment: .center).padding(.bottom,10).padding(.horizontal,20)
                //                .padding(10)
                
            }
            .frame(maxWidth: .infinity, alignment: .center)
        }
        .frame(maxWidth: .infinity)
        .padding(3)
        .background(Color.white)
        .cornerRadius(8)
        .shadow(radius: 5)
    }
}

//
//// LottieView and LoadingView implementations
//struct LottieView: UIViewRepresentable {
//    var name: String
//    var loop: Bool = false
//
//    func makeUIView(context: Context) -> LottieAnimationView {
//        let view = LottieAnimationView(name: name)
//        view.loopMode = loop ? .loop : .playOnce
//        view.play()
//        return view
//    }
//
//    func updateUIView(_ uiView: LottieAnimationView, context: Context) {}
//}
//
//struct LoadingView: View {
//    var body: some View {
//        ProgressView("Loading...")
//            .progressViewStyle(CircularProgressViewStyle())
//            .frame(maxWidth: .infinity, maxHeight: .infinity)
//    }
//}
//
//// Pull to Refresh Modifier
//extension View {
//    func pullToRefresh(isShowing: Binding<Bool>, action: @escaping () -> Void) -> some View {
//        self.modifier(PullToRefreshModifier(isShowing: isShowing, action: action))
//    }
//}
//
//struct PullToRefreshModifier: ViewModifier {
//    @Binding var isShowing: Bool
//    let action: () -> Void
//
//    func body(content: Content) -> some View {
//        content
//            .overlay(
//                Group {
//                    if isShowing {
//                        ProgressView()
//                            .progressViewStyle(CircularProgressViewStyle())
//                            .padding(.top, 8)
//                    }
//                }
//            )
//            .onChange(of: isShowing) { newValue in
//                if newValue {
//                    DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
//                        action()
//                        isShowing = false
//                    }
//                }
//            }
//    }
//}
struct HomeViewPreview: PreviewProvider {
    static var previews: some View {
        HomeView(observer: HomeObserver(viewModel: HomeViewModel(),jwt: loadJwt()!))
    }
}
