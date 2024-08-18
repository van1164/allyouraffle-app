import SwiftUI
import Combine
import shared

struct ContentView: View {
	let greet = Greeting().greet()

	var body: some View {
		MainView()
            .font(.custom("Jua",size: 16))
	}
}

struct MainView: View {
    var body: some View {
        TabView {
            RaffleListView(isFree:true)
                .tabItem {
                    Label("광고 래플", systemImage: "play.display")
                }
            RaffleListView( isFree: false)
                .tabItem {
                    Label("천원 래플", systemImage: "dollarsign.circle")
                }
            RaffleListView(isFree: false)
                .tabItem {
                    Label("마이 페이지", systemImage: "person")
                }
        }
    }
}


struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
