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

struct MainView: View {
    var body: some View {
        NavigationView{
            TabView {
                let jwt = loadJwt()
                HomeView(observer: HomeObserver(viewModel:HomeViewModel(),jwt:jwt!))
                    .tabItem {
                        Label("홈", systemImage: "house")
                    }
                
                RaffleListView(isFree:true)
                    .tabItem {
                        Label("광고 래플", systemImage: "play.display")
                    }
                
                RaffleListView(isFree: false)
                    .tabItem {
                        Label("마이 페이지", systemImage: "person")
                    }
            }
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
