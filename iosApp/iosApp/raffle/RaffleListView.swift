import SwiftUI
import shared
import Kingfisher

struct RaffleListView: View {
    @ObservedObject var observer: RaffleObserver
    var isFree: Bool
    var jwt : String
    init(isFree: Bool) {
        let raffleViewModel = RaffleViewModel()
        self.jwt = loadJwt()!
        self.observer = RaffleObserver(isFree: isFree,raffleViewModel: raffleViewModel,jwt: jwt)
        self.isFree = isFree
    }
    
    var body: some View {
        VStack{
            if(observer.loading || observer.ticketCount == -1){
                LoadingScreen()
            }
            else{
                VStack(alignment:.leading) {
                    //                Logo(fontSize: 60)
                    Spacer().frame(height: 10)
                    
                    Banner(message: isFree ? "광고 래플" : "천원 래플", tickets: observer.ticketCount)
                    
                    
                    ScrollView {
                        LazyVStack {
                            ForEach(observer.raffleList, id: \.id) { raffle in
                                ProductCard(raffle: raffle)
                            }
                        }
                    }.refreshable {
                        observer.initRaffle(isFree: isFree)
                        observer.loadTickets(jwt: jwt)
                    }
                    
                }
                .toast(isPresented: observer.error != nil, message: $observer.error){
                    observer.setErrorNull()
                }
            }
        }.onAppear{
            observer.loadTickets(jwt: jwt)
        }
    }
}

struct ProductCard: View {
    var raffle: RaffleResponse
    
    var body: some View {
        let rowHeight: CGFloat = 120
        
        NavigationLink(destination: DetailView(itemId: String(raffle.id), isFree: raffle.isFree)) {
            HStack {
                
                KFImage(URL(string: raffle.item.imageUrl))
                    .placeholder{
                        ProgressView()
                    }
                    .resizable()
                    .aspectRatio(1,contentMode: .fit)
                    .frame(height: rowHeight)
                
                RaffleRightColumn(raffle: raffle, rowHeight: rowHeight)
            }
            .padding(3)
            .background(Color("ComponentBackground"))
            .cornerRadius(8)
            .shadow(radius: 2)
        }
    }
}

struct RaffleRightColumn: View {
    var raffle: RaffleResponse
    var rowHeight: CGFloat
    
    var body: some View {
        VStack(alignment: .leading) {
            Text(raffle.item.name)
                .font(.system(size: 23))
                .fontWeight(.bold)
                .foregroundColor(Color("Text"))
                .padding(.leading, 2)
            
            Spacer().frame(height: 15)
            
            ProgressView(value: Double(raffle.currentCount), total: Double(raffle.totalCount))
                .progressViewStyle(LinearProgressViewStyle(tint: Color("Main")))
                .frame(height: 15)
                .padding(.trailing,15)
                .clipShape(RoundedRectangle(cornerRadius: 12))
            
            Spacer().frame(height: 15)
            
            Text("\(Int((Double(raffle.currentCount) / Double(raffle.totalCount)) * 100))%")
                .font(.custom("Jua", size: 15))
                .foregroundColor(Color("Text"))
                .frame(maxWidth: .infinity, alignment: .trailing)
                .padding(.trailing, 10)
        }
        .frame(height: rowHeight)
        .padding(.leading, 3)
    }
}

struct RaffleListPreview: PreviewProvider {
    static var previews: some View {
        RaffleListView(isFree:true)
    }
}
