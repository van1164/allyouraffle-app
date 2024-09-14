import SwiftUI
import Combine
import shared

struct ContentView: View {
    let greet = Greeting().greet()

    var body: some View {
        LoginView()
//        MainView()
//            .font(.custom("jua",size: 16))
    }
}
@MainActor
struct MainView: View {
    var homeViewModel = HomeViewModel()
    var rewardedViewModel = RewardedViewModel()
    @State var goRoot = false
    var body: some View {
        NavigationStack{
            TabView {
                let jwt = loadJwt()
                HomeView(observer: HomeObserver(viewModel:homeViewModel,jwt:jwt!),rewardedViewModel:rewardedViewModel)
                    .tabItem {
                        Label("홈", systemImage: "house")
                    }
                
                RaffleListView(isFree:true)
                    .tabItem {
                        Label("광고 래플", systemImage: "play.display")
                    }
                
                MyPageView(goRoot: $goRoot)
                    .tabItem {
                        Label("마이 페이지", systemImage: "person")
                    }
            }.navigationDestination(isPresented: $goRoot){
                LoginView().navigationBarBackButtonHidden(true)
            }
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
